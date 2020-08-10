package org.adv25.ADVNTRIP.Servers;

import org.adv25.ADVNTRIP.Clients.ClientListener;
import org.adv25.ADVNTRIP.Databases.DAO.ReferenceStationDAO;
import org.adv25.ADVNTRIP.Databases.Models.ReferenceStationModel;
import org.adv25.ADVNTRIP.Spatial.Point_lla;
import org.adv25.ADVNTRIP.Tools.AnalyzeListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ReferenceStation implements Runnable {
    final static private Logger logger = LogManager.getLogger(ReferenceStation.class.getName());

    public static HashMap<Integer, ReferenceStation> bases = new HashMap<>();

    CopyOnWriteArrayList<ClientListener> listeners = new CopyOnWriteArrayList<>();

    ReferenceStationModel model;
    SocketChannel socketChannel;
    Thread thread;
    ByteBuffer buffer = ByteBuffer.allocate(8192);

    public static void init() {
        ReferenceStationDAO dao = new ReferenceStationDAO();
        ArrayList<ReferenceStationModel> bases = dao.readAll();
        for (ReferenceStationModel base : bases) {
            dao.setOffline(base);
            new ReferenceStation(base);
        }
    }

    private ReferenceStation(ReferenceStationModel model) {
        this.model = model;
        this.thread = new Thread(this);
        bases.put(model.getId(), this);
        logger.debug(model.getMountpoint() + " position: " + this.getPosition().toString());
        this.addListener(new AnalyzeListener(this));
    }

    // If base station connection was aborted
    public void setNewSocket(SocketChannel newChannel) throws IOException {
        if (thread.getState() == Thread.State.RUNNABLE || thread.getState() == Thread.State.BLOCKED) {
            logger.warn("New connection try interrupt is alive stream!");
            throw new SecurityException();
        }

        if (this.socketChannel != null) {
            this.socketChannel.close();
        }

        this.socketChannel = newChannel;

        if (thread.getState() == Thread.State.NEW) {
            thread.start();
        }

        if (thread.getState() == Thread.State.TERMINATED) {
            thread = new Thread(this);
            thread.start();
        }

        ReferenceStationDAO dao = new ReferenceStationDAO();
        dao.setOnline(model);
    }

    public static ReferenceStation getBase(int id) {
        return bases.get(id);
    }

    public void addListener(ClientListener client) {
        listeners.add(client);
    }

    public void removeListener(ClientListener client) {
        listeners.remove(client);
    }

    public void safeClose() throws IOException {
        this.socketChannel.close();
    }

    //Getters and setters
    public String getName() {
        return model.getMountpoint();
    }

    public int getId() {
        return model.getId();
    }


    public Point_lla getPosition() {
        return model.getLla();
    }

    @Override
    public String toString() {
        return model.getId() + "(" + model.getMountpoint() + ")";
    }

    public ReferenceStationModel getModel() {
        return model;
    }

    @Override
    public void run() {
        logger.info(model.getMountpoint() + " thread started.");

        while (true) {
            try {
                buffer.clear();
                int i = socketChannel.read(buffer);

                if (i == -1) {
                    socketChannel.close();
                    ReferenceStationDAO dao = new ReferenceStationDAO();
                    dao.setOffline(model);
                    return;
                }

                for (ClientListener listener : listeners) {
                    buffer.flip();
                    listener.send(buffer, this);
                }

            } catch (AsynchronousCloseException ex) {
                logger.warn(model.getMountpoint() + " new connection, socket has replace!");
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socketChannel.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
    }
}