package org.adv25.ADVNTRIP.Tools;

//import org.adv25.ADVNTRIP.Databases.DAO.DataSource;

import org.adv25.ADVNTRIP.Databases.DAO.DataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bootstrap {
    private static Logger log = Logger.getLogger(Bootstrap.class.getName());


    public Bootstrap() {
        try (Connection connection = DataSource.getConnection()) {

            try (Statement statement = connection.createStatement()) {

                statement.executeUpdate(usersLog);
                statement.executeUpdate(stations);
                statement.executeUpdate(stationsInfo);
                statement.executeUpdate(fail2ban);
                statement.executeUpdate(config);
                for (String line : config_insert) {
                    statement.executeUpdate(line);
                }
            } catch (SQLException e) {
                log.log(Level.WARNING,"statement error:", e);
            }

            //isConfigured = true;
            log.log(Level.FINE ,"A successful connection to the database!");
        } catch (SQLException e) {
            e.printStackTrace();
            log.warning("Cannot connect to mysql database!");
            System.out.println("The server is running in limited functionality mode. To access all functions, connect the database.");
        }

    }



    String usersLog = "CREATE TABLE IF NOT EXISTS `clients_log` (" +
            "`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT," +
            "`user_id` INT(10) UNSIGNED NOT NULL," +
            "`time_mark` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP()," +
            "`coordinates` POINT NULL," +
            "`ip` VARCHAR(50) NULL DEFAULT NULL," +
            "`user-agent` VARCHAR(500) NULL DEFAULT NULL," +
            "PRIMARY KEY (`id`)," +
            "INDEX `user_id` (`user_id`)," +
            "INDEX `time_mark` (`time_mark`))" +
            "COLLATE='utf8_general_ci';";

    String stations = "CREATE TABLE IF NOT EXISTS `stations` (" +
            "`id` INT NOT NULL AUTO_INCREMENT," +
            "`mountpoint` VARCHAR(100) NOT NULL," +
            "`identifier` VARCHAR(255) NULL," +
            "`format` VARCHAR(255) NULL," +
            "`format-details` VARCHAR(255) NULL," +
            "`carrier` INT(1) NULL DEFAULT NULL," +
            "`nav-system` VARCHAR(255) NULL DEFAULT NULL," +
            "`network` VARCHAR(255) NULL DEFAULT NULL," +
            "`country` VARCHAR(3) NULL DEFAULT NULL," +
            "`latitude` FLOAT NULL DEFAULT NULL," +
            "`longitude` FLOAT NULL DEFAULT NULL," +
            "`nmea` INT NULL DEFAULT NULL," +
            "`solution` INT NULL DEFAULT NULL," +
            "`generator` VARCHAR(255) NULL DEFAULT NULL," +
            "`compression` VARCHAR(255) NULL DEFAULT NULL," +
            "`authentication` VARCHAR(1) NULL DEFAULT NULL," +
            "`fee` VARCHAR(1) NULL DEFAULT NULL," +
            "`bitrate` INT NULL DEFAULT NULL," +
            "`misc` VARCHAR(255) NULL DEFAULT NULL," +
            "PRIMARY KEY (`id`)," +
            "INDEX `mountpoint` (`mountpoint`))" +
            "COLLATE='utf8_general_ci';";

    String stationsInfo = "CREATE TABLE IF NOT EXISTS `stations_info` (" +
            "`id` INT NOT NULL," +
            "`is_online` CHAR(1) NULL DEFAULT NULL," +
            "`properties` JSON NULL DEFAULT NULL," +
            "`password` VARCHAR(255) NULL," +
            "PRIMARY KEY (`id`))" +
            "COLLATE='utf8_general_ci';";

    String fail2ban = "CREATE TABLE IF NOT EXISTS `fail2ban` (" +
            "`id` INT NOT NULL AUTO_INCREMENT," +
            "`ip` VARCHAR(50) NOT NULL DEFAULT '0'," +
            "`try` INT NOT NULL," +
            "`time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP()," +
            "INDEX `time` (`time`)," +
            "INDEX `ip` (`ip`)," +
            "PRIMARY KEY (`id`))" +
            "COLLATE='utf8_general_ci';";

    String config = "CREATE TABLE IF NOT EXISTS `config` (" +
            "`id` INT NOT NULL AUTO_INCREMENT," +
            "`group` VARCHAR(100) NOT NULL," +
            "`key` VARCHAR(100) NOT NULL," +
            "`value` VARCHAR(100) NOT NULL," +
            "PRIMARY KEY (`id`)," +
            "INDEX `group` (`group`)," +
            "INDEX `key` (`key`))" +
            "COLLATE='utf8_general_ci';";

    String[] config_insert = {
            "INSERT IGNORE INTO `config` (`id`, `group`, `key`, `value`) VALUES (1, 'fail2ban', 'attempts', '10');",
            "INSERT IGNORE INTO `config` (`id`, `group`, `key`, `value`) VALUES (2, 'fail2ban', 'ban_time_min', '30');",
            "INSERT IGNORE INTO `config` (`id`, `group`, `key`, `value`) VALUES (3, 'clients', 'authorization', 'none');",
            "INSERT IGNORE INTO `config` (`id`, `group`, `key`, `value`) VALUES (4, 'stations', 'authorization', 'none');",
            "INSERT IGNORE INTO `config` (`id`, `group`, `key`, `value`) VALUES (5, 'stations', 'Hz', '1');",
            "INSERT IGNORE INTO `config` (`id`, `group`, `key`, `value`) VALUES (6, 'system', 'time_out_sec', '10');"
    };


}
