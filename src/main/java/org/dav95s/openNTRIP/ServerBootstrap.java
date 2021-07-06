package org.dav95s.openNTRIP;


import org.dav95s.openNTRIP.Databases.DataSource;
import org.dav95s.openNTRIP.Databases.Models.NtripCasterModel;
import org.dav95s.openNTRIP.Databases.Models.ReferenceStationModel;
import org.dav95s.openNTRIP.Servers.NtripCaster;
import org.dav95s.openNTRIP.Servers.ReferenceStation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ServerBootstrap {
    private final Logger logger = LoggerFactory.getLogger(ServerBootstrap.class.getName());
    private final Timer timer = new Timer("ThreadServerUpdater");

    private static ServerBootstrap instance;

    private ServerBootstrap() {
    }

    public static ServerBootstrap getInstance() {
        if (instance == null) {
            instance = new ServerBootstrap();
        }
        return instance;
    }

    public void start() {
        timer.schedule(referenceStationCreator, 0, 5000);
        timer.schedule(casterCreator, 0, 7500);
    }

    private final Set<ReferenceStation> referenceStations = new HashSet<>();

    private final TimerTask referenceStationCreator = new TimerTask() {
        @Override
        public void run() {
            ArrayList<Integer> idList = getAllReferenceStation();
            for (Integer id : idList) {
                ReferenceStation rs = getReferenceStationById(id);
                if (getReferenceStationById(id) == null) {
                    addNewReferenceStation(id);
                } else {
                    refreshReferenceStation(rs);
                }
            }
        }
    };

    private void refreshReferenceStation(ReferenceStation rs) {
        try {
            rs.refresh();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            referenceStations.remove(rs);
        }
    }

    private void addNewReferenceStation(Integer id) {
        try {
            ReferenceStation newStation = new ReferenceStation(new ReferenceStationModel(id));
            referenceStations.add(newStation);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private ArrayList<Integer> getAllReferenceStation() {
        String sql = "SELECT `id` FROM reference_stations";
        ArrayList<Integer> listId = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    listId.add(rs.getInt("id"));
                }
            }

        } catch (SQLException e) {
            logger.error("Database connection error", e);
        }
        return listId;
    }

    public ReferenceStation getReferenceStationById(int id) {
        for (ReferenceStation station : referenceStations) {
            if (station.getId() == id)
                return station;
        }
        return null;
    }

    public ReferenceStation getReferenceStationByName(String name) throws IllegalArgumentException {
        for (ReferenceStation station : referenceStations) {
            if (station.getName().equals(name))
                return station;
        }
        throw new IllegalArgumentException();
    }

    private final ArrayList<NtripCaster> casters = new ArrayList<>();

    private final TimerTask casterCreator = new TimerTask() {
        @Override
        public void run() {
            ArrayList<Integer> idList = readIdALlCasters();
            for (Integer id : idList) {
                NtripCaster caster = getCasterById(id);
                if (caster == null) {
                    addNewCater(id);
                } else {
                    refreshCaster(caster);
                }
            }
        }
    };

    private void refreshCaster(NtripCaster caster) {
        try {
            caster.refresh();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void addNewCater(Integer id) {
        try {
            casters.add(new NtripCaster(new NtripCasterModel(id)));
        } catch (IOException | SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public void removeCaster(NtripCaster caster) {
        casters.remove(caster);
    }

    public NtripCaster getCasterById(int id) {
        for (NtripCaster caster : casters) {
            if (caster.getId() == id) {
                return caster;
            }
        }
        return null;
    }


    private ArrayList<Integer> readIdALlCasters() {
        String sql = "SELECT `id` FROM casters";
        ArrayList<Integer> listId = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    listId.add(rs.getInt("id"));
                }
            }

        } catch (SQLException e) {
            logger.error("Can't read data from database!", e);
        }

        return listId;
    }


}
