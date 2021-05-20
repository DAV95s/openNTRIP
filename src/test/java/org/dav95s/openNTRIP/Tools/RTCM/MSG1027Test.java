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
        msg1027.setLaPC(new BigDecimal("89.66553321"));
        msg1027.setLoPC(new BigDecimal("179.999999993"));
        msg1027.setAzIL(new BigDecimal("360"));
        msg1027.setRectifiedToSkew(new BigDecimal("359.6309013"));
        msg1027.setSILppm(BigDecimal.valueOf(1000000));
        msg1027.setEPC(BigDecimal.valueOf(68719476.735));
        msg1027.setNPC(BigDecimal.valueOf(17179869.183));

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