package org.adv25.ADVNTRIP.Databases.DAO;

import org.adv25.ADVNTRIP.Databases.Models.ClientModel;
import org.adv25.ADVNTRIP.Databases.Models.StationModel;

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

//        try (Connection con = DataSource.getConnection();
//             PreparedStatement statement = con.prepareStatement(ClientDAO.SQL.GET.QUERY)) {
//
//            statement.setString(1, s);
//
//            try(ResultSet rs = statement.executeQuery()){
//                if (rs.next()) {
//                    model.setId(rs.getLong("id"));
//                    model.setName(rs.getString("Name"));
//                    model.setPassword(rs.getString("password"));
//                    model.setEmail(rs.getString("email"));
//                    model.setRegistration(rs.getDate("registration"));
//                    model.setPermission(rs.getInt("permission"));
//                }
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

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
        GET("SELECT `id`, `name`, `email`, `password`, `registration`, `permission` FROM `users` WHERE `name` = ?"),
        CREATE("sd"),
        UPDATE("213123"),
        DELETE ("123");


        String QUERY;
        SQL(String s){
            this.QUERY = s;
        }

    }

}
