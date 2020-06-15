package org.adv25.ADVNTRIP.Clients;

import org.adv25.ADVNTRIP.Databases.Models.ClientModel;
import org.adv25.ADVNTRIP.Servers.BaseStation;
import org.adv25.ADVNTRIP.Servers.MountPoint;
import org.adv25.ADVNTRIP.Tools.NMEA;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;

public class Client implements ClientListener {

    SocketChannel channel;
    ClientModel model;
    NMEA.GPSPosition position;
    String requestLine;
    Hashtable<String, String> requestHeaders;
    StringBuffer messageBody;
    MountPoint mountPoint;
    BaseStation baseStation;
    final static org.apache.log4j.Logger logger = Logger.getLogger(Client.class);

    public Client(SocketChannel channel, String requestLine, Hashtable<String, String> header, StringBuffer body) {
        this.channel = channel;
        this.requestLine = requestLine;
        this.requestHeaders = header;
        this.messageBody = body;
    }

    //Network
    public static final byte[] OK_MESSAGE = "ICY 200 OK\r\n".getBytes();
    public static final byte[] BAD_MESSAGE = "ERROR - Bad Password\r\n".getBytes();

    private ByteBuffer bb = ByteBuffer.allocate(8192);

    public void sendMessage(byte[] msg) throws IOException {
        this.bb.clear();
        this.bb.put(msg);
        this.bb.flip();
        this.channel.write(bb);
    }

    public void sendMessageAndClose(byte[] msg) throws IOException {
        this.bb.clear();
        this.bb.put(msg);
        this.bb.flip();
        this.channel.write(bb);
        this.channel.close();
    }

    public void safeClose() {
        try {
            baseStation.removeListener(this);
            channel.close();
        } catch (IOException e) {

        }
    }

    public void send(ByteBuffer bytes, BaseStation baseStation) throws IOException {
        try {
            this.bb.clear();
            this.bb.put(bytes);
            //this.bb.put(mountPoint.injection(this, baseStation));
            this.bb.flip();
            channel.write(bb);
        } catch (IOException e) {
            logger.info(model.getName() + " user was disconnected.", e);
            baseStation.removeListener(this);
        }

    }

    //Getters and setters
    public NMEA.GPSPosition getPosition() {
        return position;
    }

    public void setPosition(String nmea) {
        NMEA nmea1 = new NMEA();
        position = nmea1.parse(nmea);
        logger.info(model.getName() + " set " + position.toString());
    }

    public String getHttpHeader(String key) {
        return requestHeaders.get(key);
    }

    public String getPassword() {
        return this.model.getPassword();
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public String getRequestLine() {
        return requestLine;
    }

    public StringBuffer getMessageBody() {
        return messageBody;
    }

    public void setPosition(NMEA.GPSPosition position) {
        this.position = position;
    }

    public void setMountPoint(MountPoint mountPoint) {
        this.mountPoint = mountPoint;
    }

    public void setModel(ClientModel model) {
        this.model = model;
    }

    public void setBaseStation(BaseStation baseStation) {
        this.baseStation = baseStation;
    }
}
