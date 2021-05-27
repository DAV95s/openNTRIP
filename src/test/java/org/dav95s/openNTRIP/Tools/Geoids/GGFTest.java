package org.dav95s.openNTRIP.Tools.Geoids;

import org.dav95s.openNTRIP.CRSUtils.Geoids.GGF;
import org.dav95s.openNTRIP.Tools.Geometry.AngleTools;
import org.dav95s.openNTRIP.Tools.NMEA;
import org.junit.Assert;
import org.junit.Test;

public class GGFTest {
//    GGF ggf = new GGF("C:\\Users\\1663646\\Desktop\\EGM08-R1.GGF");
    GGF ggf = new GGF("C:\\Users\\1663646\\Desktop\\EGM08-R1.GGF");

    @Test
    public void isInAreaLonTest() {
        Assert.assertTrue(ggf.isInAreaLon(15, 25, 66));
        Assert.assertFalse(ggf.isInAreaLon(14.35, 72, 66));
        Assert.assertFalse(ggf.isInAreaLon(14.35, 1, 66));
        Assert.assertFalse(ggf.isInAreaLon(14.35, -144, 66));

        Assert.assertTrue(ggf.isInAreaLon(-14.35, 25, 66));
        Assert.assertTrue(ggf.isInAreaLon(-14.35, -5, 66));
        Assert.assertTrue(ggf.isInAreaLon(-14.35, 0, 66));
        Assert.assertFalse(ggf.isInAreaLon(-14.35, -25, 66));
        Assert.assertFalse(ggf.isInAreaLon(-14.35, 190, 66));

        Assert.assertTrue(ggf.isInAreaLon(-114.35, -90, -14));
        Assert.assertFalse(ggf.isInAreaLon(-114.35, -120, -14));
        Assert.assertFalse(ggf.isInAreaLon(-114.35, -4, -14));
        Assert.assertFalse(ggf.isInAreaLon(-114.35, 25, -14));

        Assert.assertTrue(ggf.isInAreaLon(150, 179, -160));
        Assert.assertTrue(ggf.isInAreaLon(150, -179, -160));
        Assert.assertTrue(ggf.isInAreaLon(150, -180, -160));
        Assert.assertTrue(ggf.isInAreaLon(150, 180, -160));
        Assert.assertFalse(ggf.isInAreaLon(150, 140, -160));
        Assert.assertFalse(ggf.isInAreaLon(150, -150, -160));
    }

    @Test
    public void isInAreaLan() {
        Assert.assertTrue(ggf.isInAreaLat(60, 45, 10));
        Assert.assertTrue(ggf.isInAreaLat(60, 45, -10));
        Assert.assertTrue(ggf.isInAreaLat(60, 0, -10));
        Assert.assertTrue(ggf.isInAreaLat(60, -5, -10));
        Assert.assertTrue(ggf.isInAreaLat(0, -5, -10));
        Assert.assertTrue(ggf.isInAreaLat(-1, -5, -10));
        Assert.assertTrue(ggf.isInAreaLat(-1, -5, -10));

        Assert.assertFalse(ggf.isInAreaLat(60, 88, 40));
        Assert.assertFalse(ggf.isInAreaLat(60, -10, 40));
        Assert.assertFalse(ggf.isInAreaLat(-10, 0, -55));
        Assert.assertFalse(ggf.isInAreaLat(-12, -76, -55));
        Assert.assertFalse(ggf.isInAreaLat(-12, -1, -55));
    }

    @Test
    public void get16PointsAroundUserTest() throws IndexOutOfBoundsException {
        ggf.validateGeoidData();
        System.out.println(ggf.toString());
        NMEA.GPSPosition user = new NMEA.GPSPosition();
        user.lat = (float) AngleTools.dmsToDecimal(60,0,0);
        user.lon = (float) AngleTools.dmsToDecimal(30, 0, 0);

        float[][] points = ggf.get16PointsAroundUser(user);

        for (int y = 0; y < points.length; y++) {
            for (int x = 0; x < points[0].length; x++) {
                System.out.print(points[y][x] + " | ");
            }
            System.out.print("\r\n");
        }
    }
}