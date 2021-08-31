package org.dav95s.openNTRIP.Tools.RTCM.Assets;

public enum Plates {
    NONE(0), AFRC(1), ANTA(2), ARAB(3), AUST(4), CARB(5), COCO(6), EURA(7), INDI(8),
    NOAM(9), NAZC(10), PCFC(11), SOAM(12), JUFU(13), PHIL(14), RIVR(15), SCOT(16);

    int plateNmb;

    public int getPlateNmb() {
        return plateNmb;
    }

    Plates(int i) {
        plateNmb = i;
    }
}