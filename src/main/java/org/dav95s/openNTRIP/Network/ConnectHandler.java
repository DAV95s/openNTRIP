package org.dav95s.openNTRIP.Network;

import com.github.pbbl.heap.ByteBufferPool;
import org.dav95s.openNTRIP.Clients.User;
import org.dav95s.openNTRIP.Servers.NtripCaster;
import org.dav95s.openNTRIP.Servers.ReferenceStation;
import org.dav95s.openNTRIP.Tools.HttpParser;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;

public class ConnectHandler implements INetworkHandler {
    final static private Logger logger = LoggerFactory.getLogger(ConnectHandler.class.getName());

    private final Socket socket;
    private final NtripCaster caster;
    private static final ByteBufferPool bufferPool = new ByteBufferPool();

    public ConnectHandler(Socket socket, NtripCaster caster) {
        this.socket = socket;
        this.caster = caster;
        this.socket.attach(this);
    }

    @Override
    public void close() {
        socket.close();
        logger.debug(socket + " closed!");
    }

    @Override
    public void readChannel() throws IOException {
        ByteBuffer buffer = bufferPool.take(1024);

        if (socket.endOfStreamReached) {
            throw new IOException(socket + " end of stream reached.");
        }

        int count = this.socket.read(buffer);

        buffer.flip();
        this.dataQueue.add(buffer);

        if (logger.isDebugEnabled()) {
            JSONObject object = new JSONObject();
            object.put("socket", socket.toString());
            object.put("read", count);
            logger.debug(object.toString());
        }
    }

    Queue<ByteBuffer> dataQueue = new ArrayDeque<>();

    @Override
    public void run() {
        try {
            if (dataQueue.peek() == null)
                return;

            ByteBuffer buffer = dataQueue.poll();
            String request = StandardCharsets.US_ASCII.decode(buffer).toString();
            bufferPool.give(buffer);

            if (logger.isDebugEnabled()) {
                logger.debug(socket.toString() + " new connection. [" + request + "]");
            }

            HttpParser httpParser = new HttpParser(request);

            if (httpParser.getParam("GET") != null) {
                //GET CONNECT
                User user = new User(this.socket, httpParser, this.caster);
                logger.debug(socket.toString() + "is new client.");
                this.caster.clientAuthorization(user);
                this.socket.attach(user);
            } else if (httpParser.getParam("SOURCE") != null) {
                //SOURCE CONNECT
                String name = httpParser.getParam("SOURCE");
                ReferenceStation referenceStation = this.caster.getReferenceStationByName(name);
                referenceStation.authentication(socket, httpParser);
                this.socket.attach(referenceStation);
            } else {
                this.close();
            }

        } catch (Exception e) {
            logger.error("ConnectHandler", e);
            this.socket.sendBadMessageAndClose();
        }
    }
}