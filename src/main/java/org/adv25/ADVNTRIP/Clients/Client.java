package org.adv25.ADVNTRIP.Clients;

import org.adv25.ADVNTRIP.Databases.Models.ClientModel;
import org.adv25.ADVNTRIP.Databases.Models.MountPointModel;
import org.adv25.ADVNTRIP.Servers.NtripCaster;
import org.adv25.ADVNTRIP.Servers.ReferenceStation;
import org.adv25.ADVNTRIP.Tools.HttpRequestParser;
import org.adv25.ADVNTRIP.Tools.NMEA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Client implements Runnable {
    final static private Logger logger = LogManager.getLogger(Client.class.getName());

    private ReferenceStation referenceStation;
    private MountPointModel mountPoint;
    private NtripCaster caster;
    private SelectionKey key;
    private SocketChannel socket;
    private ClientModel model;
    private NMEA.GPSPosition position;
    private HttpRequestParser httpRequest;
    private ByteBuffer input = ByteBuffer.allocate(2048);
    private ByteBuffer output = ByteBuffer.allocate(32768);

    private long connectionTimeStamp = System.currentTimeMillis();
    private long bytesReceived = 0;

    public static final byte[] OK_MESSAGE = "ICY 200 OK\r\n".getBytes();
    public static final byte[] BAD_MESSAGE = "ERROR - Bad Password\r\n".getBytes();


    public Client(SocketChannel socket, HttpRequestParser httpRequest, NtripCaster caster, SelectionKey key) {
        logger.debug("New client!");
        this.socket = socket;
        this.httpRequest = httpRequest;
        this.caster = caster;
        this.key = key;
        this.key.attach(this);
    }

    public void setReferenceStation(ReferenceStation referenceStation) {
        this.referenceStation = referenceStation;
        referenceStation.addClient(this);
    }

    public void sendMessageAndClose(byte[] msg) throws IOException {
        output.clear();
        output.put(msg);
        output.flip();
        this.socket.write(output);
        this.safeClose();
    }

    public void safeClose() {
        this.key.cancel();
        this.referenceStation.removeClient(this);
        try {
            this.socket.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }


    public int write(ByteBuffer byteBuffer) throws IOException {
        int bytesWritten = this.socket.write(byteBuffer);
        int totalBytesWritten = bytesWritten;

        while (bytesWritten > 0 && byteBuffer.hasRemaining()) {
            bytesWritten = this.socket.write(byteBuffer);
            totalBytesWritten += bytesWritten;
        }

        return totalBytesWritten;
    }

    public void read() {
        try {
            input.clear();
            int bytesRead = this.socket.read(input);
            int totalBytesRead = bytesRead;

            while (bytesRead > 0) {
                bytesRead = this.socket.read(input);
                totalBytesRead += bytesRead;
            }

            if (bytesRead == -1) {
                throw new IOException();
            }

            bytesReceived += totalBytesRead;

        } catch (IOException e) {
            logger.info("Client closed connection");
            this.safeClose();
        }
    }

    /* getters and setters */
    public void setMountPoint(MountPointModel mountPoint) {
        this.mountPoint = mountPoint;
    }

    public void setPosition(String nmea_str) {
        position = new NMEA().parse(nmea_str);
    }

    public NMEA.GPSPosition getPosition() {
        return position;
    }

    public String getHttpHeader(String key) {
        return httpRequest.getParam(key);
    }

    public String getPassword() {
        return this.model.getPassword();
    }

    public void setModel(ClientModel model) {
        this.model = model;
    }
    /* getters and setters */

    @Override
    public void run() {
        input.flip();
        byte[] bytes = new byte[input.limit()];
        input.get(bytes);
        input.clear();
        System.out.println(new String(bytes));
        setPosition(new String(bytes));

        try {
            caster.clientAuthorizationProcessing(this);
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
