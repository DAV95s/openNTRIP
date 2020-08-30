package org.adv25.ADVNTRIP.Servers;

import org.adv25.ADVNTRIP.Clients.Client;
import org.adv25.ADVNTRIP.Databases.DAO.ReferenceStationDAO;
import org.adv25.ADVNTRIP.Databases.Models.ReferenceStationModel;
import org.adv25.ADVNTRIP.Spatial.PointLla;
import org.adv25.ADVNTRIP.Tools.AnalyzeListener;
import org.adv25.ADVNTRIP.Tools.Decoders.IDecoder;
import org.adv25.ADVNTRIP.Tools.Decoders.RAW;
import org.adv25.ADVNTRIP.Tools.Decoders.RTCM_3X;
import org.adv25.ADVNTRIP.Tools.MessagePack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class RefStation implements Runnable {
    final static private Logger logger = LogManager.getLogger(RefStation.class.getName());

    /* static block */
    private static ReferenceStationDAO dao = new ReferenceStationDAO();
    private static Map<String, RefStation> refStations = new HashMap<>();

    public static RefStation getStationByName(String name) {
        return refStations.get(name);
    }

    public static RefStation getStationById(int id) {
        for (RefStation station : refStations.values()) {
            if (station.getId() == id)
                return station;
        }
        return null;
    }

    private static Timer timer = new Timer();
    /* static block */

    /* client handlers */
    private CopyOnWriteArrayList<Client> subscribers = new CopyOnWriteArrayList<>();

    public void addClient(Client client) {
        subscribers.add(client);
    }

    public void removeClient(Client client) {
        subscribers.remove(client);
    }
    /* client handlers */

    /* networking */
    private SocketChannel socket;
    private SelectionKey key;
    private ByteBuffer buffer = ByteBuffer.allocate(32768);
    public boolean available = false;
    private AnalyzeListener analyzeListener;

    public boolean setSocket(SelectionKey key) {
        if (socket == null || !socket.isRegistered()) {
            this.analyzeListener = new AnalyzeListener(this);
            this.acceptBytes = 0;
            this.upTime = System.currentTimeMillis();
            dao.setOnline(model);

            this.socket = (SocketChannel) key.channel();
            this.key = key;
            this.key.attach(this);
            this.available = true;
            logger.info(model.getId() + " (" + model.getName() + ") has connected.");
            return true;
        } else {
            logger.info(model.getId() + " (" + model.getName() + ") already taken.");
            return false;
        }
    }

    public void safeClose() {
        dao.setOffline(model);
        this.available = false;
        this.key.cancel();
        analyzeListener.close();
        try {
            this.socket.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private boolean readBlocking = false;

    public boolean readSelf() {
        if (this.readBlocking)
            return false;

        try {
            buffer.clear();
            int bytesRead = this.socket.read(buffer);

            int totalBytesRead = bytesRead;

            while (bytesRead > 0) {
                bytesRead = this.socket.read(buffer);
                totalBytesRead += bytesRead;
            }

            if (bytesRead == -1) {
                throw new IOException();
            }

            logger.debug(model.getName() + " read " + totalBytesRead + " bytes. Reference station have " + subscribers.size() + " clients");
            acceptBytes += totalBytesRead;


        } catch (IOException e) {
            logger.info(model.getName() + " closed connection");
            this.safeClose();
            this.readBlocking = false;
        }
        return this.readBlocking = true;
    }

    IDecoder decoder = new RTCM_3X();

    @Override
    public void run() {
        MessagePack messagePack = null;

        try {
            messagePack = decoder.separate(buffer);
        } catch (IOException e) {
            if (decoder instanceof RTCM_3X)
                decoder = new RAW();
        }

        this.readBlocking = false;

        if (messagePack == null)
            return;

        analyzeListener.putData(messagePack);

        ByteBuffer localBuffer = messagePack.getFullBytes();

        for (Client client : subscribers) {
            localBuffer.flip();
            try {
                client.write(localBuffer);
            } catch (IOException e) {
                client.safeClose();
            }
        }
    }

    /* networking */

    /* data model*/
    private ReferenceStationModel model;
    private long upTime;
    private long acceptBytes;

    private TimerTask updateModel = new TimerTask() {
        @Override
        public void run() {
            ReferenceStationDAO dao = new ReferenceStationDAO();
            ReferenceStationModel ref_model = dao.read(model.getId());
            if (ref_model == null)
                remove();

            model = ref_model;
        }
    };

    public RefStation(ReferenceStationModel model) {
        this.model = model;
        timer.schedule(updateModel, 10_000, 10_000);
        refStations.put(model.getName(), this);
    }

    private void remove() {
        this.updateModel.cancel();
        this.safeClose();
        refStations.remove(model.getId());
    }

    public PointLla getPosition() {
        return model.getLla();
    }

    public boolean checkPassword(String password) {
        return model.getPassword().equals(password);
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
    /* data model*/
}