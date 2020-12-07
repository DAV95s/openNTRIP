package org.dav95s.openNTRIP.Databases.Models;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dav95s.openNTRIP.Clients.Authentication.IAuthenticator;
import org.dav95s.openNTRIP.Clients.Authentication.Basic;
import org.dav95s.openNTRIP.Clients.Authentication.Digest;
import org.dav95s.openNTRIP.Clients.Authentication.None;
import org.dav95s.openNTRIP.Clients.User;
import org.dav95s.openNTRIP.Databases.DataSource;
import org.dav95s.openNTRIP.ServerBootstrap;
import org.dav95s.openNTRIP.Servers.MountPoint;
import org.dav95s.openNTRIP.Servers.ReferenceStation;
import org.dav95s.openNTRIP.Tools.NMEA;
import org.json.simple.JSONObject;

import java.sql.*;
import java.util.*;

public class MountPointModel {
    final static private Logger logger = LogManager.getLogger(MountPointModel.class.getName());

    @Getter @Setter
    private int id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String identifier;
    @Getter @Setter
    private String format;
    @Getter @Setter
    private String format_details;
    @Getter @Setter
    private int carrier;
    @Getter @Setter
    private String nav_system;
    @Getter @Setter
    private String network;
    @Getter @Setter
    private String country;
    @Getter @Setter
    private double lat;
    @Getter @Setter
    private double lon;
    @Getter @Setter
    private boolean nmea;
    @Getter @Setter
    private boolean solution;
    @Getter @Setter
    private String generator;
    @Getter @Setter
    private String compression;
    @Getter
    private IAuthenticator authenticator;
    @Getter @Setter
    private boolean fee;
    @Getter @Setter
    private int bitrate;
    @Getter @Setter
    private String misc;
    @Getter @Setter
    private int caster_id;
    @Getter @Setter
    private boolean available;
    @Getter @Setter
    private int plugin_id;

    @Getter
    private ArrayList<ReferenceStation> stationsPool;


    public MountPointModel() {

    }

    public MountPointModel(int id) throws SQLException {
        this.id = id;
        this.read();
        this.readAccessibleReferenceStations();
    }

    public void setAuthenticator(String authenticator) {
        switch (authenticator) {
            case "Basic":
                this.authenticator = new Basic();
                break;
            case "Digest":
                this.authenticator = new Digest();
                break;
            default:
                this.authenticator = new None();
                break;
        }
    }

    public ReferenceStation getReferenceStation(User user) {
        if (nmea) {
            if (user.getPosition() == null)
                return null;

            return getNearestStation(user);
        }

        return stationsPool.get(0);
    }

    private ReferenceStation getNearestStation(User user) {
        TreeMap<Float, ReferenceStation> sortedRange = new TreeMap<>();
        NMEA.GPSPosition clientPosition = user.getPosition();

        for (ReferenceStation station : stationsPool) {
            if (station.getModel().isOnline() || station.getModel().getPosition().isSet()) {
                sortedRange.put(station.distance(clientPosition), station);
            }
        }

        if (logger.isDebugEnabled()) {
            JSONObject object = new JSONObject();
            object.put("from", "getNearestStation");
            object.put("user", user.toString());
            object.put("userPosition", user.getPosition().toString());
            object.put("mountpoint", name);
            object.put("pull", Arrays.toString(stationsPool.toArray()));
            object.put("ranges", Arrays.toString(sortedRange.keySet().toArray()));
            logger.debug(object);
        }

        return sortedRange.firstEntry().getValue();
    }

    @Override
    public String toString() {
        return "STR" + ';' + getName() + ';' + getIdentifier() + ';' + getFormat() + ';' + getFormat_details() + ';' + getCarrier() + ';' + getNav_system() + ';' + getNetwork() + ';' + getCountry()
                + ';' + String.format("%.2f", getLat()) + ';' + String.format("%.2f", getLon()) + ';' + (isNmea() ? 1 : 0) + ';' + (isSolution() ? 1 : 0) + ';' + getGenerator() + ';' + getCompression()
                + ';' + getAuthenticator().toString() + ';' + (isFee() ? 'Y' : 'N') + ';' + getBitrate() + ';' + getMisc() + "\r\n";
    }

    public int create() throws SQLException {
        String sql = "INSERT INTO `mountpoints`(`name`, `identifier`, `format`, `format_details`, `carrier`, `nav_system`, `network`, `country`, `latitude`, `longitude`, `nmea`, `solution`, `generator`, `compression`, `authenticator`, `fee`, `bitrate`, `misc`, `caster_id`, `available`, `plugin_id`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, name);
            statement.setString(2, identifier);
            statement.setString(3, format);
            statement.setString(4, format_details);
            statement.setInt(5, carrier);
            statement.setString(6, nav_system);
            statement.setString(7, network);
            statement.setString(8, country);
            statement.setDouble(9, lat);
            statement.setDouble(10, lon);
            statement.setBoolean(11, nmea);
            statement.setBoolean(12, solution);
            statement.setString(13, generator);
            statement.setString(14, compression);
            statement.setString(15, authenticator.toString());
            statement.setBoolean(16, fee);
            statement.setInt(17, bitrate);
            statement.setString(18, misc);
            statement.setInt(19, caster_id);
            statement.setBoolean(20, available);
            statement.setInt(21, plugin_id);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();

            if (rs.next()) {
                id = rs.getInt(1);
                return id;
            } else {
                throw new SQLException("The database did not return the id.");
            }

        } catch (SQLException e) {
            throw new SQLException("Can't create new mountpoint", e);
        }
    }

    public boolean read() throws SQLException {
        String sql = "SELECT * FROM `mountpoints` WHERE `id` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("name");
                    identifier = rs.getString("identifier");
                    format = rs.getString("format");
                    format_details = rs.getString("format_details");
                    carrier = rs.getInt("carrier");
                    nav_system = rs.getString("nav_system");
                    network = rs.getString("network");
                    country = rs.getString("country");
                    lat = rs.getDouble("latitude");
                    lon = rs.getDouble("longitude");
                    nmea = rs.getBoolean("nmea");
                    solution = rs.getBoolean("solution");
                    generator = rs.getString("generator");
                    compression = rs.getString("compression");
                    setAuthenticator(rs.getString("authenticator"));
                    fee = rs.getBoolean("fee");
                    bitrate = rs.getInt("bitrate");
                    misc = rs.getString("misc");
                    caster_id = rs.getInt("caster_id");
                    available = rs.getBoolean("available");
                    plugin_id = rs.getInt("plugin_id");
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public boolean update() throws SQLException {
        String sql = "UPDATE `mountpoints` SET `name`=?,`identifier`=?,`format`=?,`format_details`=?,`carrier`=?,`nav_system`=?,`network`=?,`country`=?,`latitude`=?,`longitude`=?,`nmea`=?,`solution`=?,`generator`=?,`compression`=?,`authenticator`=?,`fee`=?,`bitrate`=?,`misc`=?,`caster_id`=?,`available`=?,`plugin_id`=? WHERE `id` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, identifier);
            statement.setString(3, format);
            statement.setString(4, format_details);
            statement.setInt(5, carrier);
            statement.setString(6, nav_system);
            statement.setString(7, network);
            statement.setString(8, country);
            statement.setDouble(9, lat);
            statement.setDouble(10, lon);
            statement.setBoolean(11, nmea);
            statement.setBoolean(12, solution);
            statement.setString(13, generator);
            statement.setString(14, compression);
            statement.setString(15, getAuthenticator().toString());
            statement.setBoolean(16, fee);
            statement.setInt(17, bitrate);
            statement.setString(18, misc);
            statement.setInt(19, caster_id);
            statement.setBoolean(20, available);
            statement.setInt(21, plugin_id);

            statement.setInt(22, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public boolean delete() throws SQLException {
        String sql = "DELETE FROM `mountpoints` WHERE `id` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setInt(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public void readAccessibleReferenceStations() throws SQLException {
        String sql = "SELECT `station_id` FROM `mountpoints_stations` WHERE `mountpoint_id` = ?";

        ArrayList<ReferenceStation> pool = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    pool.add(ServerBootstrap.getReferenceStationById(rs.getInt("station_id")));
                }
                stationsPool = pool;
            }
        }
    }
}
