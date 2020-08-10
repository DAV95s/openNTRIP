package org.adv25.ADVNTRIP.Databases.DAO;

import org.adv25.ADVNTRIP.Databases.DataSource;
import org.adv25.ADVNTRIP.Databases.Models.ReferenceStationModel;
import org.adv25.ADVNTRIP.Spatial.Point_lla;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReferenceStationDAO implements DAO<ReferenceStationModel, String> {
    @Override
    public boolean create(ReferenceStationModel model) {
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(SQL.CREATE.QUERY)) {

            statement.setString(1, model.getMountpoint());
            statement.setString(2, model.getIdentifier());
            statement.setString(3, model.getFormat());
            statement.setString(4, model.getFormat_details());
            statement.setInt(5, model.getCarrier());
            statement.setString(6, model.getNav_system());
            statement.setString(7, model.getCountry());
            statement.setString(8, model.getLla().getWKT());
            statement.setBigDecimal(9, model.getLla().getAlt());
            statement.setInt(10, model.getBitrate());
            statement.setString(11, model.getMisc());
            statement.setInt(12, model.isIs_online() ? 1 : 0);
            statement.setString(13, model.getPassword());
            statement.setInt(14, model.getHz());

            if (statement.execute())
                return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ReferenceStationModel read(String s) {
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(SQL.READ.QUERY)) {

            ReferenceStationModel model = new ReferenceStationModel();
            statement.setString(1, s);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    model.setId(rs.getInt("id"));
                    model.setMountpoint(rs.getString("mountpoint"));
                    model.setIdentifier(rs.getString("identifier"));
                    model.setFormat(rs.getString("format"));
                    model.setFormat_details(rs.getString("format-details"));
                    model.setCarrier(rs.getInt("carrier"));
                    model.setNav_system(rs.getString("nav-system"));
                    model.setCountry(rs.getString("country"));
                    model.setLla(new Point_lla(rs.getString("lla")));
                    model.getLla().setAlt(rs.getBigDecimal("altitude"));
                    model.setBitrate(rs.getInt("bitrate"));
                    model.setMisc(rs.getString("misc"));
                    model.setIs_online(rs.getBoolean("is_online"));
                    model.setPassword(rs.getString("password"));
                    model.setHz(rs.getInt("hz"));
                }
            }
            return model;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<ReferenceStationModel> readAll() {
        ArrayList<ReferenceStationModel> response = new ArrayList<>();
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(SQL.READALL.QUERY)) {

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ReferenceStationModel model = new ReferenceStationModel();
                    model.setId(rs.getInt("id"));
                    model.setMountpoint(rs.getString("mountpoint"));
                    model.setIdentifier(rs.getString("identifier"));
                    model.setFormat(rs.getString("format"));
                    model.setFormat_details(rs.getString("format-details"));
                    model.setCarrier(rs.getInt("carrier"));
                    model.setNav_system(rs.getString("nav-system"));
                    model.setCountry(rs.getString("country"));
                    model.setLla(new Point_lla(rs.getString("lla")));
                    model.getLla().setAlt(rs.getBigDecimal("altitude"));
                    model.setBitrate(rs.getInt("bitrate"));
                    model.setMisc(rs.getString("misc"));
                    model.setIs_online(rs.getBoolean("is_online"));
                    model.setPassword(rs.getString("password"));
                    model.setHz(rs.getInt("hz"));
                    response.add(model);
                }
            }
            return response;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(ReferenceStationModel model) {

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(SQL.UPDATE.QUERY)) {

            statement.setString(1, model.getIdentifier());
            statement.setString(2, model.getFormat());
            statement.setString(3, model.getFormat_details());
            statement.setInt(4, model.getCarrier());
            statement.setString(5, model.getNav_system());
            statement.setString(6, model.getCountry());
            statement.setString(7, model.getLla().getWKT());
            statement.setBigDecimal(8, model.getLla().getAlt());
            statement.setInt(9, model.getBitrate());
            statement.setString(10, model.getMisc());
            statement.setInt(11, model.getHz());

            statement.setInt(12, model.getId());

            if (statement.execute())
                return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(ReferenceStationModel model) {
        return false;
    }

    public void setOnline(ReferenceStationModel model) {
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(SQL.SET_ONLINE.QUERY)) {

            statement.setInt(1, model.getId());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setOffline(ReferenceStationModel model) {
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(SQL.SET_OFFLINE.QUERY)) {

            statement.setInt(1, model.getId());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Integer getNearestId(double lat, double lon, String filter) {
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement((SQL.GET_NEAREST.QUERY))) {
            statement.setString(1, "POINT(" + lat + " " + lon + ")");
            statement.setString(2, filter);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    enum SQL {
        CREATE("INSERT INTO base_stations VALUES( DEFAULT , ?, ?, ?, ?, ?, ?, ?, GeomFromText(?),? , ?, ?, ?, ?, GeomFromText(?),? ,? );"),
        READ("SELECT id, mountpoint, identifier, format, `format-details`, carrier, `nav-system`, country, ST_AsText(lla) as lla, altitude, bitrate, misc, is_online, password, hz\n" +
                "FROM ntrip.base_stations" +
                " WHERE `mountpoint` = ?;"),
        READALL("SELECT id, mountpoint, identifier, format, `format-details`, carrier, `nav-system`, country, ST_AsText(lla) as lla, altitude, bitrate, misc, is_online, password, hz " +
                "FROM ntrip.base_stations"),
        UPDATE("UPDATE ntrip.base_stations " +
                "SET `identifier`=?, `format`=?, `format-details`=?, `carrier`=?, `nav-system`=?, `country`=?, `lla`=ST_GeomFromText(?), `altitude`=?, `bitrate`=?, `misc`=?, `hz`=? WHERE `id` = ?"),
        DELETE(""),

        SET_OFFLINE("UPDATE ntrip.base_stations " +
                "SET `is_online` = 0 WHERE `id` = ?;"),
        SET_ONLINE("UPDATE ntrip.base_stations " +
                "SET `is_online` = 1 WHERE `id` = ?;"),

        GET_NEAREST("SELECT id, lla, st_distance(lla, ST_GeomFromText(?)) AS dist " +
                "FRO< ntrip.base_stations " +
                "WHERE lla IS NOT NULL AND id IN (?)" +
                "GROUP BY dist" +
                "LIMIT 1");

        String QUERY;

        SQL(String query) {
            QUERY = query;
        }
    }
}
