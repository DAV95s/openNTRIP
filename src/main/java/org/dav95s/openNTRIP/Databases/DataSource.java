package org.dav95s.openNTRIP.Databases;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {

    private static HikariConfig configHikari = new HikariConfig();
    private static HikariDataSource ds;

    static {

        String configFile = "src/main/resources/db.properties";
        HikariConfig cfg = new HikariConfig(configFile);

        ds = new HikariDataSource(cfg);
    }

    private DataSource() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
