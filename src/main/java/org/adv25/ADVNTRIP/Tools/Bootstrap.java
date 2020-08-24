package org.adv25.ADVNTRIP.Tools;

import org.adv25.ADVNTRIP.Databases.DAO.NtripCasterDAO;
import org.adv25.ADVNTRIP.Databases.DAO.ReferenceStationDAO;
import org.adv25.ADVNTRIP.Databases.Models.NtripCasterModel;
import org.adv25.ADVNTRIP.Databases.Models.ReferenceStationModel;

import org.adv25.ADVNTRIP.Servers.NtripCaster;
import org.adv25.ADVNTRIP.Servers.RefStation;
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
        timer.schedule(refStationInit, 0);
        timer.schedule(casterInit, 0);

    }

    TimerTask refStationInit = new TimerTask() {
        @Override
        public void run() {
            ReferenceStationDAO dao = new ReferenceStationDAO();
            ArrayList<ReferenceStationModel> refStations = dao.readAll();

            for (ReferenceStationModel model : refStations) {
                if (RefStation.getStationById(model.getId()) == null)
                    new RefStation(model);
            }
        }
    };

    TimerTask casterInit = new TimerTask() {
        @Override
        public void run() {
            ArrayList<NtripCasterModel> casters = new NtripCasterDAO().readAll();

            for (NtripCasterModel ntripCasterModel : casters) {
                if (NtripCaster.getCasterById(ntripCasterModel.getId()) == null) {
                    try {
                        new NtripCaster(ntripCasterModel);
                    } catch (IOException e) {
                        logger.error(e);
                    }
                }
            }
        }
    };
}
