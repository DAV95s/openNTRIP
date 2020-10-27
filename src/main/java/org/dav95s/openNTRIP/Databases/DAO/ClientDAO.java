package org.dav95s.openNTRIP.Databases.DAO;

import org.dav95s.openNTRIP.Databases.DataSource;
import org.dav95s.openNTRIP.Databases.Models.ClientModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientDAO {

    public boolean create(ClientModel model) {
        return false;
    }


    public ClientModel read(String s) {
        ClientModel model = new ClientModel();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(ClientDAO.SQL.GET.QUERY)) {

            statement.setString(1, s);

            try(ResultSet rs = statement.executeQuery()){
                if (rs.next()) {
                    model.setId(rs.getLong("id"));
                    model.setName(rs.getString("username"));
                    model.setPassword(rs.getString("password"));
                    model.setEmail(rs.getString("email"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return model;
    }


    public boolean update(ClientModel model) {
        return false;
    }


    public boolean delete(ClientModel model) {
        return false;
    }

    enum SQL {
        GET("SELECT `id`, `username`, `email`, `password` FROM `users` WHERE `username` = ?");

        String QUERY;
        SQL(String s){
            this.QUERY = s;
        }
    }
}
