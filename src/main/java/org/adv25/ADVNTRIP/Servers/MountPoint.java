package org.adv25.ADVNTRIP.Servers;

import org.adv25.ADVNTRIP.Clients.Authentication.Authentication;
import org.adv25.ADVNTRIP.Clients.Client;
import org.adv25.ADVNTRIP.Databases.Models.MountPointModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class MountPoint {
    final static private Logger logger = LogManager.getLogger(MountPoint.class.getName());

    MountPointModel model;
    Caster parentCaster;

    public MountPoint(MountPointModel model, Caster parentCaster) {
        this.model = model;
        this.parentCaster = parentCaster;
        logger.info("Mountpoint " + model.getMountpoint() + "has been set.");
        System.out.println(this.toString());
    }

    public byte[] injection(Client client, BaseStation baseStation) {

        return null;
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

        BaseStation baseStation = BaseStation.getBase(1);

        client.setBaseStation(baseStation);
        baseStation.addListener(client);

        if (model.isNmea()) {
            parentCaster.clientInput(client);
        }

        client.sendMessage(Client.OK_MESSAGE);
    }

    @Override
    public String toString() {
        return "STR" + ';' + model.getMountpoint() + ';' + model.getIdentifier() + ';' + model.getFormat() + ';' + model.getFormatDetails() + ';' + model.getCarrier() + ';' + model.getNavSystem() + ';' + model.getNetwork() + ';' + model.getCountry()
                + ';' + String.format("%.2f", model.getLla().getLat()) + ';' + String.format("%.2f", model.getLla().getLon()) + ';' + (model.isNmea() ? 1 : 0) + ';' + (model.isSolution() ? 1 : 0) + ';' + model.getGenerator() + ';' + model.getCompression()
                + ';' + model.getAuthentication().toString() + ';' + (model.isFee() ? 'Y' : 'N') + ';' + model.getBitrate() + ';' + model.getMisc() + "\r\n";
    }

}
