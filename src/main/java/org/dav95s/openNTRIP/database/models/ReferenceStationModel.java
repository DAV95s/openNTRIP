package org.dav95s.openNTRIP.database.models;

import org.dav95s.openNTRIP.database.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReferenceStationModel {
    final static private Logger logger = LoggerFactory.getLogger(ReferenceStationModel.class.getName());
    public int id;
    public String name;
    public String identifier;
    public String format;
    public String format_details;
    public int carrier;
    public String nav_system;
    public String country;
    public double lat;
    public double lon;
    public double alt;
    public int bitrate;
    public String misc;
    public boolean online = false;
    public String password;
    public int hz;

    public ReferenceStationModel(String name) {
        this.name = name;
    }

    public boolean read() {
        String sql = "SELECT * FROM `reference_stations` WHERE `name` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, name);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    this.name = rs.getString("name");
                    this.identifier = rs.getString("identifier");
                    this.format = rs.getString("format");
                    this.format_details = rs.getString("format_details");
                    this.carrier = rs.getInt("carrier");
                    this.nav_system = rs.getString("nav_system");
                    this.country = rs.getString("country");
                    this.lat = rs.getDouble("lat");
                    this.lon = rs.getDouble("lon");
                    this.alt = rs.getDouble("alt");
                    this.bitrate = rs.getInt("bitrate");
                    this.misc = rs.getString("misc");
                    this.password = rs.getString("password");
                    this.hz = rs.getInt("hz");
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return false;
        }
    }

    public boolean updateOnlineStatus(boolean status) {
        String sql = "UPDATE `reference_stations` SET `is_online` = ? WHERE id = ?";

        this.online = status;

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setBoolean(1, status);
            statement.setInt(2, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return false;
        }
    }

    @Override
    public String toString() {
        return "ReferenceStationModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", online=" + online +
                ", position="  +
                '}';
    }
    }


