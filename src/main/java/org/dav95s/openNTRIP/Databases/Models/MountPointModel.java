package org.dav95s.openNTRIP.Databases.Models;

import org.dav95s.openNTRIP.Clients.Authentication.Basic;
import org.dav95s.openNTRIP.Clients.Authentication.Digest;
import org.dav95s.openNTRIP.Clients.Authentication.IAuthenticator;
import org.dav95s.openNTRIP.Clients.Authentication.None;
import org.dav95s.openNTRIP.Databases.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MountPointModel {
    final static private Logger logger = LoggerFactory.getLogger(MountPointModel.class.getName());
    final static private Timer maintainer = new Timer("Maintainer of Mount Point Model");

    protected final int id;
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
    protected IAuthenticator authenticator = new None();
    protected boolean fee;
    protected int bitRate;
    protected String misc;
    protected int caster_id;
    protected boolean available;
    protected int plugin_id;
    protected volatile ArrayList<Integer> referenceStationIds;

    public MountPointModel(int id) {
        this.id = id;
    }

    private final TimerTask work = new TimerTask() {
        @Override
        public void run() {
            if (!read()) {
                logger.debug(name + " can't updates self condition.");
            }
            if (!readAccessibleIdOfReferenceStations()) {
                logger.debug(name + " can't updates self condition.");
            }
        }
    };

    public void start() {
        MountPointModel.maintainer.scheduleAtFixedRate(work, 0, 10_000);
    }

    public void stop() {
        work.cancel();
    }

    @Override
    public String toString() {
        return "STR" + ';' + getName() + ';' + getIdentifier() + ';' + getFormat() + ';' + getFormat_details() + ';' + getCarrier() + ';' + getNav_system() + ';' + getNetwork() + ';' + getCountry()
                + ';' + String.format("%.2f", getLat()) + ';' + String.format("%.2f", getLon()) + ';' + (isNmea() ? 1 : 0) + ';' + (isSolution() ? 1 : 0) + ';' + getGenerator() + ';' + getCompression()
                + ';' + getAuthenticator().toString() + ';' + (isFee() ? 'Y' : 'N') + ';' + getBitRate() + ';' + getMisc() + "\r\n";
    }

    private boolean read() {
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
            logger.error("SQL Error", e);
            return false;
        }
    }

    private boolean readAccessibleIdOfReferenceStations() {
        String sql = "SELECT `station_id` FROM `mountpoints_stations` WHERE `mountpoint_id` = ?";

        ArrayList<Integer> response = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    response.add(rs.getInt("station_id"));
                }
            }
            this.referenceStationIds = response;
            return true;
        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return false;
        }
    }

    public ArrayList<Integer> getReferenceStationIds() {
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

    public IAuthenticator getAuthenticator() {
        return this.authenticator;
    }

    private void setAuthenticator(String authenticator) {
        switch (authenticator) {
            case "B":
            case "Basic":
                this.authenticator = new Basic();
                break;
            case "D":
            case "Digest":
                this.authenticator = new Digest();
                break;
            default:
                this.authenticator = new None();
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
