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
import org.dav95s.openNTRIP.Tools.Observer.IObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;

public class User implements INetworkHandler, IObserver {
    final static private Logger logger = LoggerFactory.getLogger(User.class.getName());
    final static private ByteBufferPool bufferPool = new ByteBufferPool();

    final private long DELAY_POSITION_UPDATE = 30_000;

    final private NtripCaster caster;
    final private Socket socket;
    final private HttpParser httpRequest;


    private ReferenceStation myReferenceStation;
    private MountPoint myMountPoint;

    private UserModel model;
    protected NMEA.GPSPosition position = new NMEA().parse("");

    public boolean authenticated = false;
    final private long connectTimeMark;

    public User(Socket socket, HttpParser httpRequest, NtripCaster caster) {
        this.socket = socket;
        this.httpRequest = httpRequest;
        this.caster = caster;
        this.connectTimeMark = System.currentTimeMillis();
    }

    public void sendBadMessageAndClose() {
        this.socket.sendBadMessageAndClose();
    }

    public void sendOkMessage() throws IOException {
        this.socket.sendOkMessage();
    }

    @Override
    public void close() {
        if (this.myMountPoint != null)
            this.myMountPoint.removeClient(this);

        if (this.myReferenceStation != null)
            this.myReferenceStation.removeObserver(this);
        this.socket.close();
    }

    synchronized public int write(ByteBuffer byteBuffer) throws IOException {
        return this.socket.write(byteBuffer);
    }

    public int write(byte[] bytes) throws IOException {
        return this.write(ByteBuffer.wrap(bytes));
    }

    private final Queue<ByteBuffer> dataQueue = new ArrayDeque<>();

    public void readChannel() throws IOException {
        ByteBuffer buffer = bufferPool.take(2048);

        if (socket.endOfStreamReached) {
            throw new IOException(this + "end of stream reached.");
        }

        int count = this.socket.read(buffer);

        logger.debug(this + "read bytes: " + count);

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

        logger.debug(this + " accept from client -> " + line);
        this.position = new NMEA().parse(line);
        logger.debug(this + " new position is -> " + this.position.toString());

        try {
            myMountPoint.updateReferenceStation(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
        this.myMountPoint = mountPoint;
    }

    public void subscribe(ReferenceStation referenceStation) {
        if (referenceStation == null) {
            logger.debug(this + "can't subscribes to null");
            return;
        }

        if (myReferenceStation == referenceStation) {
            logger.debug("Already connected to " + myReferenceStation);
            return;
        }

        logger.debug(this + " reconnected to " + referenceStation);
        if (myReferenceStation != null) {
            this.myReferenceStation.removeObserver(this);
        }
        this.myReferenceStation = referenceStation;
        this.myReferenceStation.registerObserver(this);
    }

    @Override
    public void notify(ReferenceStation referenceStation, ByteBuffer buffer) {
        try {
            buffer.flip();
            this.write(buffer);
            this.myMountPoint.epochEventListener(this);
        } catch (IOException e) {
            this.close();
            logger.debug("Notify error ", e);
        }
    }
}
