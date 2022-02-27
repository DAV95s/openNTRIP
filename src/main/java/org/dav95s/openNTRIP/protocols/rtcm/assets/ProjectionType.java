package org.dav95s.openNTRIP.protocols.rtcm.assets;

public enum ProjectionType {
    NONE(0), TM(1), TMS(2), LCC1SP(3), LCC2SP(4),
    LCCW(5), CS(6), OM(7), OS(8), MC(9), PS(10), DS(11);

    public int getNmb() {
        return nmb;
    }

    int nmb;

    ProjectionType(int i) {
        nmb = i;
    }
}
