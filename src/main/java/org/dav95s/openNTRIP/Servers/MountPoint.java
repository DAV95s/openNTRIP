package org.dav95s.openNTRIP.Servers;

import org.dav95s.openNTRIP.Clients.Authentication.IAuthenticator;
import org.dav95s.openNTRIP.Clients.User;
import org.dav95s.openNTRIP.Databases.Models.MountPointModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

public class MountPoint {
    final static private Logger logger = LoggerFactory.getLogger(MountPoint.class.getName());

    final private MountPointModel model;

    final private ArrayList<User> users = new ArrayList<>();
    final private HashMap<Integer, ReferenceStation> referenceStation = new HashMap<>();

    final private Crs crs;

    public MountPoint(MountPointModel model, HashMap<Integer, ReferenceStation> referenceStations) {
        this.model = model;
        this.model.start();
        this.crs = new Crs(model);
    }

    public void close() {
        this.model.stop();
        this.users.forEach(User::close);
    }

    public void clientAuthorization(User user) throws IOException, IllegalAccessException {
        //available
        if (!model.isAvailable()) {
            logger.debug(this.model.getName() + " is not available.");
            user.close();
            return;
        }

        //authentication
        if (!user.authenticated) {
            IAuthenticator authenticator = model.getAuthenticator();
            if (!authenticator.authentication(user)) {
                user.sendBadMessageAndClose();
                return;
            }
            user.sendOkMessage();
            users.add(user);
            user.authenticated = true;
            crs.subscribeNewUser(user);
        }
        updateReferenceStation(user);
    }

    public void updateReferenceStation(User user) throws IllegalAccessException {
        if (model.isNmea()) {
            user.subscribe(getNearestStation(user));
        } else {
            Optional<ReferenceStation> rs = referenceStation.values().stream()
                    .filter(ReferenceStation::isOnline)
                    .findFirst();
            user.subscribe(rs.orElse(null));
        }
    }

    public void epochEventListener(User user) {
        System.out.println("EPOCH EVENT!");
        crs.msgProcess(user);
    }

    private ReferenceStation getNearestStation(User user) {
        if (!user.getPosition().isSet()) {
            logger.info("MountPoint " + model.getName() + ": user - " + user + "doesn't have location");
            return null;
        }

//        if (model.getStationsPool().isEmpty()) {
//            throw new IllegalAccessException(model.getName() + " doesn't have reference stations.");
//        }

        return referenceStation.values().stream()
                .filter(ReferenceStation::isOnline)
                .min(Comparator.comparing(r -> r.distance(user)))
                .orElse(null);
    }

    public void removeClient(User user) {
        this.users.remove(user);
    }

    @Override
    public String toString() {
        return model.toString();
    }
}


