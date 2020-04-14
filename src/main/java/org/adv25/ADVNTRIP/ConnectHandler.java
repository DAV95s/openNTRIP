package org.adv25.ADVNTRIP;

import org.adv25.ADVNTRIP.Clients.Client;
import org.adv25.ADVNTRIP.Databases.DbManager;
import org.adv25.ADVNTRIP.Servers.GnssStation;
import org.adv25.ADVNTRIP.Tools.Http;
import org.adv25.ADVNTRIP.Tools.HttpFormatException;
import org.adv25.ADVNTRIP.Tools.HttpRequestParser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Base64;
import java.util.NoSuchElementException;

import static org.adv25.ADVNTRIP.Tools.Http.BAD_MESSAGE;
import static org.adv25.ADVNTRIP.Tools.Http.OK_MESSAGE;

public class ConnectHandler extends Thread {

    private SocketChannel socketChannel;

    private ByteBuffer buffer = ByteBuffer.allocate(512);

    private GnssStation requestedStation;

    public ConnectHandler(SocketChannel socket) {
        socketChannel = socket;
    }

    private HttpRequestParser http;

    @Override
    public void run() {
        try {
            socketChannel.read(buffer);

            String msg = new String(buffer.array());
            http = new HttpRequestParser();
            http.parseRequest(msg);
            http.setRemoteAddress(socketChannel.getRemoteAddress().toString());

            if (http.getRequestLine().contains("GET")) //client connection
                clientHandler();

            else if (http.getRequestLine().contains("SOURCE"))  //station connection
                stationHandler();

        } catch (IOException | HttpFormatException | IndexOutOfBoundsException e) {

            try {
                this.socketChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();
        } catch (NoSuchElementException ex) {
            Http.sendMessageAndClose(socketChannel, buffer, Caster.GetSourceTable());
        } catch (SecurityException exx) {
            Http.sendMessageAndClose(socketChannel, buffer, BAD_MESSAGE);
        }
    }

    void clientHandler() throws NoSuchElementException, SecurityException { //Client handled

        // if station not exists, will be exception
        requestedStation = Caster.getServer(http.getParam("GET"));

        if (requestedStation.isAuthentication()) {
            // if wrong basic authorization, will be exception
            String[] nameAndPass = basicAuthorizationDecode(http.getParam("Authorization"));
            DbManager database = DbManager.getInstance();
            // if wrong password, will be exception SecurityException
            database.clientAuthorization(nameAndPass[0], nameAndPass[1]);
        }
        //Successful authorization! Socket not will be closed.
        Http.sendMessage(socketChannel, buffer, OK_MESSAGE);
        requestedStation.addClient(new Client(requestedStation, socketChannel));
    }

    void stationHandler() throws SecurityException { //GNSS Station handled

        Http.sendMessage(socketChannel, buffer, Http.getOkMessage());

        try {
            //auto reconnect
            requestedStation = Caster.getServer(http.getParam("SOURCE"));
            requestedStation.setNewSocket(socketChannel);
            //add logger
        } catch (NoSuchElementException e) {
            new GnssStation(http.getParam("SOURCE"), socketChannel).start();
        }
    }

    String[] basicAuthorizationDecode(String src) {
        String temp = src.replaceAll("Basic", "").trim();

        byte[] decode = Base64.getDecoder().decode(temp);

        String result = new String(decode);

        String[] response = result.split(":");

        if (response.length != 2)
            throw new SecurityException("Illegal authorization format");

        return response;
    }
}
