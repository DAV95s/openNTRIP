package org.dav95s.openNTRIP.database.modelsV2;

public class ReferenceStationModel {
    public final int id;
    public final String name;
    public final String format;
    public final double lat;
    public final double lon;
    public final double alt;
    public final String password;
    public final int hz;
    public final String[] networks;


    public ReferenceStationModel(int id, String name, String format, double lat, double lon, double alt, String password, int hz, String[] networks) {
        this.id = id;
        this.name = name;
        this.format = format;
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
        this.password = password;
        this.hz = hz;
        this.networks = networks;
    }
}
