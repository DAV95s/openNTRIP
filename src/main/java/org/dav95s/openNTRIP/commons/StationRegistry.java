package org.dav95s.openNTRIP.commons;

import org.dav95s.openNTRIP.core.referenceStation.ReferenceStation;
import org.dav95s.openNTRIP.exception.ReferenceStationNotFoundException;
import org.dav95s.openNTRIP.utils.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StationRegistry {
    private final static Logger logger = LoggerFactory.getLogger(StationRegistry.class.getName());

    private final Map<String, ReferenceStation> stationMap = new HashMap<>();
    private final ServerProperties serverProperties;

    public StationRegistry(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    public void addReferenceStation(String name, ReferenceStation referenceStation) {
        stationMap.put(name, referenceStation);
    }

    public void removeReferenceStation(String name) {
        stationMap.remove(name);
    }

    public ReferenceStation getNearestReferenceStationByPool(double lat, double lon, String[] pool) {
        return null;
    }

    public ReferenceStation getReferenceStationByPool(ArrayList<String> pool) throws ReferenceStationNotFoundException {
        String key = pool.stream().filter(stationMap::containsKey).findFirst().orElseThrow(ReferenceStationNotFoundException::new);
        return stationMap.get(key);
    }

}
