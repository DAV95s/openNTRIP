package org.dav95s.openNTRIP.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {

    private static final HikariConfig configHikari = new HikariConfig();
    private static final HikariDataSource ds;

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
