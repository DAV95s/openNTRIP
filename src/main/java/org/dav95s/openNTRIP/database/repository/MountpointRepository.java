package org.dav95s.openNTRIP.database.repository;

import org.dav95s.openNTRIP.database.DataSource;
import org.dav95s.openNTRIP.database.modelsV2.MountpointModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MountpointRepository {
    final static private Logger logger = LoggerFactory.getLogger(MountpointRepository.class.getName());

    public MountpointModel getMountpoint(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        String sql = "SELECT `id`, `name`, `format`, network, nmea, solution, compression, authenticator, fee FROM mountpoints WHERE `name` = ?;";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, name);

            try (ResultSet rs = statement.executeQuery()) {
                MountpointModel response;
                if (rs.next()) {
                    response = new MountpointModel(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("format"),
                            rs.getString("network"),
                            rs.getBoolean("nmea"),
                            rs.getBoolean("solution"),
                            rs.getString("compression"),
                            rs.getString("authenticator"),
                            rs.getBoolean("fee")
                    );
                    return response;
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return null;
        }
    }
}
