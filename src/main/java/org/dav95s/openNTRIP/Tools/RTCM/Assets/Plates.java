package org.dav95s.openNTRIP.Tools.RTCM.Assets;

public enum Plates {
    Africa(1),
    Antarctica(2),
    Arabia(3),
    Australia(4),
    Caribbean(5),
    Cocos(6),
    Eurasia(7),
    India(8),
    NorthAmerica(9),
    Nazca(10),
    Pacific(11),
    SouthAmerica(12),
    JuandeFuca(13),
    Philippine(14),
    Rivera(15),
    Scotia(16);

    private int id;

    public int getId() {
        return id;
    }

    Plates(int i) {
        id = i;
    }
}
