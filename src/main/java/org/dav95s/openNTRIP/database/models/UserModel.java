package org.dav95s.openNTRIP.database.models;

import org.dav95s.openNTRIP.database.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class UserModel {
    final static private Logger logger = LoggerFactory.getLogger(UserModel.class.getName());
    private int id;
    private String username;
    private String password;
    private String email;
    private Boolean active;
    final private Set<UserGroupModel> listGroups = new HashSet<>();

    public UserModel(String username) {
        this.username = username;
    }

    public UserModel() {

    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean read() {
        String sql;
        String searchIndex;

        if (username != null) {
            sql = "SELECT * FROM users WHERE `username` = ?";
            searchIndex = username;
        } else if (email != null) {
            sql = "SELECT * FROM users WHERE `email` = ?";
            searchIndex = email;
        } else {
            logger.error("Need username of email, for the select query!");
            return false;
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

        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return false;
        }
    }

    public boolean readGroups() {
        String sql = "SELECT groups.name, groups.description, groups.id FROM users_groups LEFT JOIN groups ON users_groups.group_id = groups.id WHERE users_groups.user_id = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {

                if (!rs.next())
                    return false;

                do {
                    UserGroupModel group = new UserGroupModel();
                    group.setGroup_id(rs.getInt("id"));
                    group.setName(rs.getString("name"));
                    group.setDescription(rs.getString("description"));
                    listGroups.add(group);
                } while (rs.next());

                return true;
            }
        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return false;
        }
    }

    public boolean deleteFromGroup(int group_id) {
        String sql = "DELETE FROM `users_groups` WHERE `users_groups.user_id` = ? AND `users_groups.group_id` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.setInt(2, group_id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return false;
        }
    }

    public boolean addToGroup(int group_id) {
        String sql = "INSERT INTO `users_groups`(`user_id`, `group_id`) VALUES (?,?)";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.setInt(2, group_id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return false;
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
