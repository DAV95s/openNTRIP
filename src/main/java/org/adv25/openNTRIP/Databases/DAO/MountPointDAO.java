package org.adv25.openNTRIP.Databases.DAO;

import org.adv25.openNTRIP.Databases.DataSource;
import org.adv25.openNTRIP.Databases.Models.MountPointModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class MountPointDAO {

    public boolean create(MountPointModel model) {

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(SQL.CREATE.QUERY)) {

            statement.setString(1, model.getMountpoint());
            statement.setString(2, model.getIdentifier());
            statement.setString(3, model.getFormat());
            statement.setString(4, model.getFormatDetails());
            statement.setInt(5, model.getCarrier());
            statement.setString(6, model.getNavSystem());
            statement.setString(7, model.getNetwork());
            statement.setString(8, model.getCountry());
            statement.setDouble(9, model.getLla().getLat().doubleValue());
            statement.setDouble(10, model.getLla().getLon().doubleValue());
            statement.setBoolean(11, model.isNmea());
            statement.setBoolean(12, model.isSolution());
            statement.setString(13, model.getGenerator());
            statement.setString(14, model.getCompression());
            statement.setString(15, model.getAuthenticator().toString());
            statement.setBoolean(16, model.isFee());
            statement.setInt(17, model.getBitrate());
            statement.setString(18, model.getMisc());
            statement.setInt(19, model.getCasterId());
            statement.setString(20, model.getBasesIdsJoin());
            statement.setBoolean(21, model.isAvailable());
            statement.setInt(22, model.getPlugin_id());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public MountPointModel read(String s) {
        MountPointModel model = new MountPointModel();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(SQL.READ.QUERY)) {

            statement.setString(1, s);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    model.setId(rs.getLong("id"));
                    model.setMountpoint(s);
                    model.setIdentifier(rs.getString("identifier"));
                    model.setFormat(rs.getString("format"));
                    model.setFormatDetails(rs.getString("format-details"));
                    model.setCarrier(rs.getInt("carrier"));
                    model.setNavSystem(rs.getString("nav-system"));
                    model.setNetwork(rs.getString("network"));
                    model.setCountry(rs.getString("country"));
                    model.setNmea(rs.getBoolean("nmea"));
                    model.setSolution(rs.getBoolean("solution"));
                    model.setGenerator(rs.getString("generator"));
                    model.setCompression(rs.getString("compression"));
                    model.setAuthenticator(rs.getString("authentication"));
                    model.setFee(rs.getBoolean("fee"));
                    model.setBitrate(rs.getInt("bitrate"));
                    model.setMisc(rs.getString("misc"));
                    model.setCasterId(rs.getInt("caster_id"));
                    model.setStationsPool(rs.getString("bases_id"));
                    model.setAvailable(rs.getBoolean("available"));
                    model.setPlugin_id(rs.getInt("plugin_id"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return model;
    }

    public boolean update(MountPointModel model) {

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(SQL.UPDATE.QUERY)) {

            statement.setString(1, model.getIdentifier());
            statement.setString(2, model.getFormat());
            statement.setString(3, model.getFormatDetails());
            statement.setInt(4, model.getCarrier());
            statement.setString(5, model.getNavSystem());
            statement.setString(6, model.getNetwork());
            statement.setString(7, model.getCountry());
            statement.setDouble(8, model.getLla().getLat().doubleValue());
            statement.setDouble(9, model.getLla().getLon().doubleValue());
            statement.setBoolean(10, model.isNmea());
            statement.setBoolean(11, model.isSolution());
            statement.setString(12, model.getGenerator());
            statement.setString(13, model.getCompression());
            statement.setString(14, model.getAuthenticator().toString());
            statement.setBoolean(15, model.isFee());
            statement.setInt(16, model.getBitrate());
            statement.setString(17, model.getMisc());
            statement.setInt(18, model.getCasterId());
            statement.setString(19, model.getBasesIdsJoin());//model.getBasesIds()
            statement.setBoolean(20, model.isAvailable());
            statement.setInt(21, model.getPlugin_id());
            statement.setString(22, model.getMountpoint());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public HashMap<String, MountPointModel> getAllByCasterId(int caster_id) {
        HashMap<String, MountPointModel> models = new HashMap<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(SQL.GETBYID.QUERY)) {

            statement.setInt(1, caster_id);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    MountPointModel model = new MountPointModel();

                    model.setId(rs.getLong("id"));
                    model.setMountpoint(rs.getString("mountpoint"));
                    model.setIdentifier(rs.getString("identifier"));
                    model.setFormat(rs.getString("format"));
                    model.setFormatDetails(rs.getString("format-details"));
                    model.setCarrier(rs.getInt("carrier"));
                    model.setNavSystem(rs.getString("nav-system"));
                    model.setNetwork(rs.getString("network"));
                    model.setCountry(rs.getString("country"));
                    model.setLatitude(rs.getDouble("latitude"));
                    model.setLongitude(rs.getDouble("longitude"));
                    model.setNmea(rs.getBoolean("nmea"));
                    model.setSolution(rs.getBoolean("solution"));
                    model.setGenerator(rs.getString("generator"));
                    model.setCompression(rs.getString("compression"));
                    model.setAuthenticator(rs.getString("authentication"));
                    model.setFee(rs.getBoolean("fee"));
                    model.setBitrate(rs.getInt("bitrate"));
                    model.setMisc(rs.getString("misc"));
                    model.setCasterId(rs.getInt("caster_id"));
                    model.setStationsPool(rs.getString("stations_id"));
                    model.setAvailable(rs.getBoolean("available"));
                    model.setPlugin_id(rs.getInt("plugin_id"));

                    models.put(model.getMountpoint(), model);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return models;
    }


    enum SQL {
        CREATE("INSERT INTO ntrip.mountpoints\n" +
                "(mountpoint, identifier, format, `format-details`, carrier, `nav-system`, network, country, latitude, longitude, nmea, solution, generator, compression, authentication, fee, bitrate, misc, caster_id, bases_id, available, plugin_id)\n" +
                "VALUES('', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);\n"),

        READ("SELECT id, mountpoint, identifier, format, `format-details`, carrier, `nav-system`, network, country, latitude, longitude, nmea, solution, generator, compression, authentication, fee, bitrate, misc, caster_id, bases_id, available, plugin_id\n" +
                "FROM ntrip.mountpoints\n" +
                "WHERE `mountpoint` = ?;"),

        UPDATE("UPDATE ntrip.mountpoints\n" +
                "SET identifier=?, format=?, `format-details`=?, carrier=?, `nav-system`=?, network=?, country=?, latitude=?, longitude=?, nmea=?, solution=?, generator=?, compression=?, authentication=?, fee=?, bitrate=?, misc=?, caster_id=0, bases_id=?, available=?, plugin_id=?\n" +
                "WHERE mountpoint = ?;\n"),

        DELETE("DELETE FROM ntrip.mountpoints\n" +
                "WHERE mountpoint= ?;\n"),

        GETBYID("SELECT * FROM ntrip.mountpoints\n" +
                "WHERE caster_id = ?;");

        String QUERY;

        SQL(String query) {
            this.QUERY = query;
        }
    }
}
