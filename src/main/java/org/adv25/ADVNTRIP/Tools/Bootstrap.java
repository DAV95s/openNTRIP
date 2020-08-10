package org.adv25.ADVNTRIP.Tools;

import org.adv25.ADVNTRIP.Databases.DAO.CastersDAO;
import org.adv25.ADVNTRIP.Databases.DAO.MountPointDAO;
import org.adv25.ADVNTRIP.Databases.Models.CasterModel;
import org.adv25.ADVNTRIP.Servers.Caster;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Bootstrap {

    private static Bootstrap instance;

    public static Bootstrap getInstance() {
        if (instance == null)
            instance = new Bootstrap();

        return instance;
    }

    private Timer timer = new Timer();

    private Bootstrap() {
        timer.schedule(casterInit, 0);
    }

    TimerTask casterInit = new TimerTask() {

        private boolean checkPort(int port) {
            for (Caster caster : Caster.casters) {
                if (caster.getPort() == port)
                    return false;
            }
            return true;
        }

        @Override
        public void run() {
            ArrayList<CasterModel> casters = new CastersDAO().readAll();

            for (CasterModel casterModel : casters) {
                new Caster(casterModel);
            }
        }
    };
}
