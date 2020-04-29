package org.adv25.ADVNTRIP;

import org.adv25.ADVNTRIP.Tools.Bootstrap;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        new Bootstrap();
        Caster ntripCaster = new Caster();
        ntripCaster.run();
    }
}
