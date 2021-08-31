package org.dav95s.openNTRIP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            Bootstrap server = new Bootstrap();
            server.start();
        } catch (Exception e) {
            logger.error("FATAL ERROR!", e);
        }
    }
}
