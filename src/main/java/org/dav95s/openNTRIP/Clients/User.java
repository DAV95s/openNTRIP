package org.dav95s.openNTRIP.Clients;


import com.github.pbbl.heap.ByteBufferPool;
import org.dav95s.openNTRIP.Databases.Models.UserModel;
import org.dav95s.openNTRIP.Network.INetworkHandler;
import org.dav95s.openNTRIP.Network.Socket;
import org.dav95s.openNTRIP.Servers.MountPoint;
import org.dav95s.openNTRIP.Servers.NtripCaster;
import org.dav95s.openNTRIP.Servers.ReferenceStation;
import org.dav95s.openNTRIP.Tools.HttpParser;
import org.dav95s.openNTRIP.Tools.NMEA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;

public class User implements INetworkHandler {
    final static private Logger logger = LogManager.getLogger(User.class.getName());

    final private long DELAY_POSITION_UPDATE = 30_000;

    final private NtripCaster caster;
    final private Socket socket;
    final private HttpParser httpRequest;
    final static private ByteBufferPool bufferPool = new ByteBufferPool();

    private ReferenceStation referenceStation;
    private UserModel model;
    private NMEA.GPSPosition position;

    final private long connectTimeMark;
    private MountPoint mountPoint;

    public User(Socket socket, HttpParser httpRequest, NtripCaster caster) {
        logger.debug(socket.toString() + "is new client.");
        this.socket = socket;
        this.httpRequest = httpRequest;
        this.caster = caster;
        this.connectTimeMark = System.currentTimeMillis();
    }

    public void subscribe(ReferenceStation referenceStation) {
        if (referenceStation == null)
            return;

        if (this.referenceStation != null)
            this.referenceStation.removeClient(this);

        this.referenceStation = referenceStation;
        this.referenceStation.addClient(this);
    }

    public void sendBadMessageAndClose() {
        this.socket.sendBadMessageAndClose();
    }

    public void sendOkMessage() throws IOException {
        this.socket.sendOkMessage();
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

    private final Queue<ByteBuffer> dataQueue = new ArrayDeque<>();

    public void readChannel() throws IOException {
        ByteBuffer buffer = bufferPool.take(2048);

        if (socket.endOfStreamReached) {
            throw new IOException(this.toString() + "end of stream reached.");
        }

        int count = this.socket.read(buffer);

        logger.debug(() -> this.toString() + "read bytes: " + count);

        if (count == 0)
            return;

        buffer.flip();
        this.dataQueue.add(buffer);
    }

    @Override
    public void run() {
        if (dataQueue.peek() == null)
            return;

        ByteBuffer buffer = dataQueue.poll();
        String line = StandardCharsets.UTF_8.decode(buffer).toString();
        bufferPool.give(buffer);

        logger.debug(socket.toString() + " accept nmea from client -> " + line);
        this.setPosition(line);

        ReferenceStation rs = mountPoint.getReferenceStation(this);
        this.subscribe(rs);
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

    public void setModel(UserModel model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "User " + socket.toString();
    }

    public void setMountPoint(MountPoint mountPoint) {
        this.mountPoint = mountPoint;
    }
}
