package org.dav95s.openNTRIP.Databases.Models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dav95s.openNTRIP.Clients.Authentication.Basic;
import org.dav95s.openNTRIP.Clients.Authentication.Digest;
import org.dav95s.openNTRIP.Clients.Authentication.IAuthenticator;
import org.dav95s.openNTRIP.Clients.Authentication.None;
import org.dav95s.openNTRIP.Clients.User;
import org.dav95s.openNTRIP.Databases.DataSource;
import org.dav95s.openNTRIP.ServerBootstrap;
import org.dav95s.openNTRIP.Servers.ReferenceStation;
import org.dav95s.openNTRIP.Tools.NMEA;
import org.json.simple.JSONObject;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.parser.Proj4Parser;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

public class MountPointModel {
    final static private Logger logger = LogManager.getLogger(MountPointModel.class.getName());

    private int id;
    private String name;
    private String identifier;
    private String format;
    private String format_details;
    private int carrier;
    private String nav_system;
    private String network;
    private String country;
    private double lat;
    private double lon;
    private boolean nmea;
    private boolean solution;
    private String generator;
    private String compression;
    private IAuthenticator authenticator;
    private boolean fee;
    private int bitRate;
    private String misc;
    private int caster_id;
    private boolean available;
    private int plugin_id;
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
        NMEA.GPSPosition clientPosition = user.getPosition();

        //concurrent fix
        ArrayList<ReferenceStation> refStationPool = stationsPool;

        if (refStationPool.size() == 0)
            throw new IllegalStateException(this.toString() + " does not have accessible Reference stations!");

        ReferenceStation response = refStationPool.get(0);
        float minDistance = response.distance(clientPosition);

        for (ReferenceStation station : refStationPool) {
            float distance = station.distance(clientPosition);
            if (distance < minDistance) {
                response = station;
                minDistance = distance;
            }
        }

        return response;
    }

    @Override
    public String toString() {
        return "STR" + ';' + getName() + ';' + getIdentifier() + ';' + getFormat() + ';' + getFormat_details() + ';' + getCarrier() + ';' + getNav_system() + ';' + getNetwork() + ';' + getCountry()
                + ';' + String.format("%.2f", getLat()) + ';' + String.format("%.2f", getLon()) + ';' + (isNmea() ? 1 : 0) + ';' + (isSolution() ? 1 : 0) + ';' + getGenerator() + ';' + getCompression()
                + ';' + getAuthenticator().toString() + ';' + (isFee() ? 'Y' : 'N') + ';' + getBitRate() + ';' + getMisc() + "\r\n";
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
            statement.setInt(17, bitRate);
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
                    bitRate = rs.getInt("bitrate");
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
            statement.setInt(17, bitRate);
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
                    pool.add(ServerBootstrap.getInstance().getReferenceStationById(rs.getInt("station_id")));
                }
                stationsPool = pool;
            }
        }
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getFormat() {
        return this.format;
    }

    public String getFormat_details() {
        return this.format_details;
    }

    public int getCarrier() {
        return this.carrier;
    }

    public String getNav_system() {
        return this.nav_system;
    }

    public String getNetwork() {
        return this.network;
    }

    public String getCountry() {
        return this.country;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLon() {
        return this.lon;
    }

    public boolean isNmea() {
        return this.nmea;
    }

    public boolean isSolution() {
        return this.solution;
    }

    public String getGenerator() {
        return this.generator;
    }

    public String getCompression() {
        return this.compression;
    }

    public IAuthenticator getAuthenticator() {
        return this.authenticator;
    }

    public boolean isFee() {
        return this.fee;
    }

    public int getBitRate() {
        return this.bitRate;
    }

    public String getMisc() {
        return this.misc;
    }

    public int getCaster_id() {
        return this.caster_id;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public int getPlugin_id() {
        return this.plugin_id;
    }

    public ArrayList<ReferenceStation> getStationsPool() {
        return this.stationsPool;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setFormat_details(String format_details) {
        this.format_details = format_details;
    }

    public void setCarrier(int carrier) {
        this.carrier = carrier;
    }

    public void setNav_system(String nav_system) {
        this.nav_system = nav_system;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setNmea(boolean nmea) {
        this.nmea = nmea;
    }

    public void setSolution(boolean solution) {
        this.solution = solution;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }

    public void setFee(boolean fee) {
        this.fee = fee;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }

    public void setCaster_id(int caster_id) {
        this.caster_id = caster_id;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setPlugin_id(int plugin_id) {
        this.plugin_id = plugin_id;
    }
}

class CRS {

}