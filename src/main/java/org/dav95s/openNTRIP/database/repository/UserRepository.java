package org.dav95s.openNTRIP.database.repository;

import org.dav95s.openNTRIP.database.DataSource;
import org.dav95s.openNTRIP.database.modelsV2.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    final static private Logger logger = LoggerFactory.getLogger(UserRepository.class.getName());

    public UserModel getUserByUsername(String username) {
        String sql = "SELECT `id`,`username`, `password`, `email` FROM users WHERE `username` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new UserModel(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            logger.error("SQL Error", e);
            return null;
        }
    }
}
