package org.dav95s.openNTRIP.Databases.Models;

import org.dav95s.openNTRIP.Databases.DataSource;
import org.dav95s.openNTRIP.Tools.Config;

import java.sql.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class UserModel {
    private int id;
    private String username;
    private String password;
    private String email;
    private Boolean active;
    final private Set<UserGroupModel> listGroups = new HashSet<>();

    public void setPassword(String password) {
        this.password = password;
    }

    //todo password hash!!!!
    public int create() throws SQLException {
        String sql = "INSERT INTO `users`(`username`, `password`, `email`, `created_on`) VALUES (?,?,?,?)";

        if (email == null) {
            email = username + "@" + Config.getInstance().getDefaultEmailDomain();
        }

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, email);
            statement.setLong(4, Instant.now().getEpochSecond());

            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            rs.next();

            id = rs.getInt(1);
            return id;
        }
    }

    public boolean read() throws SQLException {
        String sql;
        String searchIndex;

        if (username != null) {
            sql = "SELECT * FROM users WHERE `username` = ?";
            searchIndex = username;
        } else if (email != null) {
            sql = "SELECT * FROM users WHERE `email` = ?";
            searchIndex = email;
        } else {
            throw new SQLException("Need username of email, for the select query!");
        }

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, searchIndex);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("id");
                    username = rs.getString("username");
                    password = rs.getString("password");
                    email = rs.getString("email");
                    active = rs.getBoolean("active");
                    return true;
                } else {
                    return false;
                }
            }

        }
    }

    public boolean update() {
        String sql = "UPDATE `users` SET `username`= ?,`password`= ?,`email`= ? WHERE `id` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, email);
            statement.setInt(4, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete() throws SQLException {
        String sql = "DELETE FROM `users` WHERE `id` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;
        }
    }

    public void readGroups() throws SQLException {
        String sql = "SELECT groups.name, groups.description, groups.id FROM users_groups LEFT JOIN groups ON users_groups.group_id = groups.id WHERE users_groups.user_id = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {

                if (!rs.next())
                    throw new SQLException("Can't read user groups. User does not exist.");

                do {
                    UserGroupModel group = new UserGroupModel();
                    group.setGroup_id(rs.getInt("id"));
                    group.setName(rs.getString("name"));
                    group.setDescription(rs.getString("description"));
                    listGroups.add(group);
                } while (rs.next());
            }
        }
    }

    public void deleteFromGroup(int group_id) throws SQLException {
        String sql = "DELETE FROM `users_groups` WHERE `users_groups.user_id` = ? AND `users_groups.group_id` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.setInt(2, group_id);
            statement.executeUpdate();
        }
    }

    public void deleteFromGroup(UserGroupModel model) throws SQLException {
        deleteFromGroup(model.getGroup_id());
    }

    public void addToGroup(int group_id) throws SQLException {
        String sql = "INSERT INTO `users_groups`(`user_id`, `group_id`) VALUES (?,?)";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.setInt(2, group_id);
            statement.executeUpdate();
        }
    }

    public int getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Set<UserGroupModel> getListGroups() {
        return this.listGroups;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
