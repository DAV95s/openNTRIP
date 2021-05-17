package org.dav95s.openNTRIP.Tools.Geoids;

import org.dav95s.openNTRIP.Tools.NMEA;

interface IGeoidModel {
    float[][] get16PointsAroundUser(NMEA.GPSPosition user);
}
