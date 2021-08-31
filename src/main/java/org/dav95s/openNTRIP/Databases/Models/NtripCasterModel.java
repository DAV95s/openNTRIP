package org.dav95s.openNTRIP.Databases.Models;

import org.dav95s.openNTRIP.Databases.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.OptionalInt;

public class NtripCasterModel {
    final static private Logger logger = LoggerFactory.getLogger(NtripCasterModel.class.getName());

    public NtripCasterModel(int id) {
        this.id = id;
        this.read();
    }

    public NtripCasterModel() {

    }

    private int id;
    private String address;
    private int port;
    private int group_id;
    private boolean status;

    public OptionalInt create() {
        String sql = "INSERT INTO `casters`(`address`, `port`, `group_id`, `status`) VALUES (?,?,?,?)";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, address);
            statement.setInt(2, port);
            statement.setInt(3, group_id);
            statement.setBoolean(4, status);
            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
                return OptionalInt.of(id);
            } else {
                return OptionalInt.empty();
            }

        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return OptionalInt.empty();
        }
    }

    public boolean read() {
        String sql = "SELECT * FROM casters WHERE `id` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    this.address = rs.getString("address");
                    this.port = rs.getInt("port");
                    this.group_id = rs.getInt("group_id");
                    this.status = rs.getBoolean("status");
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

    public boolean update() {
        String sql = "UPDATE `casters` SET `address`= ?,`port`= ?,`group_id`= ?,`status`= ? WHERE `id` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, address);
            statement.setInt(2, port);
            statement.setInt(3, group_id);
            statement.setBoolean(4, status);
            statement.setInt(5, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return false;
        }
    }

    public boolean delete() {
        String sql = "DELETE FROM `casters` WHERE `id` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return false;
        }
    }

    public HashMap<Integer, String> getAccessibleMountpointIds() {
        String sql = "SELECT `id`,`name` FROM `mountpoints` WHERE `caster_id` = ?";

        HashMap<Integer, String> response = new HashMap<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    response.put(rs.getInt("id"), rs.getString("name"));
                }
            }
            return response;
        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return new HashMap<Integer, String>();
        }
    }

    public int getId() {
        return this.id;
    }

    public String getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    public int getGroup_id() {
        return this.group_id;
    }

    public boolean isStatus() {
        return this.status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
