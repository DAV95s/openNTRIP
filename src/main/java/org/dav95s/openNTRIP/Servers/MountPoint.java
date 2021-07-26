package org.dav95s.openNTRIP.Servers;

import org.dav95s.openNTRIP.Clients.Authentication.IAuthenticator;
import org.dav95s.openNTRIP.Clients.User;
import org.dav95s.openNTRIP.Databases.Models.MountPointModel;
import org.dav95s.openNTRIP.Tools.Observer.IObserver;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class MountPoint implements IObserver {
    final static private Logger logger = LoggerFactory.getLogger(MountPoint.class.getName());

    final private ConcurrentHashMap<ReferenceStation, CopyOnWriteArraySet<User>> stationPool = new ConcurrentHashMap<>();

    final private MountPointModel model;

    final private Crs crs;

    public MountPoint(MountPointModel model) {
        this.model = model;
        this.model.getStationsPool().forEach(st -> stationPool.put(st, new CopyOnWriteArraySet<>()));
        this.stationPool.keySet().forEach(rs -> rs.registerObserver(this));
        this.crs = new Crs(model.getId(), this);
    }

    public String getName() {
        return model.getName();
    }

    public void clientAuthorization(User user) throws IOException, IllegalAccessException {

        if (logger.isDebugEnabled()) {
            JSONObject object = new JSONObject();
            object.put("input", user.toString());
            object.put("isAvailable", model.isAvailable());
            object.put("authenticator", model.getAuthenticator());
            object.put("pool", Arrays.toString(model.getStationsPool().toArray()));
            logger.debug(object.toString());
        }

        //available
        if (!model.isAvailable()) {
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
            user.authenticated = true;
            if (crs.isSet()) {
                crs.newUser(user);
            }
        }

        addClient(user);
    }

    public void updateReferenceStationByUser(User user) throws IllegalAccessException {
//        for (Map.Entry<ReferenceStation, CopyOnWriteArraySet<User>> entry : stationPool.entrySet()) {
//            if (entry.getValue().contains(user)) {
//                ReferenceStation nearestStation = getNearestStation(user);
//                if (nearestStation != entry.getKey() && nearestStation.isOnline()) {
//                    this.removeClient(user);
//                    this.stationPool.get(nearestStation).add(user);
//                }
//            }
//        }
    }


    public void addClient(User user) throws IllegalAccessException {
        if (model.isNmea()) {
            ReferenceStation nearest = getNearestStation(user);

            if (nearest != null) {
                stationPool.get(nearest).add(user);
            }//or find in next time
            return;
        } else {
            Optional<ReferenceStation> rs = stationPool.keySet().stream().filter(ReferenceStation::isOnline).findFirst();
            if (rs.isPresent()) {
                stationPool.get(rs).add(user);
            } else {
                throw new IllegalAccessException(model.getName() + " Doesn't have accessibly ReferenceStation.");
            }
//            for (Map.Entry<ReferenceStation, CopyOnWriteArraySet<User>> entry : stationPool.entrySet()) {
//                if (entry.getKey().isOnline()) {
//                    entry.getValue().add(user);
//                    return;
//                }
//            }
        }
        throw new IllegalAccessException(model.getName() + " Doesn't have accessibly ReferenceStation.");
    }

    private ReferenceStation getNearestStation(User user) throws IllegalAccessException {
        if (!user.getPosition().isSet()) {
            logger.info("MountPoint " + model.getName() + ": user - " + user + "doesn't have location");
            return null;
        }

        if (stationPool.isEmpty()) {
            throw new IllegalAccessException(model.getName() + " doesn't have reference stations.");
        }

//        Optional<ReferenceStation> optCandidate = stationPool.keySet().stream().filter(ReferenceStation::isOnline).findFirst();
//
//        if (optCandidate.isEmpty()) {
//            throw new IllegalAccessException(model.getName() + " doesn't have online reference stations.");
//        }

        return stationPool.keySet().stream()
                .filter(ReferenceStation::isOnline)
                .min(Comparator.comparing(r -> r.distance(user)))
                .get();

//        ReferenceStation candidate = optCandidate.get();
//
//        float dist = candidate.distance(user);
//
//        for (ReferenceStation referenceStation : stationPool.keySet()) {
//            if (referenceStation.isOnline()) {
//                float newDist = referenceStation.distance(user);
//                if (newDist < dist) {
//                    candidate = referenceStation;
//                    dist = newDist;
//                }
//            }
//        }
//        return candidate;
    }

    public void removeClient(User user) {
        for (Map.Entry<ReferenceStation, CopyOnWriteArraySet<User>> entry : stationPool.entrySet()) {
            entry.getValue().remove(user);
        }
    }

    private void updateStationPool() {
//        ArrayList<ReferenceStation> fromDB = model.getStationsPool();
//        for (ReferenceStation rs : fromDB) {
//            if (!stationPool.containsKey(rs)) {
//                stationPool.put(rs, new CopyOnWriteArraySet<>());
//            }
//        }
//
//        for (ReferenceStation rs : stationPool.keySet()) {
//            if (!fromDB.contains(rs)) {
//                stationPool.get(rs).forEach(User::close);
//                stationPool.remove(rs);
//            }
//        }
    }

    public void refresh() {
        this.model.read();
        this.model.readAccessibleReferenceStations();
        updateStationPool();
    }

    @Override
    public String toString() {
        return model.toString();
    }

    @Override
    public void notify(ReferenceStation referenceStation, ByteBuffer buffer) {
        CopyOnWriteArraySet<User> users = stationPool.get(referenceStation);
        for (User user : users) {
            if (crs != null)
                crs.run(user);

            try {
                buffer.flip();
                user.write(buffer);
            } catch (IOException e) {
                user.close();
            }
        }
    }

    public ConcurrentHashMap<ReferenceStation, CopyOnWriteArraySet<User>> getStationPool() {
        return stationPool;
    }
}


