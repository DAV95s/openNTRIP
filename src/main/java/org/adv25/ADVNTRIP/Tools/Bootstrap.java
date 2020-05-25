package org.adv25.ADVNTRIP.Tools;

import org.adv25.ADVNTRIP.Databases.DAO.StationDAO;

public class Bootstrap {

    public Bootstrap() {
        StationDAO stationDAO = new StationDAO();
        stationDAO.setAllOffline();
    }
}
