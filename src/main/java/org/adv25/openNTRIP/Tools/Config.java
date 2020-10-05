package org.adv25.openNTRIP.Tools;

import org.adv25.openNTRIP.Clients.Passwords.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {
    public static final Logger log = Logger.getLogger(Config.class.getName());
    private static Config instance;

    public static Config getInstance() {

        synchronized (Config.class) {
            if (instance == null)
                instance = new Config();

            return instance;
        }
    }

    private File configFile = new File("config.ini");
    private Properties properties = new Properties();
    private PasswordHandler passwordHandler;

    private Config() {

        try {

            if (!configFile.exists()) {
                configFile.createNewFile();
            }

            properties.load(new FileReader(configFile));

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Hash Algorithm
        String hashAlgorithm = properties.getProperty("passwordHashAlgorithm");
        if (hashAlgorithm == null)
            hashAlgorithm = "none";

        try {
            passwordHandler = HashAlgorithm.valueOf(hashAlgorithm).passwordHandler;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            log.log(Level.WARNING, "'passwordHashAlgorithm' have illegalArgument!");
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
