package org.adv25.ADVNTRIP;

import org.adv25.ADVNTRIP.Servers.BaseStation;
import org.adv25.ADVNTRIP.Servers.Caster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    final static private Logger logger = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {
        BaseStation.init();
        Caster.init();

    }
}
