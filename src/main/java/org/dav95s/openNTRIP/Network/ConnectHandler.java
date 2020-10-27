package org.dav95s.openNTRIP.Network;

import org.dav95s.openNTRIP.Clients.Client;
import org.dav95s.openNTRIP.Servers.NtripCaster;
import org.dav95s.openNTRIP.Servers.ReferenceStation;
import org.dav95s.openNTRIP.Tools.HttpParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;

public class ConnectHandler implements IWork {
    final static private Logger logger = LogManager.getLogger(ConnectHandler.class.getName());

    private final Socket socket;
    private final NtripCaster caster;
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);
    private final SelectionKey key;

    public ConnectHandler(SelectionKey key, NtripCaster caster) throws IOException {
        this.socket = new Socket((SocketChannel) key.channel());
        this.caster = caster;
        this.key = key;
        this.key.attach(this);
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug(socket.toString() + " closed!");
    }

    public void readSelf() throws IOException {
        this.buffer.clear();

        if (socket.endOfStreamReached){
            throw new IOException(socket.toString() + " end of stream reached.");
        }


        int count = this.socket.read(buffer);
        logger.debug(() -> socket.toString() + "read bytes: " + count);
        this.buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        this.buffer.get(bytes, 0, bytes.length);
        this.dataQueue.add(bytes);
    }

    Queue<byte[]> dataQueue = new ArrayDeque<>();

    @Override
    public void run() {
        try {
            if (dataQueue.peek() == null)
                return;

            String request = new String(dataQueue.poll());

            logger.debug(() -> socket.toString() + "send request:\r\b" + request);

            HttpParser httpParser = new HttpParser(request);

            if (httpParser.getParam("GET") != null) {
                //GET CONNECT
                Client client = new Client(this.socket, httpParser, this.caster);
                this.caster.clientAuthorizationProcessing(client);
                this.key.attach(client);

            } else if (httpParser.getParam("SOURCE") != null) {
                //OR SOURCE CONNECT
                ReferenceStation station = ReferenceStation.refStationAuth(socket, httpParser);
                this.key.attach(station);
            }

        } catch (Exception e) {
            logger.error(e);
            this.socket.sendBadMessageAndClose();
        }
    }
}