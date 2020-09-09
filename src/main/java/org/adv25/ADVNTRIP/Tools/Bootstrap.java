package org.adv25.ADVNTRIP.Tools;

import org.adv25.ADVNTRIP.Databases.DAO.NtripCasterDAO;
import org.adv25.ADVNTRIP.Databases.DAO.ReferenceStationDAO;
import org.adv25.ADVNTRIP.Databases.Models.NtripCasterModel;
import org.adv25.ADVNTRIP.Databases.Models.ReferenceStationModel;

import org.adv25.ADVNTRIP.Servers.NtripCaster;
import org.adv25.ADVNTRIP.Servers.ReferenceStation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Bootstrap {
    final static private Logger logger = LogManager.getLogger(Bootstrap.class.getName());

    private static Bootstrap instance;

    public static Bootstrap getInstance() {
        if (instance == null)
            instance = new Bootstrap();

        return instance;
    }

    private Timer timer = new Timer();

    private Bootstrap() {
        timer.schedule(refStationInit, 0, 15_000);
        timer.schedule(casterInit, 0, 15_000);
    }

    TimerTask refStationInit = new TimerTask() {
        @Override
        public void run() {
            ReferenceStationDAO dao = new ReferenceStationDAO();
            ArrayList<ReferenceStationModel> refStations = dao.readAll();

            for (ReferenceStationModel model : refStations) {
                if (ReferenceStation.getStationById(model.getId()) == null)
                    new ReferenceStation(model);
            }
        }
    };

    TimerTask casterInit = new TimerTask() {
        @Override
        public void run() {
            ArrayList<NtripCasterModel> casters = new NtripCasterDAO().readAll();

            for (NtripCasterModel model : casters) {
                if (NtripCaster.getCasterById(model.getId()) == null) {
                    try {
                        new NtripCaster(model);
                    } catch (IOException e) {
                        logger.error(e);
                    }
                }
            }
        }
    };
}
