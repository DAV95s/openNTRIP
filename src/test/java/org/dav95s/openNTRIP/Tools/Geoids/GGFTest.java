package org.dav95s.openNTRIP.Tools.Geoids;

import org.dav95s.openNTRIP.CRSUtils.Geoids.GGF;
import org.junit.Assert;
import org.junit.Test;

import static org.dav95s.openNTRIP.Tools.Geometry.AngleTools.dmsToDecimal;

public class GGFTest {

    @Test
    public void testSpb() {
        GGF ggf = new GGF("src/test/resources/geoids/spb.GGF");
        Assert.assertEquals(15.86201, ggf.getValueByPoint(60, 30), 0.00001);
        Assert.assertEquals(15.84909, ggf.getValueByPoint(dmsToDecimal(60, 1), dmsToDecimal(30, 1)), 0.00001);
        Assert.assertEquals(15.83819, ggf.getValueByPoint(dmsToDecimal(60, 2), dmsToDecimal(30, 2)), 0.00001);
        Assert.assertEquals(15.79763, ggf.getValueByPoint(dmsToDecimal(60, 2), dmsToDecimal(31, 2)), 0.00001);

        Assert.assertEquals(15.79657, ggf.getValueByPoint(dmsToDecimal(60, 2, 10), dmsToDecimal(31, 2, 10)), 0.00001);
        Assert.assertEquals(15.79553, ggf.getValueByPoint(dmsToDecimal(60, 2, 20), dmsToDecimal(31, 2, 20)), 0.00001);
        Assert.assertEquals(15.79450, ggf.getValueByPoint(dmsToDecimal(60, 2, 30), dmsToDecimal(31, 2, 30)), 0.00001);
        Assert.assertEquals(15.79349, ggf.getValueByPoint(dmsToDecimal(60, 2, 40), dmsToDecimal(31, 2, 40)), 0.00001);
        Assert.assertEquals(15.79250, ggf.getValueByPoint(dmsToDecimal(60, 2, 50), dmsToDecimal(31, 2, 50)), 0.00001);
        Assert.assertEquals(15.78683, ggf.getValueByPoint(dmsToDecimal(60, 3, 55), dmsToDecimal(31, 3, 55)), 0.00001);
        Assert.assertEquals(15.85548, ggf.getValueByPoint(dmsToDecimal(60, 0, 30), dmsToDecimal(30, 0, 30)), 0.00001);
    }

    @Test
    public void testEngland() {
        GGF ggf = new GGF("src/test/resources/geoids/england.GGF");
        Assert.assertEquals(45.00320, ggf.getValueByPoint(51, 1), 0.00001);
        Assert.assertEquals(46.41333, ggf.getValueByPoint(51, -1), 0.00001);


        Assert.assertEquals(44.99883, ggf.getValueByPoint(dmsToDecimal(51, 3), dmsToDecimal(1, 3)), 0.00001);
        Assert.assertEquals(44.99875, ggf.getValueByPoint(dmsToDecimal(51, 3,10), dmsToDecimal(1, 3,10)), 0.00001);
        Assert.assertEquals(44.99867, ggf.getValueByPoint(dmsToDecimal(51, 3,20), dmsToDecimal(1, 3,20)), 0.00001);
        Assert.assertEquals(44.99857, ggf.getValueByPoint(dmsToDecimal(51, 3,30), dmsToDecimal(1, 3,30)), 0.00001);
        Assert.assertEquals(44.99847, ggf.getValueByPoint(dmsToDecimal(51, 3,40), dmsToDecimal(1, 3,40)), 0.00001);
        Assert.assertEquals(44.99836, ggf.getValueByPoint(dmsToDecimal(51, 3,50), dmsToDecimal(1, 3,50)), 0.00001);
        Assert.assertEquals(44.99830, ggf.getValueByPoint(dmsToDecimal(51, 3,55), dmsToDecimal(1, 3,55)), 0.00001);

        Assert.assertEquals(46.54407, ggf.getValueByPoint(dmsToDecimal(51, 3), dmsToDecimal(-1, 3)), 0.00001);
        Assert.assertEquals(46.55101, ggf.getValueByPoint(dmsToDecimal(51, 3,10), dmsToDecimal(-1, 3,10)), 0.00001);
        Assert.assertEquals(46.55794, ggf.getValueByPoint(dmsToDecimal(51, 3,20), dmsToDecimal(-1, 3,20)), 0.00001);
        Assert.assertEquals(46.56487, ggf.getValueByPoint(dmsToDecimal(51, 3,30), dmsToDecimal(-1, 3,30)), 0.00001);
        Assert.assertEquals(46.57180, ggf.getValueByPoint(dmsToDecimal(51, 3,40), dmsToDecimal(-1, 3,40)), 0.00001);
        Assert.assertEquals(46.57872, ggf.getValueByPoint(dmsToDecimal(51, 3,50), dmsToDecimal(-1, 3,50)), 0.00001);
        Assert.assertEquals(46.58218, ggf.getValueByPoint(dmsToDecimal(51, 3,55), dmsToDecimal(-1, 3,55)), 0.00001);
    }

    @Test
    public void testAustralia() {
        GGF ggf = new GGF("src/test/resources/geoids/australia.GGF");
        Assert.assertEquals(35.06186, ggf.getValueByPoint(-16, 130), 0.00001);
        Assert.assertEquals(35.06186, ggf.getValueByPoint(-16, 130), 0.00001);

        Assert.assertEquals(29.24346, ggf.getValueByPoint(dmsToDecimal(-16,55,  2), dmsToDecimal(125,58,  23)), 0.00001);
        Assert.assertEquals(29.78914, ggf.getValueByPoint(dmsToDecimal(-16,30, 10), dmsToDecimal(125,30,  10)), 0.00001);
        Assert.assertEquals(29.78705, ggf.getValueByPoint(dmsToDecimal(-16,30, 20), dmsToDecimal(125,30,  20)), 0.00001);
        Assert.assertEquals(29.78502, ggf.getValueByPoint(dmsToDecimal(-16,30, 30), dmsToDecimal(125,30,  30)), 0.00001);
        Assert.assertEquals(29.78306, ggf.getValueByPoint(dmsToDecimal(-16,30, 40), dmsToDecimal(125,30,  40)), 0.00001);
        Assert.assertEquals(29.78115, ggf.getValueByPoint(dmsToDecimal(-16,30, 50), dmsToDecimal(125,30,  50)), 0.00001);
        Assert.assertEquals(29.78023, ggf.getValueByPoint(dmsToDecimal(-16,30, 55), dmsToDecimal(125,30,  55)), 0.00001);
    }

    @Test
    public void testFlorida() {
        GGF ggf = new GGF("src/test/resources/geoids/florida.GGF");
        Assert.assertEquals(-27.37862, ggf.getValueByPoint(28, -82), 0.00001);
        Assert.assertEquals(-29.32500, ggf.getValueByPoint(28, -81), 0.00001);
        Assert.assertEquals(-29.06320, ggf.getValueByPoint(dmsToDecimal(28,30,10), dmsToDecimal(-81,30,10)), 0.00001);
        Assert.assertEquals(-29.05826, ggf.getValueByPoint(dmsToDecimal(28,30,20), dmsToDecimal(-81,30,20)), 0.00001);
        Assert.assertEquals(-29.05338, ggf.getValueByPoint(dmsToDecimal(28,30,30), dmsToDecimal(-81,30,30)), 0.00001);
        Assert.assertEquals(-29.04858, ggf.getValueByPoint(dmsToDecimal(28,30,40), dmsToDecimal(-81,30,40)), 0.00001);
        Assert.assertEquals(-29.04384, ggf.getValueByPoint(dmsToDecimal(28,30,50), dmsToDecimal(-81,30,50)), 0.00001);
        Assert.assertEquals(-29.04149, ggf.getValueByPoint(dmsToDecimal(28,30,55), dmsToDecimal(-81,30,55)), 0.00001);
    }

    @Test
    public void testBering() {
        GGF ggf = new GGF("src/test/resources/geoids/bering.GGF");
        Assert.assertEquals(4.85217, ggf.getValueByPoint(63, 180), 0.00001);
        Assert.assertEquals(5.54611, ggf.getValueByPoint(63, 179), 0.00001);
        Assert.assertEquals(5.03305, ggf.getValueByPoint(63, -179), 0.00001);
        Assert.assertEquals(4.43816, ggf.getValueByPoint(dmsToDecimal(63,30,10), dmsToDecimal(-178,30,10)), 0.00001);
        Assert.assertEquals(4.43570, ggf.getValueByPoint(dmsToDecimal(63,30,20), dmsToDecimal(-178,30,20)), 0.00001);
        Assert.assertEquals(4.43325, ggf.getValueByPoint(dmsToDecimal(63,30,30), dmsToDecimal(-178,30,30)), 0.00001);
        Assert.assertEquals(4.43083, ggf.getValueByPoint(dmsToDecimal(63,30,40), dmsToDecimal(-178,30,40)), 0.00001);
        Assert.assertEquals(4.42842, ggf.getValueByPoint(dmsToDecimal(63,30,50), dmsToDecimal(-178,30,50)), 0.00001);
        Assert.assertEquals(4.42722, ggf.getValueByPoint(dmsToDecimal(63,30,55), dmsToDecimal(-178,30,55)), 0.00001);
        Assert.assertEquals(4.42710, ggf.getValueByPoint(dmsToDecimal(63,30, 55.5F), dmsToDecimal(-178,30,55.5F)), 0.00001);

    }

}