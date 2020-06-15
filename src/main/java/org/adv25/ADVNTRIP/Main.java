package org.adv25.ADVNTRIP;

import org.adv25.ADVNTRIP.Servers.BaseStation;
import org.adv25.ADVNTRIP.Servers.Caster;
import org.apache.log4j.Logger;

public class Main {
    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        BaseStation.init();
        Caster.init();
    }
}
