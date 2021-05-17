package org.dav95s.openNTRIP.Tools.Geometry;

public class AngleTools {
    static public double dmsToDecimal(int degrees, int minutes, float seconds){
        if (minutes > 59 || seconds > 59.9999){
            throw new IllegalArgumentException("minutes or seconds more than 59");
        }

        return Math.signum(degrees) * (Math.abs(degrees) + (minutes / 60.0) + (seconds / 3600.0));
    }
}
