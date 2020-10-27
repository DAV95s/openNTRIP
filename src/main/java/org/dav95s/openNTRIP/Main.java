package org.dav95s.openNTRIP;

import org.dav95s.openNTRIP.Tools.Bootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    final static private Logger logger = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Bootstrap bootstrap = Bootstrap.getInstance();
    }
}
