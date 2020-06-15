package org.adv25.ADVNTRIP.Spatial;

import java.math.BigDecimal;

public class Point_lla {

    private BigDecimal lat;
    private BigDecimal lon;
    private BigDecimal alt;

    public Point_lla(String lat, String lon, String alt) {
        this.lat = new BigDecimal(lat);
        this.lon = new BigDecimal(lon);
        this.alt = new BigDecimal(alt);
    }

    public Point_lla(String wkt) {
        if (wkt == null)
            return;

        String clear = wkt.substring(wkt.indexOf("(") + 1, wkt.indexOf(")"));
        lat = new BigDecimal(clear.split(" ")[0]);
        lon = new BigDecimal(clear.split(" ")[1]);
    }

    public Point_lla() {

    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public void setLon(BigDecimal lon) {
        this.lon = lon;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public BigDecimal getLon() {
        return lon;
    }

    public BigDecimal getAlt() {
        return alt;
    }

    public void setAlt(BigDecimal alt) {
        this.alt = alt;
    }

    public String getWKT() {
        return "POINT(" + lat + " " + lon + ")";
    }
}
