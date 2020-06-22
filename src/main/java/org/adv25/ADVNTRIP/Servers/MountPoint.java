package org.adv25.ADVNTRIP.Servers;

import org.adv25.ADVNTRIP.Clients.Authentication.Authentication;
import org.adv25.ADVNTRIP.Clients.Client;
import org.adv25.ADVNTRIP.Databases.Models.MountPointModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class MountPoint {
    final static private Logger logger = LogManager.getLogger(MountPoint.class.getName());

    Caster parentCaster;
    MountPointModel model;
    BaseStation[] baseStationPool;

    public MountPoint(MountPointModel model, Caster parentCaster) {
        this.model = model;
        this.parentCaster = parentCaster;
        logger.info("Mountpoint " + model.getMountpoint() + " has been set.");

        String[] baseIds = this.model.getBasesIds().split(",");
        baseStationPool = new BaseStation[baseIds.length];

        for (int i = 0; i < baseIds.length; i++) {
            int temp = Integer.parseInt(baseIds[i]);
            baseStationPool[i] = BaseStation.getBase(temp);
        }

        logger.info("Mountpoint " + model.getMountpoint() + " " + Arrays.toString(baseStationPool));
    }

    public boolean isAvailable() {
        return this.model.isAvailable();
    }

    synchronized public void clientAuthorization(Client client) throws IOException {
        if (!isAvailable())
            return;

        Authentication auth = this.model.getAuthentication();

        if (!auth.start(client)) {
            client.sendMessageAndClose(Client.BAD_MESSAGE);
            return;
        }

        client.setMountPoint(this);

        if (!model.isNmea()) {
            BaseStation baseStation = BaseStation.getBase(1);
            client.setBaseStation(baseStation);
            baseStation.addListener(client);
            client.sendMessage(Client.OK_MESSAGE);
            return;
        }

        parentCaster.clientInput(client);

        if (!client.getPosition().isSet()) {
            nmeaClientQueue.add(client);
        }
    }

    //NMEA wait
    public static ArrayList<Client> nmeaClientQueue = new ArrayList();

    public void nmeaWait(Client client) {
        if (!client.getPosition().isSet())
            return;

        TreeMap<Float, BaseStation> distentions = new TreeMap<>();

        for (BaseStation base : baseStationPool) {
            float distention = base.getPosition().distance(client.getPosition());
            distentions.put(distention, base);
        }

        BaseStation selectedBs = distentions.firstEntry().getValue();
        selectedBs.addListener(client);

        nmeaClientQueue.remove(client);
    }

    @Override
    public String toString() {
        return "STR" + ';' + model.getMountpoint() + ';' + model.getIdentifier() + ';' + model.getFormat() + ';' + model.getFormatDetails() + ';' + model.getCarrier() + ';' + model.getNavSystem() + ';' + model.getNetwork() + ';' + model.getCountry()
                + ';' + String.format("%.2f", model.getLla().getLat()) + ';' + String.format("%.2f", model.getLla().getLon()) + ';' + (model.isNmea() ? 1 : 0) + ';' + (model.isSolution() ? 1 : 0) + ';' + model.getGenerator() + ';' + model.getCompression()
                + ';' + model.getAuthentication().toString() + ';' + (model.isFee() ? 'Y' : 'N') + ';' + model.getBitrate() + ';' + model.getMisc() + "\r\n";
    }

}
