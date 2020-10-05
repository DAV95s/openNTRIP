package org.adv25.openNTRIP.Network;

import org.adv25.openNTRIP.Clients.Client;
import org.adv25.openNTRIP.Servers.NtripCaster;
import org.adv25.openNTRIP.Servers.ReferenceStation;
import org.adv25.openNTRIP.Tools.HttpRequestParser;
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

    private SocketChannel socket;
    private NtripCaster caster;
    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    private SelectionKey key;

    public ConnectHandler(SelectionKey key, NtripCaster caster) {
        logger.info("New connection has created!");
        this.socket = (SocketChannel) key.channel();
        this.caster = caster;
        this.key = key;
        key.attach(this);
    }

    public void close() {
        logger.debug("Close connection");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readSelf() throws IOException {
        buffer.clear();
        int bytesRead = this.socket.read(buffer);
        int totalBytesRead = bytesRead;

        while (bytesRead > 0) {
            bytesRead = this.socket.read(buffer);
            totalBytesRead += bytesRead;
        }

        if (bytesRead == -1) {
            throw new IOException();
        }

        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes, 0, bytes.length);
        dataBuffer.add(bytes);

    }

    Queue<byte[]> dataBuffer = new ArrayDeque<>();

    @Override
    public void run() {
        try {
            if (dataBuffer.peek() == null)
                return;

            String request = new String(dataBuffer.peek());
            logger.debug(request);

            HttpRequestParser httpParser = new HttpRequestParser(request);

            //GET CONNECT
            if (httpParser.getParam("GET") != null) {
                Client client = new Client(this.socket, httpParser, this.caster);
                this.caster.clientAuthorizationProcessing(client);
                this.key.attach(client);
            } else
                //OR SOURCE CONNECT
                if (httpParser.getParam("SOURCE") != null) {

                    ReferenceStation station = ReferenceStation.getStationByName(httpParser.getParam("SOURCE"));

                    //mb station not exists
                    if (station == null)
                        throw new IOException("MountPoint " + httpParser.getParam("SOURCE") + " is not exists.");

                    //mb password wrong
                    if (!station.checkPassword(httpParser.getParam("PASSWORD")))
                        throw new IOException("Bad password.");

                    //mb station in time connect
                    if (!station.setSocket(socket))
                        throw new IOException("Can't replace reference station socket.");

                    sendOkMessage();
                    this.key.attach(station);
                }
        } catch (IOException e) {
            sendBadMessage();
        }
    }

    private void sendBadMessage() {
        try {
            buffer.clear();
            buffer.put(Client.BAD_MESSAGE);
            buffer.flip();
            socket.write(buffer);
            socket.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void sendOkMessage() {
        try {
            buffer.clear();
            buffer.put(Client.OK_MESSAGE);
            buffer.flip();
            socket.write(buffer);
        } catch (IOException e) {
            logger.debug("fail try send ok message");
        }
    }
}