package org.adv25.ADVNTRIP.Servers;

import org.adv25.ADVNTRIP.Clients.Client;
import org.adv25.ADVNTRIP.Databases.DAO.ReferenceStationDAO;
import org.adv25.ADVNTRIP.Databases.Models.ReferenceStationModel;
import org.adv25.ADVNTRIP.Spatial.PointLla;
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
    private static Map<Integer, RefStation> refStations = new HashMap<>();

    public static RefStation getStationById(int id) {
        return refStations.get(id);
    }

    public static RefStation getStationByName(String name) {
        for (RefStation station : refStations.values()) {
            if (station.getName().equals(name))
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

    public boolean setSocket(SelectionKey key) {
        if (socket == null || !socket.isRegistered()) {
            acceptBytes = 0;
            upTime = System.currentTimeMillis();

            this.socket = (SocketChannel) key.channel();
            this.key = key;
            this.key.attach(this);
            this.available = true;
            logger.info(model.getId() + " (" + model.getMountpoint() + ") has connected.");
            return true;
        } else {
            logger.info(model.getId() + " (" + model.getMountpoint() + ") already taken.");
            return false;
        }
    }

    public void readSelf() {
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

            logger.debug(model.getMountpoint() + " read " + totalBytesRead + " bytes. Reference station have " + subscribers.size() + " clients");
            acceptBytes += totalBytesRead;

        } catch (IOException e) {
            logger.info(model.getMountpoint() + " closed connection");
            this.safeClose();
        }
    }

    public void safeClose() {
        this.available = false;
        this.key.cancel();
        try {
            this.socket.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public void run() {
        for (Client client : subscribers) {
            this.buffer.flip();
            try {
                client.write(this.buffer);
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

    //***** model updaters
    private TimerTask updateModel = new TimerTask() {
        @Override
        public void run() {
            ReferenceStationDAO dao = new ReferenceStationDAO();
            ReferenceStationModel ref_model = dao.read(model.getId());
            if (ref_model == null)
                remove();

            logger.debug(ref_model.getMountpoint() + " ref. update");
            model = ref_model;
        }
    };

    public RefStation(ReferenceStationModel model) {
        this.model = model;
        timer.schedule(updateModel, 10_000, 10_000);
        refStations.put(model.getId(), this);
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
        return model.getMountpoint();
    }

    public int getId() {
        return model.getId();
    }
    /* data model*/
}