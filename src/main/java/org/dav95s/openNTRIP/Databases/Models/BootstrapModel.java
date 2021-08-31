package org.dav95s.openNTRIP.Databases.Models;

import org.dav95s.openNTRIP.Databases.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BootstrapModel {
    private final static Logger logger = LoggerFactory.getLogger(BootstrapModel.class.getName());

    public ArrayList<Integer> getAllIdsOfReferenceStations() {
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

    public ArrayList<Integer> getAllIdOfCasters() {
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
