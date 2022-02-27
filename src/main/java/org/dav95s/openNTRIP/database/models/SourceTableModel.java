package org.dav95s.openNTRIP.database.models;

import org.dav95s.openNTRIP.database.DataSource;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SourceTableModel {
    //"SELECT CONCAT('STR', ';', name, ';', identifier, ';', format, ';', format_details, ';', carrier, ';', nav_system, ';', network, ';', country, ';', ROUND(latitude,2), ';', ROUND(longitude,2), ';', nmea, ';', solution, ';', generator, ';', compression, ';', authenticator, ';', fee, ';', bitrate, ';', misc) as `sourcetable` FROM mountpoints WHERE caster_id = ? AND available = ?"
    private ArrayList<String> getTable() {
        String sql = "SELECT CONCAT_WS(';','STR', name, identifier, format, format_details, carrier, nav_system, network, country, ROUND(latitude,2), ROUND(longitude,2), nmea, solution, generator, compression, authenticator, fee, bitrate, misc) as `sourcetable` FROM mountpoints WHERE caster_id = ? AND available = ?";

        ArrayList<String> response = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, 1);
            statement.setInt(2, 1);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    response.add(rs.getString("sourcetable"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String getSourcetable() {
        ArrayList<String> table = this.getTable();
        String header = "SOURCETABLE 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n";

        StringBuilder body = new StringBuilder();
        table.forEach((mp) -> {
            body.append(mp);
            body.append("\r\n");
        });
        body.append("ENDSOURCETABLE\r\n");

        String bodyString = body.toString();
        header += "Content-Length: " + bodyString.getBytes(StandardCharsets.ISO_8859_1).length + "\r\n\n";

        return header + bodyString;
    }
}
