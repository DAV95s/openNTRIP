package org.dav95s.openNTRIP.utils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ServerProperties extends Properties {
    public ServerProperties() {
        initProperties(Config.SERVER_PROPERTIES_FILENAME);
    }

    public ServerProperties(String propertiesFileName) {
        initProperties(propertiesFileName);
    }

    public ServerProperties(Path propertiesFileNamePath) {
        initProperties(propertiesFileNamePath);
    }

    private static Path getFileInCurrentDir(String filename) {
        return Paths.get(System.getProperty("user.dir"), filename);
    }

    public static Path getCurrentDir() {
        return Paths.get(System.getProperty("user.dir"));
    }

    /**
     * First loads properties file from class path after that from current folder.
     * So properties file in current folder is always overrides properties in classpath.
     *
     * @param filePropertiesName - name of properties file, for example "twitter4j.properties"
     */
    private void initProperties(String filePropertiesName) {
        if (!filePropertiesName.startsWith("/")) {
            filePropertiesName = "/" + filePropertiesName;
        }

        try (InputStream classPath = ServerProperties.class.getResourceAsStream(filePropertiesName)) {
            if (classPath != null) {
                load(classPath);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error getting properties file : " + filePropertiesName, e);
        }

        Path curDirPath = getFileInCurrentDir(filePropertiesName);
        if (Files.exists(curDirPath)) {
            try (InputStream curFolder = Files.newInputStream(curDirPath)) {
                load(curFolder);
            } catch (Exception e) {
                throw new RuntimeException("Error getting properties file : " + filePropertiesName, e);
            }
        }

    }

    private void initProperties(Path path) {
        if (Files.exists(path)) {
            try (InputStream curFolder = Files.newInputStream(path)) {
                load(curFolder);
            } catch (Exception e) {
                throw new RuntimeException("Error getting properties file : " + path, e);
            }
        }
    }

    public int getIntProperty(String propertyName) {
        return ParseUtil.parseInt(getProperty(propertyName));
    }

    public int getIntProperty(String propertyName, int defaultValue) {
        if (getProperty(propertyName) == null || "".equals(getProperty(propertyName))) {
            return defaultValue;
        }
        return ParseUtil.parseInt(getProperty(propertyName));
    }

    public boolean getBoolProperty(String propertyName) {
        return Boolean.parseBoolean(getProperty(propertyName));
    }

    public long getLongProperty(String propertyName) {
        return ParseUtil.parseLong(getProperty(propertyName));
    }
}
