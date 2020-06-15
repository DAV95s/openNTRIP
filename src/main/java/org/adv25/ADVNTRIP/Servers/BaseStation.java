package org.adv25.ADVNTRIP.Servers;

import org.adv25.ADVNTRIP.Clients.Client;
import org.adv25.ADVNTRIP.Clients.ClientListener;
import org.adv25.ADVNTRIP.Databases.DAO.BaseStationDAO;
import org.adv25.ADVNTRIP.Databases.Models.BaseStationModel;
import org.apache.log4j.Logger;

import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;


public class BaseStation implements Runnable {
    final static org.apache.log4j.Logger logger = Logger.getLogger(BaseStation.class);
    BaseStationModel model;
    SocketChannel socketChannel = null;
    long timeLastPack;
    Thread thread;
    CopyOnWriteArrayList<ClientListener> listeners = new CopyOnWriteArrayList();
    protected static HashMap<Integer, BaseStation> bases = new HashMap<>();

    public static void init() {
        BaseStationDAO dao = new BaseStationDAO();
        ArrayList<BaseStationModel> bases = dao.readAll();
        for (BaseStationModel base : bases) {
            new BaseStation(base);
        }
    }

    private BaseStation(BaseStationModel model) {
        this.model = model;
        bases.put(model.getId(), this);
        this.thread = new Thread(this);
    }

    public static BaseStation getBase(int id) {
        return bases.get(id);
    }

    public void addListener(Client client) {
        listeners.add(client);
    }

    public void removeListener(Client client) {
        listeners.remove(client);
    }

    public void safeClose() throws IOException {
        this.socketChannel.close();
    }

    //Getters and setters
    public String getMountpoint() {
        return model.getMountpoint();
    }

    public int getId() {
        return model.getId();
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public long getTimeLastPack() {
        return timeLastPack;
    }

    // If base station connection was aborted
    public void setNewSocket(SocketChannel newChannel) throws IOException {
        if (this.socketChannel != null) {
            this.socketChannel.close();
        }
        this.socketChannel = newChannel;
        if (!thread.isAlive())
            thread.start();
    }

    ByteBuffer buffer = ByteBuffer.allocate(8192);

    @Override
    public void run() {
        logger.info(model.getMountpoint() + " thread started.");

        while (true) {
            try {
                buffer.clear();
                int i = socketChannel.read(buffer);

                if (i == -1) {
                    socketChannel.close();
                    return;
                }

                Iterator<ClientListener> iterator = listeners.iterator();

                while (iterator.hasNext()) {
                    buffer.flip();
                    ClientListener client = iterator.next();
                    client.send(buffer, this);
                }

            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socketChannel.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return;
            }
        }
    }
}