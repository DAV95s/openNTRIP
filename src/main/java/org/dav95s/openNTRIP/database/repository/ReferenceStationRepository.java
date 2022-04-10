package org.dav95s.openNTRIP.database.repository;

import org.dav95s.openNTRIP.database.DataSource;
import org.dav95s.openNTRIP.database.modelsV2.ReferenceStationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReferenceStationRepository {
    final static private Logger logger = LoggerFactory.getLogger(ReferenceStationRepository.class.getName());

    public ReferenceStationModel GetReferenceStationByName(String name) {
        String sql = "SELECT `id`, `name`, `format`, `country`, `lat`, `lon`, `alt`, `password`, `hz`, " +
                "(SELECT GROUP_CONCAT(network_name) FROM network_stations_mapping WHERE station_name = NAME) AS networks " +
                "FROM `reference_stations` " +
                "WHERE `name` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, name);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new ReferenceStationModel(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("format"),
                            rs.getDouble("lat"),
                            rs.getDouble("lon"),
                            rs.getDouble("alt"),
                            rs.getString("password"),
                            rs.getInt("hz"),
                            rs.getString("networks").split(",")
                    );
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return null;
        }
    }

    public ArrayList<String> GetNetworksByReferenceStation(String name) {
        String sql = "SELECT `network_name` FROM `network_stations_mapping` WHERE station_name = ?";

        ArrayList<String> networks = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, name);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    networks.add(rs.getString("network_name"));
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return null;
        }
        return networks;
    }

    public boolean updateOnlineStatus(String name, boolean status) {
        String sql = "UPDATE `reference_stations` SET `is_online` = ? WHERE `name` = ?";


        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setBoolean(1, status);
            statement.setString(2, name);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return false;
        }
    }
}
