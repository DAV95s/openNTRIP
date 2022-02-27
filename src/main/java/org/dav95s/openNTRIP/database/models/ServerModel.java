package org.dav95s.openNTRIP.database.models;

public class ServerModel {
    int id;
    int port;

    public void read() {
        String sql = "SELECT * FROM `reference_stations` WHERE `id` = ?";

//        try (Connection con = DataSource.getConnection();
//             PreparedStatement statement = con.prepareStatement(sql)) {
//
//            statement.setInt(1, id);
//
//            try (ResultSet rs = statement.executeQuery()) {
//                if (rs.next()) {
//                    this.name = rs.getString("name");
//                    this.identifier = rs.getString("identifier");
//                    this.format = rs.getString("format");
//                    this.format_details = rs.getString("format_details");
//                    this.carrier = rs.getInt("carrier");
//                    this.nav_system = rs.getString("nav_system");
//                    this.country = rs.getString("country");
//                    this.position.lat = (float) rs.getDouble("lat");
//                    this.position.lon = (float) rs.getDouble("lon");
//                    this.position.altitude = (float) rs.getDouble("alt");
//                    this.bitrate = rs.getInt("bitrate");
//                    this.misc = rs.getString("misc");
//                    this.password = rs.getString("password");
//                    this.hz = rs.getInt("hz");
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        } catch (SQLException e) {
//            logger.error("SQL Error", e);
//            return false;
//        }
   }
}
