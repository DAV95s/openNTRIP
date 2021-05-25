package org.dav95s.openNTRIP.Tools.RTCM;

import org.dav95s.openNTRIP.Tools.RTCMStream.BitUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class MSG1027Test {
    @Test
    public void maxValues() {

        MSG1027 msg1027 = new MSG1027();
        msg1027.setSystemIdentificationNumber(255);
        msg1027.setProjectionType(11);
        msg1027.setLaPC(90);
        msg1027.setLoPC(180);
        msg1027.setAzIL(360);
        msg1027.setRectifiedToSkew(360.369098741);
        msg1027.setSILppm(1.003737418);
        msg1027.setEPC(68719476.735);
        msg1027.setNPC(17179869.183);

        byte[] result1 = msg1027.write();
        MSG1027 msg10271 = new MSG1027(result1);
        byte[] result2 = msg10271.write();

        System.out.println(Arrays.toString(result1));
        System.out.println(Arrays.toString(result2));
        System.out.println(msg1027.toString());
        System.out.println(msg10271.toString());
        System.out.println(new BitUtils(result1).toString(' '));
        System.out.println(new BitUtils(result2).toString(' '));
        Assert.assertArrayEquals(result1, result2);
    }
}