package org.dav95s.openNTRIP.Databases.Models;

import lombok.Getter;
import lombok.Setter;
import org.dav95s.openNTRIP.Databases.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class UserGroupModel {

    @Getter @Setter
    private int group_id;
    @Getter @Setter
    private String name;
    @Getter @Setter
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
}
