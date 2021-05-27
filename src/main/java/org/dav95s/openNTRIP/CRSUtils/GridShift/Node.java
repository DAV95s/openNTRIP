package org.dav95s.openNTRIP.CRSUtils.GridShift;

public class Node {
    public double north;
    public double east;

    public boolean isPositive = true;

    public double dNorth;
    public double dEast;


    public Node(double north, double east, double dNorth, double dEast) {
        if (north < 0 || east < 0)
            isPositive = false;

        this.north = north;
        this.east = east;
        this.dNorth = dNorth;
        this.dEast = dEast;
    }
}
