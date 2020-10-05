package org.adv25.openNTRIP.Tools;

import org.adv25.openNTRIP.Servers.ReferenceStation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class ReferenceStationsPack {
    final static private Logger logger = LogManager.getLogger(ReferenceStationsPack.class.getName());

    private HashMap<Integer, ReferenceStation> array = new HashMap<>();

    public void parseStationsId(String ids) {
        for (String id : ids.split(",")) {
            int intId = Integer.parseInt(id);
            array.put(intId, ReferenceStation.getStationById(Integer.parseInt(id)));
        }
    }


}
