package org.dav95s.openNTRIP.Databases.Models;

import org.dav95s.openNTRIP.CRSUtils.GridShift.GeodeticPoint;
import org.dav95s.openNTRIP.Databases.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GridModel {

    public ArrayList<GeodeticPoint> getAddGeodeticPointByCrsId(int crsId) throws SQLException {
        String sql = "SELECT id, ROUND(X(geodetic_point_measured), 9) AS north, ROUND(Y(geodetic_point_measured), 9) AS east, ROUND(X(geodetic_point_from_catalog)-X(geodetic_point_measured), 9) AS dnorth, ROUND(Y(geodetic_point_from_catalog)-Y(geodetic_point_measured), 9) AS deast FROM `crs_grids` WHERE `crs_id` = ?;";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setInt(1, crsId);

            try (ResultSet rs = statement.executeQuery()) {
                ArrayList<GeodeticPoint> gridNodes = new ArrayList<>();

                while (rs.next()) {
                    GeodeticPoint point = new GeodeticPoint();
                    point.id = rs.getLong("id");
                    point.north = rs.getDouble("north");
                    point.east = rs.getDouble("east");
                    point.dNorth = rs.getDouble("dnorth");
                    point.dEast = rs.getDouble("deast");
                    gridNodes.add(point);
                }
                return gridNodes;
            }

        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }
}
