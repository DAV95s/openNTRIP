package org.adv25.ADVNTRIP.Databases;

import org.adv25.ADVNTRIP.Tools.Config;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;

import java.sql.*;
import java.time.ZoneId;

public class DbManager {
    private static DbManager instance;

    public static DbManager getInstance() {

        synchronized (DbManager.class) {
            if (instance == null)
                instance = new DbManager();

            return instance;
        }

    }

    static String host;
    static String port;
    static String user;
    static String password;
    static String db;
    static String connectUrl;

    static boolean isConfigured = false;

    private DbManager() {
        Config config = Config.getInstance();

        host = config.getProperties("MysqlHost").replaceAll("\"", "");
        user = config.getProperties("MysqlUser").replaceAll("\"", "");
        port = config.getProperties("MysqlPort").replaceAll("\"", "");
        password = config.getProperties("MysqlPass").replaceAll("\"", "");
        db = config.getProperties("MysqlDb").replaceAll("\"", "");

        connectUrl = "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false" + "&serverTimezone=" + ZoneId.systemDefault();

        generateTable();
    }


    public boolean clientAuthorization(String userName, String userPassword) {
        if (!isConfigured)
            return false;

        boolean response = false;
        int userId;
        String sql = "SELECT id, name, password FROM users WHERE name = ?;";

        try (Connection connection = DriverManager.getConnection(connectUrl, user, password)) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                userId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String pass = resultSet.getString("password");

                if (name.equals(userName) && pass.equals(userPassword)) {
                    return true;
                }
            }
        } catch (CommunicationsException c) {
            throw new SecurityException("Database not working!");
        } catch (SQLException e) {
            e.printStackTrace();
            throw  new SecurityException("SQLException");
        }

        throw new SecurityException("Wrong password!");
    }


    private static void generateTable() {

        String usersTable = "CREATE TABLE IF NOT EXISTS `users` (" +
                "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT," +
                "`name` VARCHAR(100) NULL DEFAULT NULL," +
                "`email` VARCHAR(100) NULL DEFAULT NULL," +
                "`password` VARCHAR(100) NULL DEFAULT NULL," +
                "`registration` DATE NULL DEFAULT NULL," +
                "`permission` TINYINT NULL DEFAULT NULL," +
                "PRIMARY KEY (`id`)," +
                "INDEX `name` (`name`))" +
                "COLLATE='utf8_general_ci';";

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
                "`station_id` INT NOT NULL," +
                "`is_online` CHAR(1) NULL DEFAULT NULL," +
                "`properties` JSON NULL DEFAULT NULL," +
                "PRIMARY KEY (`station_id`))" +
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


        try (Connection connection = DriverManager.getConnection(connectUrl, user, password)) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(usersTable);
            statement.executeUpdate(usersLog);
            statement.executeUpdate(stations);
            statement.executeUpdate(stationsInfo);
            statement.executeUpdate(fail2ban);
            statement.executeUpdate(config);

            for (String line : config_insert) {
                statement.executeUpdate(line);
            }

            isConfigured = true;
            System.out.println("A successful connection to the database!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Cannot connect to mysql database!");
            System.out.println("The server is running in limited functionality mode. To access all functions, connect the database.");
        }
    }
}
