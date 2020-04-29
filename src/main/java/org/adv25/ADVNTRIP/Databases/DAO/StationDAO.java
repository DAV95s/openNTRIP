package org.adv25.ADVNTRIP.Databases.DAO;

import org.adv25.ADVNTRIP.Databases.Models.StationModel;

import java.sql.*;

public class StationDAO implements DAO<StationModel, String> {

    @Override
    public boolean create(StationModel model) {
        return false;
    }

    @Override
    public StationModel read(String s) {
        StationModel model = new StationModel();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(SQL.GET.QUERY)) {

            statement.setString(1, s);

            try(ResultSet rs = statement.executeQuery()){
                if (rs.next()) {
                    model.setMountpoint(s);
                    model.setId(rs.getLong("id"));
                    model.setAuthentication(rs.getString("authentication"));
                    model.setBitrate(rs.getInt("bitrate"));
                    model.setCarrier(rs.getInt("carrier"));
                    model.setCompression(rs.getString("compression"));
                    model.setCountry(rs.getString("country"));
                    model.setFee(rs.getString("fee"));
                    model.setFormat(rs.getString("format"));
                    model.setFormatDetails(rs.getString("format-details"));
                    model.setGenerator(rs.getString("generator"));
                    model.setIdentifier(rs.getString("identifier"));
                    model.setLatitude(rs.getDouble("latitude"));
                    model.setLongitude(rs.getDouble("longitude"));
                    model.setMisc(rs.getString("misc"));
                    model.setNavSystem(rs.getString("nav-system"));
                    model.setNetwork(rs.getString("network"));
                    model.setNmea(rs.getString("nmea"));
                    model.setSolution(rs.getString("solution"));
                    model.setIs_online(rs.getString("is_online"));
                    model.setPassword(rs.getString("password"));
                    model.setProperties(rs.getString("properties"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return model;
    }

    @Override
    public boolean update(StationModel model) {
        return false;
    }

    @Override
    public boolean delete(StationModel model) {
        return false;
    }

    enum SQL {
        GET("select stations.id, mountpoint, identifier, format, `format-details`, carrier, `nav-system`, network, country, latitude, longitude, nmea, solution, generator, compression, authentication, fee, bitrate, misc, password, is_online, properties from ntrip.stations left join ntrip.stations_info on stations.id = stations_info.id where mountpoint = ?"),
        DELETE("DELETE FROM `ntrip`.`stations` WHERE  `mountpoint`='?';"),
        ADD("INSERT INTO `ntrip`.`stations` (`mountpoint`, `identifier`, `format`, `format-details`, `carrier`, `nav-system`, `network`, `country`, `latitude`, `longitude`, `nmea`, `solution`, `generator`, `compression`, `authentication`, `fee`, bitrate`, `misc`) " +
                "VALUES ('?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?');"),
        UPDATE("SQL4");


        String QUERY;

        SQL(String query) {
            this.QUERY = query;
        }
    }
}
