package org.dav95s.openNTRIP.Spatial;

import org.dav95s.openNTRIP.Tools.NMEA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PointLla {
    final static private Logger logger = LogManager.getLogger(PointLla.class.getName());
    private BigDecimal lat;
    private BigDecimal lon;
    private BigDecimal alt;

    public PointLla(double lat, double lon) {
        this.lat = new BigDecimal(lat).setScale(5, RoundingMode.HALF_EVEN);
        this.lon = new BigDecimal(lon).setScale(5, RoundingMode.HALF_EVEN);
    }


    public PointLla(String wkt) {
        if (wkt == null)
            return;

        String clear = wkt.substring(wkt.indexOf("(") + 1, wkt.indexOf(")"));
        lat = new BigDecimal(clear.split(" ")[0]).setScale(5, RoundingMode.HALF_EVEN);
        lon = new BigDecimal(clear.split(" ")[1]).setScale(5, RoundingMode.HALF_EVEN);
    }

    public PointLla() {

    }

    public boolean isZero() {
        if (lat.doubleValue() == 0.0 && lon.doubleValue() == 0.0)
            return true;

        return false;
    }

    public float distance(float lat, float lon) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat - this.lat.floatValue());
        double dLng = Math.toRadians(lon - this.lon.floatValue());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(this.lat.floatValue())) * Math.cos(Math.toRadians(lat)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (float) (earthRadius * c);
    }

    public float distance(NMEA.GPSPosition point) {
        return distance(point.lat, point.lon);
    }

    public float distance(PointLla point) {
        return distance(point.lat.floatValue(), point.lon.floatValue());
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

    @Override
    public String toString() {
        return "PointLla{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", alt=" + alt +
                '}';
    }
}
