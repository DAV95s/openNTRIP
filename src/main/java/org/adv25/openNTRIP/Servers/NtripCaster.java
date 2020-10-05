package org.adv25.openNTRIP.Servers;

import org.adv25.openNTRIP.Clients.Authentication.Authenticator;
import org.adv25.openNTRIP.Clients.Client;
import org.adv25.openNTRIP.Databases.Models.MountPointModel;
import org.adv25.openNTRIP.Databases.Models.NtripCasterModel;
import org.adv25.openNTRIP.Network.NetworkProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;

public class NtripCaster extends NtripCasterUpdater {
    final static private Logger logger = LogManager.getLogger(NtripCaster.class.getName());

    private static HashMap<Integer, NtripCaster> ntripCasters = new HashMap<>();

    private SelectionKey selectionKey;
    private ServerSocketChannel serverChannel;

    public NtripCaster(NtripCasterModel model) throws IOException {
        super.model = model;

        ntripCasters.put(model.getId(), this);

        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.bind(new InetSocketAddress(model.getPort()));
        this.serverChannel.configureBlocking(false);

        this.selectionKey = NetworkProcessor.getInstance().registerServerChannel(serverChannel, this);

        timer.schedule(updateModel, 10_000, 10_000);
        timer.schedule(updateMountPoints, 0, 10_000);
        logger.info("NtripCaster :" + model.getPort() + " has been initiated!");
    }

    public static NtripCaster getCasterById(int i) {
        return ntripCasters.get(i);
    }

    @Override
    public void close() {
        try {
            this.updateMountPoints.cancel();
            NtripCaster.ntripCasters.remove(model.getId());
            selectionKey.cancel();
            serverChannel.close();
        } catch (IOException e) {
            logger.warn(e);
        }
    }

    private byte[] sourceTable() {
        String header = "SOURCETABLE 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n";

        String body = "";

        for (MountPointModel mountPoint : super.mountPoints.values()) {
            body += mountPoint.toString();
        }

        body += "ENDSOURCETABLE\r\n";
        header += "Content-Length: " + body.getBytes().length + "\r\n\n";

        return (header + body).getBytes();
    }

    /**
     * This method will be call on get request and after NMEA message from a client.
     *
     * @param client
     * @throws IOException
     */
    public void clientAuthorizationProcessing(Client client) throws IOException {
        logger.debug(client.getHttpHeader("GET"));
        MountPointModel requestedMountPoint = super.mountPoints.get(client.getHttpHeader("GET"));

        //requested mountpoint not exists. Send sourcetable.
        if (requestedMountPoint == null) {
            client.sendMessageAndClose(sourceTable());
            logger.debug("Caster " + model.getPort() + ": MountPoint " + client.getHttpHeader("GET") + " is not exists!");
            return;
        }

        //mountpoint can be off
        if (!requestedMountPoint.isAvailable()) {
            logger.info("Caster " + model.getPort() + ": MountPoint " + requestedMountPoint.getMountpoint() + " is not available!");
            client.close();
            return;
        }

        client.setMountPoint(requestedMountPoint);

        //authentication
        Authenticator authenticator = requestedMountPoint.getAuthenticator();
        logger.debug("Caster " + model.getPort() + ": MountPoint " + requestedMountPoint.getMountpoint() + " authenticator " + authenticator);
        if (!authenticator.authentication(client)) {
            logger.info("Caster " + model.getPort() + ": MountPoint " + requestedMountPoint.getMountpoint() + " bad password!");
            client.sendMessageAndClose(Client.BAD_MESSAGE);
            return;
        }

        //Selecting reference station
        if (requestedMountPoint.isNmea()) {
            try {
                client.subscribe(requestedMountPoint.getNearestReferenceStation(client));
            } catch (IllegalStateException e) {
                logger.debug("Let's try another time!");
            }
        } else {
            ReferenceStation ref = requestedMountPoint.getReferenceStation();
            logger.debug(ref.model.getName());
            client.subscribe(ref);
        }
    }
}
