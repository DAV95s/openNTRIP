package org.adv25.ADVNTRIP.Databases.DAO;

import org.adv25.ADVNTRIP.Databases.DataSource;
import org.adv25.ADVNTRIP.Databases.Models.CasterModel;

import java.sql.*;
import java.util.ArrayList;

public class CastersDAO {

    public ArrayList<CasterModel> readAll() {
        ArrayList<CasterModel> response;

        try (Connection con = DataSource.getConnection();
             Statement statement = con.createStatement()) {

            try (ResultSet rs = statement.executeQuery(SQL.READ_ALL.QUERY)) {
                response = new ArrayList<>();

                if (rs.next()) {
                    CasterModel temp = new CasterModel();

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

    enum SQL {
        READ_ALL("SELECT * FROM casters;");

        String QUERY;

        SQL(String query) {
            this.QUERY = query;
        }
    }
}
