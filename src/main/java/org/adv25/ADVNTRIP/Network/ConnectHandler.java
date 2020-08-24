package org.adv25.ADVNTRIP.Network;

import org.adv25.ADVNTRIP.Clients.Client;
import org.adv25.ADVNTRIP.Databases.Models.MountPointModel;
import org.adv25.ADVNTRIP.Servers.NtripCaster;
import org.adv25.ADVNTRIP.Servers.RefStation;
import org.adv25.ADVNTRIP.Tools.HttpRequestParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class ConnectHandler implements Runnable {
    final static private Logger logger = LogManager.getLogger(ConnectHandler.class.getName());

    private SocketChannel socket;
    private NtripCaster caster;
    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    private SelectionKey key;

    public ConnectHandler(SelectionKey key) {
        logger.info("New connection has created!");
        this.socket = (SocketChannel) key.channel();
        this.caster = (NtripCaster) key.attachment();
        this.key = key;

    }

    public void close() {
        logger.debug("Close connection");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        key.cancel();
    }

    public void read() {
        try {
            int bytesRead = this.socket.read(buffer);
            int totalBytesRead = bytesRead;

            while (bytesRead > 0) {
                bytesRead = this.socket.read(buffer);
                totalBytesRead += bytesRead;
            }

            if (bytesRead == -1) {
                throw new IOException();
            }
        } catch (IOException e) {
            this.close();
            logger.info("Closed connection");
        }
    }

    @Override
    public void run() {
        try {
            buffer.flip();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            String request = new String(bytes);
            logger.debug(request);

            HttpRequestParser httpParser = new HttpRequestParser();
            httpParser.parseRequest(request);

            //GET CONNECT
            if (httpParser.getParam("GET") != null) {
                Client client = new Client(this.socket, httpParser, this.caster, this.key);
                this.caster.clientAuthorizationProcessing(client);
            } else
                //OR SOURCE CONNECT
                if (httpParser.getParam("SOURCE") != null) {

                    RefStation station = RefStation.getStationByName(httpParser.getParam("SOURCE"));

                    //mb account of station not exists
                    if (station == null)
                        throw new IOException("MountPoint " + httpParser.getParam("SOURCE") + " is not exists.");

                    //mb password wrong
                    if (!station.checkPassword(httpParser.getParam("PASSWORD")))
                        throw new IOException("Bad password.");

                    //mb station in time connect
                    if (!station.setSocket(key))
                        throw new IOException("Can't replace reference station socket.");

                    sendOkMessage();
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