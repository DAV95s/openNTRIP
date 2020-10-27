package org.dav95s.openNTRIP.Tools;

import org.dav95s.openNTRIP.Databases.DAO.NtripCasterDAO;
import org.dav95s.openNTRIP.Databases.DAO.ReferenceStationDAO;
import org.dav95s.openNTRIP.Databases.Models.NtripCasterModel;
import org.dav95s.openNTRIP.Databases.Models.ReferenceStationModel;

import org.dav95s.openNTRIP.Servers.NtripCaster;
import org.dav95s.openNTRIP.Servers.ReferenceStation;
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
        Config.getInstance();
        timer.schedule(refStationInit, 0, 15_000);
        timer.schedule(casterInit, 0, 15_000);
    }

    //add new ref station, if added in db
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

    //add new caster, if added in db
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
