package org.dav95s.openNTRIP.database.models;

import org.dav95s.openNTRIP.database.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CrsModel {
    static final private Logger logger = LoggerFactory.getLogger(CrsModel.class.getName());

    int id;
    int mountpointId;
    String crs;
    String geoidPath;


    String residualGrid;

    public CrsModel(int mountpointId) {
        this.mountpointId = mountpointId;
    }

    public boolean read() {
        String sql = "SELECT * FROM `crs` WHERE `id` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, mountpointId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("id");
                    crs = rs.getString("crs");
                    geoidPath = rs.getString("geoid_path");
                    residualGrid = rs.getString("residual_grid");
                    return true;
                } else {
                    return false;
                }
            }

        } catch (SQLException e) {
            logger.error("SQL error", e);
            return false;
        }
    }

    public boolean update() {
        String sql = "UPDATE `crs` SET `residual_grid`= ? WHERE `id` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, residualGrid);
            statement.setInt(2, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return false;
        }
    }

    public int getId() {
        return id;
    }

    public int getMountpointId() {
        return mountpointId;
    }

    public String getCrs() {
        return crs;
    }

    public String getGeoidPath() {
        return geoidPath;
    }

    public String getResidualGrid() {
        return residualGrid;
    }

    public void setResidualGrid(String residualGrid) {
        this.residualGrid = residualGrid;
    }

}
