package org.dav95s.openNTRIP.CRSUtils.Geoids;

import org.dav95s.openNTRIP.Tools.NMEA;

interface IGeoidModel {
    float[][] get16PointsAroundUser(NMEA.GPSPosition user);
}
