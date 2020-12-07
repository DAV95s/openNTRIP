package org.dav95s.openNTRIP;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    final static private Logger logger = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            ServerBootstrap.start();
        } catch (IOException | SQLException e) {
            logger.fatal(e);
        }
    }
}
