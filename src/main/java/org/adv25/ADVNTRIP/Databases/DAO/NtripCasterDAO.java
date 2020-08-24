package org.adv25.ADVNTRIP.Databases.DAO;

import org.adv25.ADVNTRIP.Databases.DataSource;
import org.adv25.ADVNTRIP.Databases.Models.NtripCasterModel;

import java.sql.*;
import java.util.ArrayList;

public class NtripCasterDAO {

    public ArrayList<NtripCasterModel> readAll() {
        ArrayList<NtripCasterModel> response;

        try (Connection con = DataSource.getConnection();
             Statement statement = con.createStatement()) {

            try (ResultSet rs = statement.executeQuery(SQL.READ_ALL.QUERY)) {
                response = new ArrayList<>();

                while (rs.next()) {
                    NtripCasterModel temp = new NtripCasterModel();

                    temp.setId(rs.getInt("id"));
                    temp.setAddress(rs.getString("address"));
                    temp.setPort(rs.getInt("port"));
                    temp.setGroup_id(rs.getInt("group_id"));
                    temp.setStatus(rs.getBoolean("status"));

                    response.add(temp);
                }

                return response;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public NtripCasterModel read(int id) {
        NtripCasterModel response = null;
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(SQL.READ.QUERY)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    response = new NtripCasterModel();
                    response.setId(rs.getInt("id"));
                    response.setAddress(rs.getString("address"));
                    response.setPort(rs.getInt("port"));
                    response.setGroup_id(rs.getInt("group_id"));
                    response.setStatus(rs.getBoolean("status"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }

    enum SQL {
        READ_ALL("SELECT * FROM casters;"),
        READ("SELECT * FROM casters WHERE `id` = ?");
        String QUERY;

        SQL(String query) {
            this.QUERY = query;
        }
    }
}
