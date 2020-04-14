package org.adv25.ADVNTRIP.Servers;


import org.adv25.ADVNTRIP.Caster;
import org.adv25.ADVNTRIP.Clients.IClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;


public class GnssStation extends Thread {

    ArrayList<IClient> clients = new ArrayList<>();

    ByteBuffer buffer = ByteBuffer.allocate(4096);

    long timeLastMsg;

    private SocketChannel socketChannel;

    public GnssStation(String name, SocketChannel s) {
        this.Mountpoint = name;
        this.socketChannel = s;
        Caster.AddServer(this);
        //new Analyzer(this, 10000);
    }

    public long getTimeLastMsg() {
        return timeLastMsg;
    }

    public String getStationName() {
        return this.Mountpoint;
    }

    public void setNewSocket(SocketChannel newChannel) {
        this.socketChannel = newChannel;
    }

    public void addClient(IClient client) {
        clients.add(client);
    }

    public void removeClient(IClient client) {
        clients.remove(client);
    }

    public boolean isAuthentication() {
        if (this.Authentication == 'Y')
            return true;
        else
            return false;
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

    //server description
    private long timeMark = 0; //for garbage collector
    private String Type = "STR";
    private String Mountpoint;
    private String Identifier = "";
    private String Format = "";
    private String FormatDetails = "";
    private String Carrier = "";
    private String NavSystem = "";
    private String Network = "";
    private String Country = "";
    private Double Latitude = 0.0;
    private Double Longitude = 0.0;
    private Boolean Nmea = false;
    private Boolean Solution = false;
    private String Generator = "";
    private String Compression = "";
    private char Authentication = 'N';
    private char Fee = 'N';
    private int Bitrate = 128;
    private String Misc = "";

    //for sourcetable
    @Override
    public String toString() {
        return Type + ';' + Mountpoint + ';' + Identifier + ';' + Format + ';' + FormatDetails + ';' + Carrier + ';' + NavSystem + ';' + Network + ';' + Country
                + ';' + Latitude.toString() + ';' + Longitude.toString() + ';' + Nmea.toString() + ';' + Solution.toString() + ';' + Generator + ';' + Compression
                + ';' + Authentication + ';' + Fee + ';' + Bitrate + ';' + Misc;
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