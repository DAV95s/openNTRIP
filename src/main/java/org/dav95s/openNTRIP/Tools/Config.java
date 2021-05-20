package org.dav95s.openNTRIP.Tools;


import org.dav95s.openNTRIP.Clients.Passwords.HashAlgorithms;
import org.dav95s.openNTRIP.Clients.Passwords.PasswordHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {
    final static private Logger logger = LoggerFactory.getLogger(Config.class.getName());
    private static Config instance;

    public static Config getInstance() {

        synchronized (Config.class) {
            if (instance == null)
                instance = new Config();

            return instance;
        }
    }

    private final Properties properties = new Properties();
    private PasswordHandler passwordHandler;

    //todo rewrite this bullshit
    private Config() {
        File configFile = new File("src/main/resources/app.properties");
        if (configFile.exists()) {
            logger.info("'app.properties' config file loading..");
            try {
                properties.load(new FileReader(configFile));
                logger.info("'Config file ok!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.info("app.properties doesn't exist.");
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
            passwordHandler = HashAlgorithms.BCrypt.passwordHandler;
        }
    }

    //todo rewrite
    public String getProperties(String key) {
        String prop = properties.getProperty(key);
        if (prop == null)
            return null;

        return prop.replaceAll("\"", "");
    }

    public String getDefaultEmailDomain() {
        String domain = this.getProperties("defaultEmailDomain");
        return domain == null ? "localhost" : domain;
    }

    public PasswordHandler getPasswordHandler() {
        return passwordHandler;
    }

}
