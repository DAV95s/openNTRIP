package org.dav95s.openNTRIP.Tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dav95s.openNTRIP.Clients.Passwords.HashAlgorithms;
import org.dav95s.openNTRIP.Clients.Passwords.PasswordHandler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {
    final static private Logger logger = LogManager.getLogger(Config.class.getName());
    private static Config instance;

    public static Config getInstance() {

        synchronized (Config.class) {
            if (instance == null)
                instance = new Config();

            return instance;
        }
    }

    private File configFile = new File("src/main/resources/app.properties");
    private Properties properties = new Properties();
    private PasswordHandler passwordHandler;

    private Config() {
        if (configFile.exists()) {
            logger.info("'app.properties' config file loading..");
            try {
                properties.load(new FileReader(configFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.info("main/resources/app.properties not exists.");
            try {
                configFile.createNewFile();
                properties.load(new FileReader(configFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.info("main/resources/app.properties not exists.");
            properties.setProperty("passwordHashAlgorithm", "BCrypt");
        }

        //Hash Algorithm
        String hashAlgorithm = properties.getProperty("passwordHashAlgorithm");

        try {
            passwordHandler = HashAlgorithms.valueOf(hashAlgorithm).passwordHandler;
        } catch (IllegalArgumentException e) {
            logger.error("'app.properties' -> 'passwordHashAlgorithm' have illegalArgument!");
            logger.error("'passwordHashAlgorithm' set BCrypt");
            passwordHandler = HashAlgorithms.None.passwordHandler;
        }
    }

    public PasswordHandler getPasswordHandler() {
        return passwordHandler;
    }

    public String getProperties(String key) {
        String prop = properties.getProperty(key);
        if (prop == null)
            return null;

        return prop.replaceAll("\"", "");
    }

}
