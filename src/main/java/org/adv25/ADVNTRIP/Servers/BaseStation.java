package org.adv25.ADVNTRIP.Servers;

import org.adv25.ADVNTRIP.Clients.ClientListener;
import org.adv25.ADVNTRIP.Databases.DAO.BaseStationDAO;
import org.adv25.ADVNTRIP.Databases.Models.BaseStationModel;
import org.adv25.ADVNTRIP.Spatial.Point_lla;
import org.adv25.ADVNTRIP.Tools.Analyzer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class BaseStation implements Runnable {
    final static private Logger logger = LogManager.getLogger(BaseStation.class.getName());

    CopyOnWriteArrayList<ClientListener> listeners = new CopyOnWriteArrayList<>();

    static HashMap<Integer, BaseStation> bases = new HashMap<>();

    BaseStationModel model;
    SocketChannel socketChannel = null;
    Thread thread;
    ByteBuffer buffer = ByteBuffer.allocate(8192);

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
        addListener(Analyzer.registration(this));
        logger.debug(model.getMountpoint() + " position: " + this.getPosition().toString());
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
        System.out.println(thread.getState());
        if (thread.getState() == Thread.State.NEW) {
            thread.start();
        }

        if (thread.getState() == Thread.State.TERMINATED) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public static BaseStation getBase(int id) {
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

    public BaseStationModel getModel() {
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
                    return;
                }

                Iterator<ClientListener> iterator = listeners.iterator();

                while (iterator.hasNext()) {
                    buffer.flip();
                    ClientListener client = iterator.next();
                    client.send(buffer, this);
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