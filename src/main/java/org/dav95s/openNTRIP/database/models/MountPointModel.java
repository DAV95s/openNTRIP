package org.dav95s.openNTRIP.database.models;


import org.dav95s.openNTRIP.database.DataSource;
import org.dav95s.openNTRIP.database.models.assets.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MountPointModel {
    final static private Logger logger = LoggerFactory.getLogger(MountPointModel.class.getName());

    protected int id;
    protected String name;
    protected String identifier;
    protected String format;
    protected String format_details;
    protected int carrier;
    protected String nav_system;
    protected String network;
    protected String country;
    protected double lat;
    protected double lon;
    protected boolean nmea;
    protected boolean solution;
    protected String generator;
    protected String compression;
    protected Authenticator authenticator;
    protected boolean fee;
    protected int bitRate;
    protected String misc;
    protected int caster_id;
    protected boolean available;
    protected int plugin_id;
    protected ArrayList<String> referenceStationIds = new ArrayList<>();

    public MountPointModel(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "STR" + ';' + getName() + ';' + getIdentifier() + ';' + getFormat() + ';' + getFormat_details() + ';' + getCarrier() + ';' + getNav_system() + ';' + getNetwork() + ';' + getCountry()
                + ';' + String.format("%.2f", getLat()) + ';' + String.format("%.2f", getLon()) + ';' + (isNmea() ? 1 : 0) + ';' + (isSolution() ? 1 : 0) + ';' + getGenerator() + ';' + getCompression()
                + ';' + authenticator + ';' + (isFee() ? 'Y' : 'N') + ';' + getBitRate() + ';' + getMisc() + "\r\n";
    }

    public boolean read() {
        String sql = "SELECT *, " +
                "(SELECT `name` FROM `reference_stations` WHERE `id` = station_id) AS `stations` " +
                "FROM mountpoints " +
                "LEFT JOIN mountpoints_stations " +
                "ON mountpoints.id=mountpoints_stations.mountpoint_id " +
                "WHERE `name` = ?;";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, name);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("id");
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
                    referenceStationIds.add(rs.getString("stations"));
                } else {
                    return false;
                }

                while (rs.next()) {
                    referenceStationIds.add(rs.getString("stations"));
                }
                return true;
            }

        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return false;
        }
    }

    public ArrayList<String> getReferenceStationIds() {
        return referenceStationIds;
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

    public Authenticator getAuthenticator() {
        return this.authenticator;
    }

    private void setAuthenticator(String authenticator) {
        switch (authenticator) {
            case "B":
            case "Basic":
                this.authenticator = Authenticator.Basic;
                break;
            case "D":
            case "Digest":
                this.authenticator = Authenticator.Digest;
                break;
            default:
                this.authenticator = Authenticator.None;
                break;
        }
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
}


