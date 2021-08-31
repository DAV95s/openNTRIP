package org.dav95s.openNTRIP;

import org.dav95s.openNTRIP.Databases.Models.BootstrapModel;
import org.dav95s.openNTRIP.Databases.Models.ReferenceStationModel;
import org.dav95s.openNTRIP.Network.NetworkCore;
import org.dav95s.openNTRIP.Servers.NtripCaster;
import org.dav95s.openNTRIP.Servers.ReferenceStation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Bootstrap {
    private final static Logger logger = LoggerFactory.getLogger(Bootstrap.class.getName());

    private final BootstrapModel model = new BootstrapModel();

    private final Timer timer = new Timer("MainCreator");

    private final HashMap<Integer, ReferenceStation> referenceStations = new HashMap<>();
    private final Map<Integer, NtripCaster> casters = new HashMap<>();

    private final NetworkCore networkCore = new NetworkCore();
    private final Bootstrap bootstrap = this;

    public void start() {
        timer.schedule(createReferenceStations, 0, 5000);
        timer.schedule(createCasters, 0, 7500);
    }

    public ReferenceStation getReferenceStationByIds(int id) {
        return referenceStations.get(id);
    }

    public ReferenceStation getReferenceStationByName(String name) {
        return referenceStations.values().stream()
                .filter(rs -> rs.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    TimerTask createCasters = new TimerTask() {
        @Override
        public void run() {
            List<Integer> collect = model.getAllIdOfCasters().stream()
                    .filter(id -> !casters.containsKey(id))
                    .collect(Collectors.toList());

            collect.forEach(id->{
                try {
                    casters.put(id, new NtripCaster(id, networkCore, bootstrap));
                } catch (IOException e) {
                    logger.error("Can't init new caster, ID=" + id, e);
                }
            });
        }
    };

    TimerTask createReferenceStations = new TimerTask() {
        @Override
        public void run() {
            List<Integer> collect = model.getAllIdsOfReferenceStations().stream()
                    .filter(id -> !referenceStations.containsKey(id))
                    .collect(Collectors.toList());

            collect.forEach(id -> referenceStations.put(id, new ReferenceStation(new ReferenceStationModel(id))));
        }
    };

    public HashMap<Integer, ReferenceStation> getReferenceStations() {
        return referenceStations;
    }

    public void removeCaster(int id) {
        casters.remove(id);
    }
}
