package org.adv25.ADVNTRIP;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.adv25.ADVNTRIP.Tools.Bootstrap;

public class Main {
    public static void main(String[] args) {

        new Bootstrap();
        Caster ntripCaster = new Caster();
        ntripCaster.run();
    }
}
