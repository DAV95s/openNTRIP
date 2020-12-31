package org.dav95s.openNTRIP.Databases.Models;

import org.dav95s.openNTRIP.Databases.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class UserGroupModel {

    private int group_id;
    private String name;
    private String description;

    public Set<UserGroupModel> readAllGroups() throws SQLException {
        String sql = "SELECT * FROM `groups`";
        Set<UserGroupModel> groups = new HashSet<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    UserGroupModel group = new UserGroupModel();
                    group.setGroup_id(rs.getInt("id"));
                    group.setName(rs.getString("name"));
                    group.setDescription(rs.getString("description"));
                    groups.add(group);
                }
                return groups;
            }
        }
    }

    public int getGroup_id() {
        return this.group_id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
