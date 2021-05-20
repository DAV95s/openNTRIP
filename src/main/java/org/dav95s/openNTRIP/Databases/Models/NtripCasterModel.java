package org.dav95s.openNTRIP.Databases.Models;

import org.dav95s.openNTRIP.Databases.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;

public class NtripCasterModel {
    final static private Logger logger = LoggerFactory.getLogger(NtripCasterModel.class.getName());

    public NtripCasterModel(int id) throws SQLException {
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

    public int create() throws SQLException {
        String sql = "INSERT INTO `casters`(`address`, `port`, `group_id`, `status`) VALUES (?,?,?,?)";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, address);
            statement.setInt(2, port);
            statement.setInt(3, group_id);
            statement.setBoolean(4, status);

            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            rs.next();

            id = rs.getInt(1);
            return id;
        } catch (SQLException e) {
            throw new SQLException("Can't create new caster", e);
        }
    }

    public boolean read() throws SQLException {
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
            throw new SQLException("Can't read from DB", e);
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
            e.printStackTrace();
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
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Integer> readMountpointsId() throws SQLException {
        String sql = "SELECT `id` FROM `mountpoints` WHERE `caster_id` = ?";

        ArrayList<Integer> response = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    response.add(rs.getInt("id"));
                }
            }
            return response;
        } catch (SQLException e) {
            throw new SQLException("Can't read from DB", e);
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
