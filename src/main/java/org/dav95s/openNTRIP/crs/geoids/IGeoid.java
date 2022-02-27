package org.dav95s.openNTRIP.crs.geoids;

public interface IGeoid {
    double getValueByPoint(double lat, double lon);
}
