package org.adv25.ADVNTRIP.Servers;


import org.adv25.ADVNTRIP.Caster;
import org.adv25.ADVNTRIP.Clients.IClient;
import org.adv25.ADVNTRIP.Databases.Models.StationModel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;


public class GnssStation extends Thread {

    ArrayList<IClient> clients = new ArrayList<>();

    ByteBuffer buffer = ByteBuffer.allocate(4096);

    long timeLastMsg;

    StationModel fields;

    private SocketChannel socketChannel;

    public StationModel getModel(){
        return fields;
    }

    public GnssStation(SocketChannel s, StationModel model) {
        fields = model;

        this.socketChannel = s;
        Caster.AddServer(this);
        //new Analyzer(this);
        this.start();
    }

    public GnssStation(SocketChannel s, String mountpoint ) {
        fields = new StationModel();
        fields.setMountpoint(mountpoint);

        this.socketChannel = s;
        Caster.AddServer(this);
        //new Analyzer(this);
        this.start();
    }

    public long getTimeLastMsg() {
        return timeLastMsg;
    }

    public String getMountpoint() {
        return fields.getMountpoint();
    }

    public void setNewSocket(SocketChannel newChannel) {
        try{
            this.socketChannel.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        this.socketChannel = newChannel;
    }

    public void addClient(IClient client) {
        clients.add(client);
    }

    public void removeClient(IClient client) {
        clients.remove(client);
    }

    public boolean isAuthentication() {
        return fields.getAuthentication();
    }

    public void safeClose() {
        ArrayList<IClient> frame = new ArrayList<IClient>(clients);
        try {
            socketChannel.close();
            for (var client : frame) {
                client.safeClose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCountClients() {
        return clients.size();
    }

    //for sourcetable
    @Override
    public String toString() {
        return fields.toString();
    }

    //Send messages
    @Override
    public void run() {

        while (true) {
            try {
                if (socketChannel.read(buffer) == -1 && clients.size() == 0) {
                    buffer.clear();
                    timeLastMsg = System.currentTimeMillis();
                    Thread.sleep(1000);
                    continue;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            ArrayList<IClient> frame = new ArrayList<IClient>(clients);

            for (IClient client : frame) {
                try {
                    buffer.flip();
                    client.sendMessage(buffer);
                } catch (IOException e) {
                    clients.remove(client);
                }
            }
            buffer.clear();
        }
    }
}