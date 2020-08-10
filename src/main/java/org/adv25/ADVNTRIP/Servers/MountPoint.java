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

    public MountPoint(MountPointModel model, Caster parentCaster) {
        this.model = model;
        this.parentCaster = parentCaster;
        logger.info("Mountpoint " + model.getMountpoint() + " has been set.");
    }

    synchronized public void clientAuthorization(Client client) throws IOException {
        if (!isAvailable())
            return;

        Authentication auth = this.model.getAuthentication();

        //if bad password
        if (!auth.start(client)) {
            client.sendMessageAndClose(Client.BAD_MESSAGE);
            return;
        }

        client.setMountPoint(this);

        //if nmea connect off
        if (!model.isNmea()) {
            ReferenceStation referenceStation = ReferenceStation.getBase(Integer.parseInt(model.getBasesIds()));
            client.setReferenceStation(referenceStation);
            referenceStation.addListener(client);
            client.sendMessage(Client.OK_MESSAGE);
            return;
        }

        //if nmea connect on
        parentCaster.clientInput(client);

        if (!client.getPosition().isSet()) {
            nmeaClientQueue.add(client);
        }
    }

    //Clients are waiting for nmea message
    public static ArrayList<Client> nmeaClientQueue = new ArrayList();

    public boolean isAvailable() {
        return this.model.isAvailable();
    }

    @Override
    public String toString() {
        return "STR" + ';' + model.getMountpoint() + ';' + model.getIdentifier() + ';' + model.getFormat() + ';' + model.getFormatDetails() + ';' + model.getCarrier() + ';' + model.getNavSystem() + ';' + model.getNetwork() + ';' + model.getCountry()
                + ';' + String.format("%.2f", model.getLla().getLat()) + ';' + String.format("%.2f", model.getLla().getLon()) + ';' + (model.isNmea() ? 1 : 0) + ';' + (model.isSolution() ? 1 : 0) + ';' + model.getGenerator() + ';' + model.getCompression()
                + ';' + model.getAuthentication().toString() + ';' + (model.isFee() ? 'Y' : 'N') + ';' + model.getBitrate() + ';' + model.getMisc() + "\r\n";
    }

}
