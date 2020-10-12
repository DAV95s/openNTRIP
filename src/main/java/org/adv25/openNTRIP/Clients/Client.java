package org.adv25.openNTRIP.Clients;

import org.adv25.openNTRIP.Databases.Models.ClientModel;
import org.adv25.openNTRIP.Databases.Models.MountPointModel;
import org.adv25.openNTRIP.Network.IWork;
import org.adv25.openNTRIP.Network.Socket;
import org.adv25.openNTRIP.Servers.NtripCaster;
import org.adv25.openNTRIP.Servers.ReferenceStation;
import org.adv25.openNTRIP.Tools.HttpRequestParser;
import org.adv25.openNTRIP.Tools.NMEA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

public class Client implements IWork {
    final static private Logger logger = LogManager.getLogger(Client.class.getName());

    private ReferenceStation referenceStation;
    private MountPointModel mountPoint;
    private final NtripCaster caster;
    private final Socket socket;
    private ClientModel model;
    private NMEA.GPSPosition position;
    private final HttpRequestParser httpRequest;
    private final ByteBuffer input = ByteBuffer.allocate(1024);

    private final long connectionTimeStamp = System.currentTimeMillis();
    private long bytesReceive = 0;
    private long bytesSent = 0;

    public Client(Socket socket, HttpRequestParser httpRequest, NtripCaster caster) {
        logger.debug("Connection " + socket.socketId + " create new client.");
        this.socket = socket;
        this.httpRequest = httpRequest;
        this.caster = caster;
    }

    public void subscribe(ReferenceStation referenceStation) {
        if (referenceStation == null)
            return;

        this.referenceStation = referenceStation;
        this.referenceStation.addClient(this);
    }

    public void sendBadMessageAndClose() throws IOException {
        this.socket.sendBadMessageAndClose();
    }

    public long getSocketId() {
        return socket.socketId;
    }

    @Override
    public void close() {
        try {
            if (this.referenceStation != null)
                this.referenceStation.removeClient(this);

            this.socket.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public int write(ByteBuffer byteBuffer) throws IOException {
        return this.socket.write(byteBuffer);
    }

    private final Queue<byte[]> dataQueue = new ArrayDeque<>();

    public void readSelf() throws IOException {
        int count = 0;
        this.input.clear();

        if (!this.socket.endOfStreamReached) {
            count = this.socket.read(input);
        } else {
            throw new IOException("Connection " + socket.socketId + " end of stream reached.");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Connection " + socket.socketId + " accept bytes " + count + " bytes receive: " + bytesReceive);
        }

        this.input.flip();
        byte[] bytes = new byte[input.remaining()];
        this.input.get(bytes, 0, bytes.length);
        this.dataQueue.add(bytes);
    }

    @Override
    public void run() {
        if (dataQueue.peek() == null)
            return;

        String line = new String(dataQueue.poll());

        logger.debug("Connection " + socket.socketId + " accept nmea from client -> " + line);
        this.setPosition(line);

        try {
            caster.clientAuthorizationProcessing(this);
        } catch (IOException e) {
            logger.error("Connection " + socket.socketId + "Re-authorization error", e);
            this.close();
        }
    }

    /* getters and setters */
    public void setMountPoint(MountPointModel mountPoint) {
        this.mountPoint = mountPoint;
    }

    public void setPosition(String nmea_str) {
        this.position = new NMEA().parse(nmea_str);
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

}
