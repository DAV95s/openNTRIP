package org.adv25.openNTRIP.Servers;

import org.adv25.openNTRIP.Clients.Client;
import org.adv25.openNTRIP.Databases.Models.ReferenceStationModel;
import org.adv25.openNTRIP.Network.IWork;
import org.adv25.openNTRIP.Network.Socket;
import org.adv25.openNTRIP.Tools.Analyzer;
import org.adv25.openNTRIP.Tools.Decoders.IDecoder;
import org.adv25.openNTRIP.Tools.Decoders.RAW;
import org.adv25.openNTRIP.Tools.Decoders.RTCM_3X;
import org.adv25.openNTRIP.Tools.MessagePack;
import org.adv25.openNTRIP.Tools.NMEA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * All reference station from the database, contains in memory and waiting for connect base receiver.
 * After successful connect, setSocket() method is called.
 */
public class ReferenceStation extends ReferenceStationUpdater implements IWork {
    final static private Logger logger = LogManager.getLogger(ReferenceStation.class.getName());

    private final CopyOnWriteArrayList<Client> subscribers = new CopyOnWriteArrayList<>();
    private final ByteBuffer buffer = ByteBuffer.allocate(32768);

    private Socket socket;
    private Analyzer analyzer;

    private long connectTimeMark;
    private long acceptBytes;

    public boolean available = false;
    //accepted bytes pack
    private final Queue<byte[]> dataBuffer = new ArrayDeque<>();

    //The decoder can change.
    private IDecoder decoder = new RTCM_3X();

    public ReferenceStation(ReferenceStationModel model) {
        this.model = model;
        timer.schedule(updateModel, 10_000, 10_000);
        refStations.put(model.getName(), this);
    }

    public void setSocket(Socket socket) throws IOException {
        if (this.socket == null || !this.socket.isRegistered()) {
            dao.setOnline(model);
            this.decoder = new RTCM_3X();
            this.analyzer = new Analyzer(this);
            this.acceptBytes = 0;
            this.connectTimeMark = System.currentTimeMillis();
            this.socket = socket;
            this.available = true;
            logger.info("Connection " + socket.socketId + " (" + model.getName() + ") was logged in.");
        } else {
            throw new IOException("Connection " + socket.socketId + " " + model.getName() + " socket already taken.");
        }
    }

    /**
     * Close connection.
     */
    @Override
    public void close() {
        try {
            dao.setOffline(model);
            this.available = false;
            this.analyzer.close();
            this.socket.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public void readSelf() throws IOException {
        int count = 0;
        this.buffer.clear();

        if (!this.socket.endOfStreamReached) {
            count = this.socket.read(buffer);
        } else {
            throw new IOException("Connection " + socket.socketId + " RefSt: " + model.getName() + " end of stream reached.");
        }

        logger.info("Connection " + socket.socketId + " RefSt: " + model.getName() + " accept " + count);

        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        dataBuffer.add(bytes);
    }

    @Override
    public void run() {
        if (dataBuffer.peek() == null)
            return;

        try {
            byte[] bytes = dataBuffer.poll();

            MessagePack messagePack = decoder.separate(ByteBuffer.wrap(bytes));

            logger.info(model.getName() + " decode: " + messagePack.toString());

            analyzer.analyze(messagePack);

            sendMessageToClients(messagePack);
        } catch (IllegalArgumentException e) {
            logger.info(model.getName() + " error " + decoder.getType());
            if (decoder instanceof RTCM_3X) {
                decoder = new RAW();
                super.rawDataType();
                logger.info(model.getName() + " set new decoder " + decoder.getType());
            }
        }
    }

    private void sendMessageToClients(MessagePack messagePack) {
        ByteBuffer localBuffer = messagePack.getFullBytes();

        for (Client client : subscribers) {
            localBuffer.flip();
            try {
                client.write(localBuffer);
            } catch (IOException e) {
                client.close();
            }
        }
    }

    // if reference station has remover from database.
    @Override
    protected void remove() {
        this.updateModel.cancel();
        this.close();
        refStations.remove(model.getName());
    }

    /**
     * Distance from reference station to client position.
     * Used for get nearest reference station for client receiver.
     *
     * @param position
     * @return float
     * @throws NullPointerException
     */
    public float distance(NMEA.GPSPosition position) throws NullPointerException {
        if (model.getLla() == null) {
            throw new NullPointerException("Reference station not have coordinate position!");
        }
        if (position == null) {
            throw new NullPointerException("Client not have position!");
        }

        return model.getLla().distance(position);
    }

    /**
     * Check password reference stations.
     *
     * @param password
     * @return boolean
     */
    public boolean checkPassword(String password) {
        return model.getPassword().equals(password);
    }

    public void addClient(Client client) {
        subscribers.add(client);
    }

    public void removeClient(Client client) {
        subscribers.remove(client);
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

}