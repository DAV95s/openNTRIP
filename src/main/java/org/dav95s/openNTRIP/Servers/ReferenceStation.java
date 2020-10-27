package org.dav95s.openNTRIP.Servers;

import org.dav95s.openNTRIP.Clients.Client;
import org.dav95s.openNTRIP.Databases.DAO.ReferenceStationDAO;
import org.dav95s.openNTRIP.Databases.Models.ReferenceStationModel;
import org.dav95s.openNTRIP.Network.IWork;
import org.dav95s.openNTRIP.Network.Socket;
import org.dav95s.openNTRIP.Tools.Analyzer;
import org.dav95s.openNTRIP.Tools.Decoders.IDecoder;
import org.dav95s.openNTRIP.Tools.Decoders.RAW;
import org.dav95s.openNTRIP.Tools.Decoders.RTCM_3X;
import org.dav95s.openNTRIP.Tools.HttpParser;
import org.dav95s.openNTRIP.Tools.MessagePack;
import org.dav95s.openNTRIP.Tools.NMEA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * All reference station from the database, contains in memory and waiting for connect base receiver.
 * After successful connect, setSocket() method is called.
 */
public class ReferenceStation implements IWork {
    final static private Logger logger = LogManager.getLogger(ReferenceStation.class.getName());

    final private CopyOnWriteArrayList<Client> subscribers = new CopyOnWriteArrayList<>();
    final private ByteBuffer buffer = ByteBuffer.allocate(32768);

    private Socket socket;
    private Analyzer analyzer;
    private IDecoder decoder = new RTCM_3X();

    public ReferenceStation(ReferenceStationModel model) {
        this.model = model;
        timer.schedule(updateModel, 10_000, 10_000);
        refStations.put(model.getName(), this);
    }

    public boolean setSocket(Socket socket) {
        if (this.socket == null || !this.socket.isRegistered()) {
            //new ReferenceStationDAO().setOnlineStatus(model);
            this.decoder = new RTCM_3X();
            this.analyzer = new Analyzer(this);
            this.socket = socket;
            this.model.setOnline(true);
            logger.info(socket.toString() + model.getName() + " logged in.");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Close connection.
     */
    @Override
    public void close() {
        try {
            //new ReferenceStationDAO().setOfflineStatus(model);
            this.model.setOnline(false);
            this.analyzer.close();
            this.socket.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private final Queue<byte[]> dataQueue = new ArrayDeque<>();

    public void readSelf() throws IOException {
        this.buffer.clear();

        if (socket.endOfStreamReached)
            throw new IOException(socket.toString() + " end of stream reached.");

        int count = this.socket.read(buffer);
        logger.info(socket.toString() + "RefSt: " + model.getName() + " accept " + count);

        this.buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        this.buffer.get(bytes);
        this.dataQueue.add(bytes);
    }

    @Override
    public void run() {
        if (this.dataQueue.peek() == null)
            return;

        byte[] bytes = dataQueue.poll();

        try {
            MessagePack messagePack = decoder.separate(ByteBuffer.wrap(bytes));
            logger.info(model.getName() + " decode: " + messagePack.toString());
            this.analyzer.analyze(messagePack);
            this.sendMessageToClients(messagePack);

        } catch (IllegalArgumentException e) {
            logger.info(model.getName() + " error " + decoder.getType());
            if (this.decoder instanceof RTCM_3X) {
                this.decoder = new RAW();
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

    public static ReferenceStation refStationAuth(Socket socket, HttpParser httpParser) throws IOException {
        String request = httpParser.getParam("SOURCE");
        ReferenceStation station = getStationByName(request);

        //mb station not exists
        if (station == null) {
            throw new IOException(socket.toString() + "MountPoint" + request + " is not exists.");
        }

        //mb password wrong
        if (!station.checkPassword(httpParser.getParam("PASSWORD"))) {
            throw new IOException(socket.toString() + " ref station " + request + " Bad password.");
        }

        //mb station in time connect
        if (!station.setSocket(socket)) {
            throw new IOException(socket.toString() + " station " + request + " already use.");
        }

        socket.sendOkMessage();
        return station;
    }

    /**
     * Distance from reference station to client position.
     * Used for get nearest reference station for client receiver.
     *
     * @param position
     * @return float
     * @throws NullPointerException
     */
    public float distance(NMEA.GPSPosition position) throws IllegalArgumentException {
        if (model.getLla() == null) {
            throw new IllegalArgumentException(model.getName() + " station not have coordinate position!");
        }

        return model.getLla().distance(position);
    }

    /**
     * Check password reference stations.
     *
     * @param password
     * @return boolean
     */
    protected static Map<String, ReferenceStation> refStations = new HashMap<>();

    public static ReferenceStation getStationByName(String name) {
        return refStations.get(name);
    }

    public static ReferenceStation getStationById(int id) {
        for (ReferenceStation station : refStations.values()) {
            if (station.getId() == id)
                return station;
        }
        return null;
    }

    protected ReferenceStationModel model;

    // if reference station has remover from database.
    protected void remove() {
        this.updateModel.cancel();
        this.close();
        refStations.remove(model.getName());
    }

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

    final private static Timer timer = new Timer();

    final private TimerTask updateModel = new TimerTask() {
        @Override
        public void run() {
            ReferenceStationDAO dao = new ReferenceStationDAO();
            ReferenceStationModel ref_model = dao.read(model.getId());
            if (ref_model == null)
                remove();

            model = ref_model;

            logger.info("RefStation: " + model.getName() + " update model.");
        }
    };

}