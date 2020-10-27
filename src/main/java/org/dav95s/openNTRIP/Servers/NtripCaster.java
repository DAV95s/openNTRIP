package org.dav95s.openNTRIP.Servers;

import org.dav95s.openNTRIP.Clients.Authentication.Authenticator;
import org.dav95s.openNTRIP.Clients.Client;
import org.dav95s.openNTRIP.Databases.DAO.MountPointDAO;
import org.dav95s.openNTRIP.Databases.DAO.NtripCasterDAO;
import org.dav95s.openNTRIP.Databases.Models.MountPointModel;
import org.dav95s.openNTRIP.Databases.Models.NtripCasterModel;
import org.dav95s.openNTRIP.Network.NetworkProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class NtripCaster {
    final static private Logger logger = LogManager.getLogger(NtripCaster.class.getName());

    final static private Timer timer = new Timer();
    final static private HashMap<Integer, NtripCaster> ntripCasters = new HashMap<>();

    public static NtripCaster getCasterById(int i) {
        return ntripCasters.get(i);
    }

    final private TimerTask updateModel = new TimerTask() {
        @Override
        public void run() {
            NtripCasterDAO dao = new NtripCasterDAO();
            NtripCasterModel cas_model = dao.read(model.getId());
            if (cas_model == null)
                close();
            model = cas_model;
            logger.info("Caster: " + model.getPort() +" update model.");
        }
    };

    final private TimerTask updateMountPoints = new TimerTask() {
        @Override
        public void run() {
            MountPointDAO dao = new MountPointDAO();
            mountPoints = dao.getAllByCasterId(model.getId());
            logger.info("Caster: " + model.getPort() +" update mountpoints.");
        }
    };

    final private ServerSocketChannel serverChannel;
    private NtripCasterModel model;
    private HashMap<String, MountPointModel> mountPoints = new HashMap<>();

    public NtripCaster(NtripCasterModel model) throws IOException {
        this.model = model;

        ntripCasters.put(model.getId(), this);

        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.bind(new InetSocketAddress(model.getPort()));
        this.serverChannel.configureBlocking(false);

        NetworkProcessor.getInstance().registerServerChannel(serverChannel, this);

        timer.schedule(updateModel, 10_000, 10_000);
        timer.schedule(updateMountPoints, 0, 10_000);
        logger.info("NtripCaster :" + model.getPort() + " has been initiated!");
    }

    public void close() {
        try {
            this.updateMountPoints.cancel();
            ntripCasters.remove(model.getId());
            serverChannel.close();
        } catch (IOException e) {
            logger.warn(e);
        }
    }

    private byte[] sourceTable() {
        String header = "SOURCETABLE 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n";

        StringBuilder body = new StringBuilder();
        for (MountPointModel mountPoint : this.mountPoints.values()) {
            body.append(mountPoint.toString());
        }

        body.append("ENDSOURCETABLE\r\n");
        String bodyString = body.toString();
        header += "Content-Length: " + bodyString.getBytes().length + "\r\n\n";

        return (header + bodyString).getBytes();
    }

    /**
     * This method will be call on get request and after NMEA message from a client.
     *
     * @param client
     * @throws IOException
     */
    public void clientAuthorizationProcessing(Client client) throws IOException {
        MountPointModel requestedMountPoint = this.mountPoints.get(client.getHttpHeader("GET"));
        logger.debug(client.toString() + " requested mountpoint " + client.getHttpHeader("GET"));

        //requested mountpoint not exists. Send sourcetable.
        if (requestedMountPoint == null) {
            client.write(ByteBuffer.wrap(sourceTable()));
            client.close();
            logger.debug("Caster " + model.getPort() + ": MountPoint " + client.getHttpHeader("GET") + " is not exists!");
            return;
        }

        //Is available?
        if (!requestedMountPoint.isAvailable()) {
            logger.info("Caster " + model.getPort() + ": MountPoint " + requestedMountPoint.getMountpoint() + " is not available!");
            client.close();
            return;
        }

        //authentication
        Authenticator authenticator = requestedMountPoint.getAuthenticator();
        logger.debug("Caster " + model.getPort() + ": MountPoint " + requestedMountPoint.getMountpoint() + " authenticator " + authenticator);
        if (!authenticator.authentication(client)) {
            logger.info("Caster " + model.getPort() + ": MountPoint " + requestedMountPoint.getMountpoint() + " bad password!");
            client.sendBadMessageAndClose();
            return;
        }

        client.sendOkMessage();

        //Selecting reference station
        if (requestedMountPoint.isNmea()) {
            try {
                client.subscribe(requestedMountPoint.getNearestReferenceStation(client));
            } catch (IllegalStateException e) {
                logger.debug(e);
            }
        } else {
            ReferenceStation station = requestedMountPoint.getReferenceStation();
            logger.debug(station.model.getName());
            client.subscribe(station);
        }
    }
}
