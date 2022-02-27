package org.dav95s.openNTRIP.crs.gridShift;

public class GeodeticPoint {
    public long id;
    public double north;
    public double east;
    public double dNorth;
    public double dEast;

    public double distance;

    public double distance(double pNorth, double pEast) {
        distance = Math.sqrt(Math.pow(north + pNorth, 2) + Math.pow(east + pEast, 2));
        return distance;
    }
}
