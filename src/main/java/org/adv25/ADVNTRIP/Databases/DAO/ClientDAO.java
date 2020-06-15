package org.adv25.ADVNTRIP.Databases.DAO;

import org.adv25.ADVNTRIP.Databases.DataSource;
import org.adv25.ADVNTRIP.Databases.Models.ClientModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientDAO implements DAO<ClientModel, String> {
    @Override
    public boolean create(ClientModel model) {
        return false;
    }

    @Override
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

    @Override
    public boolean update(ClientModel model) {
        return false;
    }

    @Override
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
