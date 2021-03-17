package org.dav95s.openNTRIP.Network;

import com.github.pbbl.heap.ByteBufferPool;
import org.dav95s.openNTRIP.Clients.User;
import org.dav95s.openNTRIP.ServerBootstrap;
import org.dav95s.openNTRIP.Servers.NtripCaster;
import org.dav95s.openNTRIP.Servers.ReferenceStation;
import org.dav95s.openNTRIP.Tools.HttpParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;

public class ConnectHandler implements INetworkHandler {
    final static private Logger logger = LogManager.getLogger(ConnectHandler.class.getName());

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
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug(socket.toString() + " closed!");
    }

    @Override
    public void readChannel() throws IOException {
        ByteBuffer buffer = bufferPool.take(1024);

        if (socket.endOfStreamReached) {
            throw new IOException(socket.toString() + " end of stream reached.");
        }

        int count = this.socket.read(buffer);

        buffer.flip();
        this.dataQueue.add(buffer);

        if (logger.isDebugEnabled()) {
            JSONObject object = new JSONObject();
            object.put("socket", socket.toString());
            object.put("read", count);
            logger.debug(object);
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
                JSONObject object = new JSONObject();
                object.put("from", "ConnectHandler");
                object.put("socket", socket.toString());
                object.put("request", request);
                logger.debug(object);
            }

            HttpParser httpParser = new HttpParser(request);

            if (httpParser.getParam("GET") != null) {
                //GET CONNECT
                User user = new User(this.socket, httpParser, this.caster);
                this.caster.clientAuthorization(user);

                this.socket.attach(user);
            } else if (httpParser.getParam("SOURCE") != null) {
                //SOURCE CONNECT
                ServerBootstrap root = ServerBootstrap.getInstance();
                ReferenceStation referenceStation = root.getReferenceStationByName(httpParser.getParam("SOURCE"));
                referenceStation.referenceStationAuthentication(socket, httpParser);
                this.socket.attach(referenceStation);
            } else {
                this.close();
            }

        } catch (Exception e) {
            logger.error(e);
            this.socket.sendBadMessageAndClose();
        }
    }
}