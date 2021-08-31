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
import org.dav95s.openNTRIP.Tools.Observer.IObservable;
import org.dav95s.openNTRIP.Tools.Observer.IObserver;
import org.dav95s.openNTRIP.Tools.RTCMStream.Analyzer;
import org.dav95s.openNTRIP.Tools.RTCMStream.MessagesPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

/**
 * All reference station from the database, contains in memory and waiting for connect base receiver.
 * After successful connect, setSocket() method is called.
 */
public class ReferenceStation implements INetworkHandler, IObservable {
    static final private Logger logger = LoggerFactory.getLogger(ReferenceStation.class.getName());
    static final private int BYTE_BUFFER_SIZE = 32768;
    static final private ByteBufferPool bufferPool = new ByteBufferPool();

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
        this.analyzer.close();
        this.socket.close();
        this.model.setOnline(false);
        this.model.update();
    }

    //The reference station has removed from database.
    protected void remove() {
        this.model.setOnline(false);
        this.socket.close();
        this.close();
    }

    public void readChannel() throws IOException {
        ByteBuffer buffer = bufferPool.take(BYTE_BUFFER_SIZE);

        if (socket.endOfStreamReached)
            throw new IOException(socket + " end of stream reached.");

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
            MessagesPack messagePack = decoder.separate(buffer);
            this.model.replaceCoordinates(messagePack);
            this.notifyObservers(messagePack.getByteBuffer());
            this.analyzer.analyze(messagePack);

            logger.debug("Name: " + model.getName() + " read;" + buffer.limit() + "; messages" + messagePack + "; queue_size");

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


    public void authentication(Socket socket, HttpParser httpParser) throws IOException {
        //mb password wrong
        if (!this.model.getPassword().equals(httpParser.getParam("PASSWORD"))) {
            throw new IOException(socket.toString() + " ref station " + this + " Bad password.");
        }
        //mb station in time connect
        if (!this.setSocket(socket)) {
            throw new IOException(socket.toString() + " station " + this + " already use.");
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
        this.model.updateOnlineStatus(true);
    }

    /**
     * Distance from reference station to client position.
     */
    public float distance(User user) {
        NMEA.GPSPosition position = user.getPosition();
        if (!this.model.getPosition().isSet()) {
            throw new IllegalStateException("Reference station does not have coordinates " + this);
        }

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(position.lat - model.getPosition().lat);
        double dLng = Math.toRadians(position.lon - model.getPosition().lon);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(model.getPosition().lat)) * Math.cos(Math.toRadians(position.lat)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        System.out.println("DISTANCE " + model.getName() + " " + (float) (earthRadius * c));
        return (float) (earthRadius * c);
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

    private final ArrayList<IObserver> observers = new ArrayList<>();

    @Override
    public void registerObserver(IObserver o) {
        logger.info(model.getName() + " adds observer: " + o.toString());
        observers.add(o);
    }

    @Override
    public void removeObserver(IObserver o) {
        logger.info(model.getName() + " removes observer: " + o.toString());
        observers.remove(o);
    }

    @Override
    public void notifyObservers(ByteBuffer buffer) {
        observers.forEach(o -> o.notify(this, buffer));
    }

}