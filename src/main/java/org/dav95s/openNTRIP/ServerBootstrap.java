package org.dav95s.openNTRIP;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dav95s.openNTRIP.Databases.DataSource;
import org.dav95s.openNTRIP.Databases.Models.NtripCasterModel;
import org.dav95s.openNTRIP.Databases.Models.ReferenceStationModel;
import org.dav95s.openNTRIP.Servers.NtripCaster;
import org.dav95s.openNTRIP.Servers.ReferenceStation;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ServerBootstrap {
    final static private Logger logger = LogManager.getLogger(ServerBootstrap.class.getName());

    private static ServerBootstrap instance;

    private static Timer timer = new Timer();

    private ServerBootstrap() {

    }

    public static void start() throws IOException, SQLException {
        timer.schedule(referenceStationCreator,0,5000 );
        timer.schedule(casterCreator,0,7500 );
    }

    private static ArrayList<NtripCaster> casters = new ArrayList<>();

    static TimerTask referenceStationCreator = new TimerTask() {
        @Override
        public void run() {
            ArrayList<Integer> idList = readAllReferenceStation();
            for (Integer id : idList) {
                if (getReferenceStationById(id) == null) {
                    try {
                        referenceStations.add(new ReferenceStation(new ReferenceStationModel(id)));
                    } catch (SQLException e) {
                        logger.error(e);
                    }
                }
            }
        }
    };

    static TimerTask casterCreator = new TimerTask() {
        @Override
        public void run() {
            ArrayList<Integer> idList = readIdALlCasters();
            for (Integer id : idList) {
                if (getCasterById(id) == null) {
                    try {
                        casters.add(new NtripCaster(new NtripCasterModel(id)));
                    } catch (IOException | SQLException e) {
                        logger.error(e);
                    }
                }
            }
        }
    };

    public static void removeCaster(NtripCaster caster) {
        casters.remove(caster);
    }

    public static NtripCaster getCasterById(int id) {
        for (NtripCaster caster : casters) {
            if (caster.getId() == id) {
                return caster;
            }
        }
        return null;
    }


    private static Set<ReferenceStation> referenceStations = new HashSet<>();

    public static ReferenceStation getReferenceStationById(int id) {
        for (ReferenceStation station : referenceStations) {
            if (station.getId() == id)
                return station;
        }
        return null;
    }

    public static ReferenceStation getReferenceStationByName(String name) throws IllegalArgumentException {
        for (ReferenceStation station : referenceStations) {
            if (station.getName().equals(name))
                return station;
        }
        throw new IllegalArgumentException();
    }

    private static ArrayList<Integer> readIdALlCasters() {
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
            logger.fatal("Can't read data from database!", e);
        }

        return listId;
    }

    private static ArrayList<Integer> readAllReferenceStation() {
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
            logger.fatal("Can't read data from database!", e);
        }
        return listId;
    }

}
