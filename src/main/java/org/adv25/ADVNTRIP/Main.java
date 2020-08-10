package org.adv25.ADVNTRIP;

import org.adv25.ADVNTRIP.Servers.ReferenceStation;
import org.adv25.ADVNTRIP.Tools.Bootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    final static private Logger logger = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {
        ReferenceStation.init();

        Bootstrap bootstrap = Bootstrap.getInstance();
    }
}
