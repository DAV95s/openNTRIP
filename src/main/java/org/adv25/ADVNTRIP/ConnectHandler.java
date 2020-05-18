package org.adv25.ADVNTRIP;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.adv25.ADVNTRIP.Clients.Client;
import org.adv25.ADVNTRIP.Databases.DAO.ClientDAO;
import org.adv25.ADVNTRIP.Databases.DAO.StationDAO;
import org.adv25.ADVNTRIP.Databases.Models.ClientModel;
import org.adv25.ADVNTRIP.Databases.Models.StationModel;
import org.adv25.ADVNTRIP.Servers.GnssStation;
import org.adv25.ADVNTRIP.Tools.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.adv25.ADVNTRIP.Tools.HttpProtocol.BAD_MESSAGE;
import static org.adv25.ADVNTRIP.Tools.HttpProtocol.OK_MESSAGE;

public class ConnectHandler extends Thread {

    private SocketChannel socketChannel;

    private ByteBuffer buffer = ByteBuffer.allocate(512);

    public ConnectHandler(SocketChannel socket) {
        socketChannel = socket;
    }

    private HttpRequestParser http;

    public static final Logger log = Logger.getLogger(ConnectHandler.class.getName());

    @Override
    public void run() {
        try {
            socketChannel.read(buffer);

            String msg = new String(buffer.array());
            log.log(Level.INFO, socketChannel.getRemoteAddress() + " new connection\r\n" + msg);
            http = new HttpRequestParser();
            http.parseRequest(msg);

            if (http.getRequestLine().contains("GET")){//client connection
                clientHandler();
            }else if (http.getRequestLine().contains("SOURCE")){ //station connection
                stationHandler();
            }

        } catch (IOException | HttpFormatException | IndexOutOfBoundsException e) {

            try {
                this.socketChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();
        } catch (NoSuchElementException ex) {
            HttpProtocol.sendMessageAndClose(socketChannel, buffer, Caster.GetSourceTable());
        } catch (SecurityException exx) {
            HttpProtocol.sendMessageAndClose(socketChannel, buffer, BAD_MESSAGE);
        }
    }

    void clientHandler() throws SecurityException { //Client handled
        GnssStation requestedStation = Caster.getServer(http.getParam("GET"));

        // if station not exists
        if (requestedStation == null) {
            log.log(Level.INFO, requestedStation + " is not exist");
            HttpProtocol.sendMessageAndClose(socketChannel, buffer, Caster.GetSourceTable());
            return;
        }

        ClientDAO dao = new ClientDAO();

        // if station have a password
        if (requestedStation.isAuthentication()) {
            //user [0] and pass [1] decode
            String[] acc = basicAuthorizationDecode(http.getParam("Authorization"));

            ClientModel clientModel = dao.read(acc[0]);

            //user not a registered
            if (clientModel == null) {
                throw new SecurityException("unknown user name or bad password");
            }

            BCrypt.Result rr = BCrypt.verifyer().verify(acc[1].getBytes(), clientModel.getPassword().getBytes());
            
            if (acc[1].equals(clientModel.getPassword())) {
                HttpProtocol.sendMessage(socketChannel, buffer, OK_MESSAGE);
                requestedStation.addClient(new Client(requestedStation, socketChannel, clientModel));
            } else {
                throw new SecurityException("unknown user name or bad password");
            }
            return;
        }

        HttpProtocol.sendMessage(socketChannel, buffer, OK_MESSAGE);
        requestedStation.addClient(new Client(requestedStation, socketChannel, null));
    }

    void stationHandler() throws SecurityException { //GNSS Station handled
        String password = http.getParam("PASSWORD");
        String stationName = http.getParam("SOURCE");
        GnssStation connectedStation = Caster.getServer(stationName);

        Config config = Config.getInstance();
        String generalPassword = config.getProperties("GeneralStationPassword");

        //
        if (generalPassword != null) {

            if (generalPassword.equals(password)) {
                HttpProtocol.sendMessage(socketChannel, buffer, HttpProtocol.getOkMessage());

                if (connectedStation != null) {
                    connectedStation.setNewSocket(socketChannel);
                } else {
                    new GnssStation(socketChannel, stationName);
                }
                return;
            } else {
                throw new SecurityException("unknown station name or bad password");
            }
        }

        StationDAO dao = new StationDAO();
        StationModel model = dao.read(stationName);

        if (model == null)
            throw new SecurityException("unknown station name or bad password");

        if (password.equals(model.getPassword())) {
            HttpProtocol.sendMessage(socketChannel, buffer, HttpProtocol.getOkMessage());

            if (connectedStation != null) {
                connectedStation.setNewSocket(socketChannel);
            } else {
                new GnssStation(socketChannel, model);
            }

            return;
        } else {
            throw new SecurityException("unknown station name or bad password");
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
