package org.adv25.ADVNTRIP.Tools;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {

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

    private Config(){
        try {
        	if (!configFile.exists()) {
				configFile.createNewFile();
			}
            properties.load(new FileReader(configFile));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperties(String key) {
    	return properties.getProperty(key);
    }
}
