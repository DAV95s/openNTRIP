package org.dav95s.openNTRIP.Databases.Models;

import org.dav95s.openNTRIP.Databases.DataSource;
import org.dav95s.openNTRIP.Tools.NMEA;
import org.dav95s.openNTRIP.Tools.RTCM.MSG1006;
import org.dav95s.openNTRIP.Tools.RTCMStream.Message;
import org.dav95s.openNTRIP.Tools.RTCMStream.MessagePack;

import java.sql.*;

public class ReferenceStationModel {
    private int id;
    private String name;
    private String identifier;
    private String format;
    private String format_details;
    private int carrier;
    private String nav_system;
    private String country;
    private int bitrate;
    private String misc;
    private boolean online;
    private String password;
    private int hz;
    private NMEA.GPSPosition position = new NMEA().getPosition();
    private ReplaceCoordinates replaceCoordinates;

    public ReferenceStationModel() {
    }

    public ReferenceStationModel(int id) throws SQLException {
        this.id = id;
        this.read();
    }

    public int create() throws SQLException {
        String sql = "INSERT INTO `reference_stations`(`name`, `identifier`, `format`, `format_details`, `carrier`, `nav_system`, `country`, `lat`, `lon`, `alt`, `bitrate`, `misc`, `is_online`, `password`, `hz`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, name);
            statement.setString(2, identifier);
            statement.setString(3, format);
            statement.setString(4, format_details);
            statement.setInt(5, carrier);
            statement.setString(6, nav_system);
            statement.setString(7, country);
            statement.setDouble(8, position.lat);
            statement.setDouble(9, position.lon);
            statement.setDouble(10, position.altitude);
            statement.setInt(11, bitrate);
            statement.setString(12, misc);
            statement.setBoolean(13, online);
            statement.setString(14, password);
            statement.setInt(15, hz);

            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            rs.next();
            id = rs.getInt(1);

            return id;
        } catch (SQLException e) {
            throw new SQLException("Can't create new reference station", e);
        }
    }

    public boolean read() throws SQLException {
        String sql = "SELECT * FROM `reference_stations` WHERE `id` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    this.name = rs.getString("name");
                    this.identifier = rs.getString("identifier");
                    this.format = rs.getString("format");
                    this.format_details = rs.getString("format_details");
                    this.carrier = rs.getInt("carrier");
                    this.nav_system = rs.getString("nav_system");
                    this.country = rs.getString("country");
                    this.position.lat = (float) rs.getDouble("lat");
                    this.position.lon = (float) rs.getDouble("lon");
                    this.position.altitude = (float) rs.getDouble("alt");
                    this.bitrate = rs.getInt("bitrate");
                    this.misc = rs.getString("misc");
                    this.online = rs.getBoolean("is_online");
                    this.password = rs.getString("password");
                    this.hz = rs.getInt("hz");
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Can't read from DB", e);
        }
    }

    public boolean update() throws SQLException {
        String sql = "UPDATE `reference_stations` SET `name`=?,`identifier`=?,`format`=?,`format_details`=?,`carrier`=?,`nav_system`=?,`country`=?,`lat`=?,`lon`=?,`alt`=?,`bitrate`=?,`misc`=?,`is_online`=?,`password`=?,`hz`=? WHERE `id` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, identifier);
            statement.setString(3, format);
            statement.setString(4, format_details);
            statement.setInt(5, carrier);
            statement.setString(6, nav_system);
            statement.setString(7, country);
            statement.setDouble(8, position.lat);
            statement.setDouble(9, position.lon);
            statement.setDouble(10, position.altitude);
            statement.setInt(11, bitrate);
            statement.setString(12, misc);
            statement.setBoolean(13, online);
            statement.setString(14, password);
            statement.setInt(15, hz);

            statement.setInt(16, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new SQLException("Can't create new reference station", e);
        }
    }

    public boolean delete() {
        String sql = "DELETE FROM `reference_stations` WHERE `id` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean getOnlineStatus(boolean status) {
        String sql = "UPDATE `reference_stations` SET `is_online` = ? WHERE id = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setBoolean(1, status);
            statement.setInt(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void replaceCoordinates(MessagePack messagePack) {
        if (replaceCoordinates != null) {
            replaceCoordinates.handle(messagePack);
        }
    }

    @Override
    public String toString() {
        return "ReferenceStationModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", online=" + online +
                ", position=" + position +
                '}';
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

    public String getCountry() {
        return this.country;
    }

    public int getBitrate() {
        return this.bitrate;
    }

    public String getMisc() {
        return this.misc;
    }

    public boolean getOnlineStatus() {
        return this.online;
    }

    public String getPassword() {
        return this.password;
    }

    public int getHz() {
        return this.hz;
    }

    public NMEA.GPSPosition getPosition() {
        return this.position;
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

    public void setCountry(String country) {
        this.country = country;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHz(int hz) {
        this.hz = hz;
    }

    public void setPosition(NMEA.GPSPosition position) {
        this.position = position;
    }

    public boolean isFixPosition() {
        return this.replaceCoordinates != null;
    }

    public boolean readReplaceCoordinates() throws SQLException {
        String sql = "SELECT * FROM `reference_stations_fix_position` WHERE `station_id` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    if (replaceCoordinates == null) {
                        replaceCoordinates = new ReplaceCoordinates();
                    }
                    this.replaceCoordinates.setECEFX(rs.getDouble("ECEF_X"));
                    this.replaceCoordinates.setECEFY(rs.getDouble("ECEF_Y"));
                    this.replaceCoordinates.setECEFZ(rs.getDouble("ECEF_Z"));
                    this.replaceCoordinates.setAntennaHeight(rs.getDouble("antenna_height"));
                    return true;
                } else {
                    this.replaceCoordinates = null;
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Can't read from DB", e);
        }
    }

    class ReplaceCoordinates {
        double ECEFX;
        double ECEFY;
        double ECEFZ;
        double antennaHeight;
        int stationID;

        public MessagePack handle(MessagePack pack) {

            Message msg = pack.getMessageByNmb(1005);

            if (msg != null) {
                MSG1006 msg_new = new MSG1006(msg.getBytes());
                msg_new.setECEFX(ECEFX);
                msg_new.setECEFY(ECEFY);
                msg_new.setECEFZ(ECEFZ);
                pack.removeMessage(msg);
                pack.addMessage(1005, msg_new.getBytes());
            }

            msg = pack.getMessageByNmb(1006);

            if (msg != null) {
                MSG1006 msg_new = new MSG1006(msg.getBytes());
                msg_new.setECEFX(ECEFX);
                msg_new.setECEFY(ECEFY);
                msg_new.setECEFZ(ECEFZ);
                msg_new.setAntennaHeight(antennaHeight);
                pack.removeMessage(msg);
                pack.addMessage(1006, msg_new.getBytes());
            }

            return pack;
        }

        public void setECEFX(double ECEFX) {
            this.ECEFX = ECEFX;
        }

        public void setECEFY(double ECEFY) {
            this.ECEFY = ECEFY;
        }

        public void setECEFZ(double ECEFZ) {
            this.ECEFZ = ECEFZ;
        }

        public void setAntennaHeight(double antennaHeight) {
            this.antennaHeight = antennaHeight;
        }

        public void setStationID(int stationID) {
            this.stationID = stationID;
        }
    }
}


