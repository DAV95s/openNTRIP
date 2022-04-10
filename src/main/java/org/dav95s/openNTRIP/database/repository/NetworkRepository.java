package org.dav95s.openNTRIP.database.repository;

import org.dav95s.openNTRIP.database.DataSource;
import org.dav95s.openNTRIP.database.modelsV2.NetworkModel;
import org.dav95s.openNTRIP.protocols.ntrip.NetworkType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class NetworkRepository {
    final static private Logger logger = LoggerFactory.getLogger(ReferenceStationRepository.class.getName());

    public ArrayList<NetworkModel> getAllNetworks() {

        String sql = "SELECT `type`, `identifier` FROM `networks`";

        ArrayList<NetworkModel> networks = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    networks.add(new NetworkModel(rs.getString("identifier"), rs.getString("type")));
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return null;
        }
        return networks;
    }

    public ArrayList<NetworkModel> getAllReferenceStations() {
        String sql = "SELECT `name` FROM `reference_stations`";

        ArrayList<NetworkModel> networks = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    networks.add(new NetworkModel(rs.getString("name"), NetworkType.STR));
                }
            }
            return networks;
        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return null;
        }
    }
}
