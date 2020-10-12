package org.adv25.openNTRIP.Tools;

import org.adv25.openNTRIP.Clients.Passwords.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

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

        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
                properties.load(new FileReader(configFile));
                properties.setProperty("passwordHashAlgorithm", "None");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Hash Algorithm
        String hashAlgorithm = properties.getProperty("passwordHashAlgorithm");
        if (hashAlgorithm == null)
            hashAlgorithm = "None";

        try {
            passwordHandler = HashAlgorithm.valueOf(hashAlgorithm).passwordHandler;
        } catch (IllegalArgumentException e) {
            logger.info("'app.properties' -> 'passwordHashAlgorithm' have illegalArgument!");
            logger.info("'passwordHashAlgorithm' set None");
            passwordHandler = HashAlgorithm.None.passwordHandler;
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
