package org.adv25.ADVNTRIP.Databases;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.adv25.ADVNTRIP.Tools.Config;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZoneId;

public class DataSource {

    private static HikariConfig configHikari = new HikariConfig();
    private static HikariDataSource ds;

    static {
//        Config config = Config.getInstance();
//        String host = config.getProperties("mysqlHost");
//        String user = config.getProperties("mysqlUser");
//        String db = config.getProperties("mysqlDb");
//        String port = config.getProperties("mysqlPort");

//        configHikari.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + db + "?serverTimezone=" + ZoneId.systemDefault());
//        configHikari.setUsername(config.getProperties("mysqlUser"));
//        configHikari.setPassword(config.getProperties("mysqlPass"));
//        configHikari.addDataSourceProperty( "cachePrepStmts" , "true" );
//        configHikari.addDataSourceProperty( "prepStmtCacheSize" , "250" );
//        configHikari.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );

        String configFile = "src/main/resources/db.properties";
        HikariConfig cfg = new HikariConfig(configFile);

        ds = new HikariDataSource(cfg);
    }

    private DataSource() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
