package org.dav95s.openNTRIP.Servers;

import com.github.pbbl.heap.ByteBufferPool;
import org.dav95s.openNTRIP.Clients.User;
import org.dav95s.openNTRIP.Databases.Models.ReferenceStationModel;
import org.dav95s.openNTRIP.Network.INetworkHandler;
import org.dav95s.openNTRIP.Network.Socket;
import org.dav95s.openNTRIP.Tools.Decoders.DecoderRTCM3;
import org.dav95s.openNTRIP.Tools.Decoders.IDecoder;
import org.dav95s.openNTRIP.Tools.Decoders.RAW;
import org.dav95s.openNTRIP.Tools.HttpParser;
import org.dav95s.openNTRIP.Tools.NMEA;
import org.dav95s.openNTRIP.Tools.RTCMStream.Analyzer;
import org.dav95s.openNTRIP.Tools.RTCMStream.MessagePack;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * All reference station from the database, contains in memory and waiting for connect base receiver.
 * After successful connect, setSocket() method is called.
 */
public class ReferenceStation implements INetworkHandler {
    static final private Logger logger = LoggerFactory.getLogger(ReferenceStation.class.getName());
    static final private int BYTE_BUFFER_SIZE = 32768;
    static final private ByteBufferPool bufferPool = new ByteBufferPool();

    final private CopyOnWriteArrayList<User> subscribers = new CopyOnWriteArrayList<>();
    final private Queue<ByteBuffer> dataQueue = new ArrayDeque<>();
    final private ReferenceStationModel model;

    private Socket socket;
    private Analyzer analyzer;
    private IDecoder decoder = new DecoderRTCM3();

    public ReferenceStation(ReferenceStationModel model) {
        this.model = model;
    }

    //connect lost
    @Override
    public void close() {
        try {
            this.analyzer.close();
            this.socket.close();
            this.model.setOnline(false);
            this.model.update();
        } catch (IOException | SQLException e) {
            logger.error(e.getMessage());
        }
    }

    //The reference station has removed from database.
    protected void remove() throws IOException {
        this.model.setOnline(false);
        this.socket.close();
        this.close();
    }

    public void readChannel() throws IOException {
        ByteBuffer buffer = bufferPool.take(BYTE_BUFFER_SIZE);

        if (socket.endOfStreamReached)
            throw new IOException(socket.toString() + " end of stream reached.");

        this.socket.read(buffer);
        buffer.flip();
        this.dataQueue.add(buffer);
    }

    @Override
    public void run() {
        if (this.dataQueue.peek() == null)
            return;

        ByteBuffer buffer = dataQueue.poll();

        try {
            MessagePack messagePack = decoder.separate(buffer);
            this.model.replaceCoordinates(messagePack);
            this.sendMessagesToClients(messagePack);
            this.analyzer.analyze(messagePack);

            if (logger.isDebugEnabled()) {
                JSONObject object = new JSONObject();
                object.put("name", model.getName());
                object.put("read", buffer.limit());
                object.put("messages", messagePack.toString());
                object.put("queue_size", this.dataQueue.size());
                object.put("fixPosition", model.isFixPosition());
                logger.debug(object.toString());
            }

        } catch (IllegalArgumentException e) {
            logger.info(model.getName() + " wrong decoder: " + decoder.getType());
            changeDecoder();
        }
        bufferPool.give(buffer);
    }

    private void changeDecoder() {
        if (this.decoder instanceof DecoderRTCM3) {
            this.decoder = new RAW();
            logger.info(model.getName() + " try new decoder: " + decoder.getType());
        }
    }

    private void sendMessagesToClients(MessagePack messagePack) {
        ByteBuffer localBuffer = messagePack.getByteBuffer();

        for (User user : subscribers) {
            localBuffer.flip();
            try {
                user.write(localBuffer);
            } catch (IOException e) {
                user.close();
            }
        }
    }

    public void referenceStationAuthentication(Socket socket, HttpParser httpParser) throws IOException {

        //mb password wrong
        if (!this.model.getPassword().equals(httpParser.getParam("PASSWORD"))) {
            throw new IOException(socket.toString() + " ref station " + this.toString() + " Bad password.");
        }

        //mb station in time connect
        if (!this.setSocket(socket)) {
            throw new IOException(socket.toString() + " station " + this.toString() + " already use.");
        }

        socket.sendOkMessage();
    }

    private boolean setSocket(Socket socket) {
        if (this.socket == null || !this.socket.isRegistered()) {
            socketInit(socket);
            logger.info(socket.toString() + model.getName() + " logged in.");
            return true;
        } else {
            return false;
        }
    }

    private void socketInit(Socket socket) {
        this.decoder = new DecoderRTCM3();
        this.analyzer = new Analyzer(this);
        this.socket = socket;
        this.model.setOnline(true);
    }

    /**
     * Distance from reference station to client position.
     * Used for get nearest reference station for client receiver.
     *
     * @param position
     * @return float
     * @throws IllegalArgumentException
     */
    public float distance(NMEA.GPSPosition position) {
        if (!this.model.getPosition().isSet()) {
            throw new IllegalStateException("Reference station does not have coordinates " + this.toString());
        }

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(position.lat - model.getPosition().lat);
        double dLng = Math.toRadians(position.lon - model.getPosition().lon);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(model.getPosition().lat)) * Math.cos(Math.toRadians(position.lat)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (float) (earthRadius * c);
    }

    public void addClient(User user) {
        subscribers.add(user);

        if (logger.isDebugEnabled()) {
            JSONObject object = new JSONObject();
            object.put("add client", user);
            object.put("contains", Arrays.toString(subscribers.toArray()));
            logger.debug(object.toString());
        }
    }

    public void removeClient(User user) {
        subscribers.remove(user);

        if (logger.isDebugEnabled()) {
            JSONObject object = new JSONObject();
            object.put("remove client", user);
            object.put("contains", Arrays.toString(subscribers.toArray()));
            logger.debug(object.toString());
        }
    }

    public ReferenceStationModel getModel() {
        return this.model;
    }

    public String getName() {
        return model.getName();
    }

    public int getId() {
        return model.getId();
    }

    public boolean isOnline() {
        return this.model.getOnlineStatus();
    }

    @Override
    public String toString() {
        return this.model.toString();
    }

    public void refresh() throws SQLException {
        model.read();
        model.readReplaceCoordinates();
    }
}