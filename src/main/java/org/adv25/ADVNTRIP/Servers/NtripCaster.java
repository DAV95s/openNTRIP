package org.adv25.ADVNTRIP.Servers;

import org.adv25.ADVNTRIP.Clients.Authentication.Authentication;
import org.adv25.ADVNTRIP.Clients.Client;
import org.adv25.ADVNTRIP.Databases.DAO.MountPointDAO;
import org.adv25.ADVNTRIP.Databases.DAO.NtripCasterDAO;
import org.adv25.ADVNTRIP.Databases.Models.MountPointModel;
import org.adv25.ADVNTRIP.Databases.Models.NtripCasterModel;
import org.adv25.ADVNTRIP.Network.NetworkProcessor;
import org.adv25.ADVNTRIP.Tools.NMEA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.*;

public class NtripCaster {
    final static private Logger logger = LogManager.getLogger(NtripCaster.class.getName());

    /* static block*/
    private static Timer timer = new Timer();

    private static Map<Integer, NtripCaster> ntripCasters = new HashMap<>();

    //***** model updaters
    private TimerTask updateModel = new TimerTask() {
        @Override
        public void run() {
            NtripCasterDAO dao = new NtripCasterDAO();
            NtripCasterModel cas_model = dao.read(model.getId());
            if (cas_model == null)
                close();
            model = cas_model;
        }
    };

    private TimerTask updateMountPoints = new TimerTask() {
        @Override
        public void run() {
            MountPointDAO dao = new MountPointDAO();
            mountPoints = dao.getAllByCasterId(model.getId());
        }
    };
    /* static block*/

    /* fields */
    private SelectionKey selectionKey;
    private ServerSocketChannel serverChannel;
    protected ArrayList<MountPointModel> mountPoints = new ArrayList<>();
    private NtripCasterModel model;
    /* fields */

    public NtripCaster(NtripCasterModel model) throws IOException {
        this.model = model;

        ntripCasters.put(model.getId(), this);

        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.bind(new InetSocketAddress(model.getPort()));
        this.serverChannel.configureBlocking(false);

        this.selectionKey = NetworkProcessor.getInstance().registerChannel(serverChannel, this);

        timer.schedule(updateModel, 10_000, 10_000);
        timer.schedule(updateMountPoints, 0, 10_000);
        logger.info("NtripCaster :" + model.getPort() + " has been initiated!");
    }

    public static NtripCaster getCasterById(int i) {
        return ntripCasters.get(i);
    }

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

    private MountPointModel getMountpoint(String mountpoint) {
        for (MountPointModel mp_model : mountPoints) {
            if (mp_model.getMountpoint().equals(mountpoint))
                return mp_model;
        }
        return null;
    }

    private byte[] sourceTable() {
        String header = "SOURCETABLE 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n";

        String body = "";

        for (MountPointModel mountPoint : this.mountPoints) {
            body += mountPoint.toString();
        }

        body += "ENDSOURCETABLE\r\n";
        header += "Content-Length: " + body.getBytes().length + "\r\n\n";

        return (header + body).getBytes();
    }

    // This method will be call on every nmea message from a client.
    public void clientAuthorizationProcessing(Client client) throws IOException {
        logger.debug(client.getHttpHeader("GET"));
        MountPointModel requestedMountPoint = getMountpoint(client.getHttpHeader("GET"));

        //requested mountpoint not exists. Send sourcetable.
        if (requestedMountPoint == null) {
            client.sendMessageAndClose(sourceTable());
            logger.debug("Caster " + model.getPort() + ": MountPoint " + client.getHttpHeader("GET") + " is not exists!");
            return;
        }

        client.setMountPoint(requestedMountPoint);

        //mountpoint can be off
        if (!requestedMountPoint.isAvailable()) {
            logger.info("Caster " + model.getPort() + ": MountPoint " + requestedMountPoint.getMountpoint() + " is not available!");
            client.safeClose();
            return;
        }

        //Authentication
        Authentication authentication = requestedMountPoint.getAuthentication();
        logger.debug("Caster " + model.getPort() + ": MountPoint " + requestedMountPoint.getMountpoint() + " authentication " + authentication);
        if (!authentication.start(client)) {
            logger.info("Caster " + model.getPort() + ": MountPoint " + requestedMountPoint.getMountpoint() + " bad password!");
            client.sendMessageAndClose(Client.BAD_MESSAGE);
            return;
        }

        //Selecting reference station
        if (requestedMountPoint.isNmea()) {
            if (client.getPosition() == null)
                logger.debug("waiting for client position");

            ReferenceStation referenceStation = getNearestReferenceStation(requestedMountPoint, client);

            if (referenceStation != null)
                client.setReferenceStation(referenceStation);
            else
                logger.debug("no suitable reference station");
        } else {
            ReferenceStation referenceStation = getReferenceStation(requestedMountPoint);

            if (referenceStation != null)
                client.setReferenceStation(referenceStation);
            else
                logger.debug("no suitable reference station");
        }
    }

    protected ReferenceStation getReferenceStation(MountPointModel model) {
        ArrayList<ReferenceStation> referenceStations = model.getBasesIds();
        if (referenceStations.size() == 0)
            logger.error(model.getMountpoint() + " have not reference station!");

        if (referenceStations.size() > 1)
            logger.error("MountPoint " + model.getMountpoint() + " have more than one ref station but nmea off.");

        for (ReferenceStation referenceStation : referenceStations) {
            if (referenceStation.available)
                return referenceStation;
        }
        return null;
    }

    protected ReferenceStation getNearestReferenceStation(MountPointModel model, Client client) {
        ArrayList<ReferenceStation> referenceStations = model.getBasesIds();
        TreeMap<Float, ReferenceStation> sortedRange = new TreeMap<>();
        NMEA.GPSPosition clientPosition = client.getPosition();

        for (ReferenceStation station : referenceStations) {
            if (station.available)
                sortedRange.put(station.getPosition().distance(clientPosition), station);
        }

        if (sortedRange.size() > 0) {
            logger.info("Nearest reference station " + sortedRange.firstEntry());
            return sortedRange.firstEntry().getValue();
        }

        return null;
    }
}
