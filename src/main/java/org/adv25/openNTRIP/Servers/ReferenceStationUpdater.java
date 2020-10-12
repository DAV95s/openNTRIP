package org.adv25.openNTRIP.Servers;

import org.adv25.openNTRIP.Databases.DAO.ReferenceStationDAO;
import org.adv25.openNTRIP.Databases.Models.ReferenceStationModel;
import org.adv25.openNTRIP.Spatial.PointLla;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public abstract class ReferenceStationUpdater {
    final static private Logger logger = LogManager.getLogger(ReferenceStationUpdater.class.getName());
    protected static ReferenceStationDAO dao = new ReferenceStationDAO();
    protected static Map<String, ReferenceStation> refStations = new HashMap<>();

    public static ReferenceStation getStationByName(String name) {
        return refStations.get(name);
    }

    public static ReferenceStation getStationById(int id) {
        for (ReferenceStation station : refStations.values()) {
            if (station.getId() == id)
                return station;
        }
        return null;
    }

    protected void rawDataType() {
        this.updateModel.cancel();
        model.setFormat("RAW");
        model.setLla(new PointLla(0, 0));
        model.setCountry("");
        model.setIdentifier("");
        model.setNav_system("");
        model.update();
    }

    protected static Timer timer = new Timer();
    protected ReferenceStationModel model;


    protected TimerTask updateModel = new TimerTask() {
        @Override
        public void run() {
            ReferenceStationDAO dao = new ReferenceStationDAO();
            ReferenceStationModel ref_model = dao.read(model.getId());
            if (ref_model == null)
                remove();

            model = ref_model;

            logger.info("RefStation: " + model.getName() + " update model.");
        }
    };

    protected void remove() {
    }
}
