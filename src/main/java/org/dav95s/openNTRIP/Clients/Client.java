package org.dav95s.openNTRIP.Clients;


import org.dav95s.openNTRIP.Databases.Models.ClientModel;
import org.dav95s.openNTRIP.Network.IWork;
import org.dav95s.openNTRIP.Network.Socket;
import org.dav95s.openNTRIP.Servers.NtripCaster;
import org.dav95s.openNTRIP.Servers.ReferenceStation;
import org.dav95s.openNTRIP.Tools.HttpParser;
import org.dav95s.openNTRIP.Tools.NMEA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

public class Client implements IWork {
    final static private Logger logger = LogManager.getLogger(Client.class.getName());

    final private NtripCaster caster;
    final private Socket socket;
    final private HttpParser httpRequest;
    final private ByteBuffer input = ByteBuffer.allocate(1024);

    private ReferenceStation referenceStation;
    private ClientModel model;
    private NMEA.GPSPosition position;

    private boolean isAuthentication = false;

    public Client(Socket socket, HttpParser httpRequest, NtripCaster caster) {
        logger.debug(socket.toString() + "is new client.");
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

    private final Queue<byte[]> dataQueue = new ArrayDeque<>();

    public void readSelf() throws IOException {
        this.input.clear();

        if (socket.endOfStreamReached){
            throw new IOException(this.toString() + "end of stream reached.");
        }


        int count = this.socket.read(input);
        logger.debug(() -> this.toString() + "read bytes: " + count);
        if (count == 0)
            return;

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

        logger.debug(socket.toString() + " accept nmea from client -> " + line);
        this.setPosition(line);

        try {
            caster.clientAuthorizationProcessing(this);
        } catch (IOException e) {
            logger.error(socket.toString() + "Re-authorization error", e);
            this.close();
        }


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

    @Override
    public String toString() {
        return "Client " + socket.toString();
    }
}
