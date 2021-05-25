package org.dav95s.openNTRIP.Tools.RTCM;

import org.dav95s.openNTRIP.Tools.RTCMStream.BitUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

public class MSG1026Test {
    @Test
    public void maxValues() {

        MSG1026 msg1026 = new MSG1026();
        msg1026.setSystemIdentificationNumber(255);
        msg1026.setProjectionType(11);
        msg1026.setLaFO(90);
        msg1026.setLoFO(180);
        msg1026.setLaSP1(90);
        msg1026.setLaSP2(90);
        msg1026.setEFO(68719476.735);
        msg1026.setNFO(17179869.182);
        byte[] result1 = msg1026.write();
        MSG1026 msg10261 = new MSG1026(result1);
        byte[] result2 = msg10261.write();
        System.out.println(Arrays.toString(result1));
        System.out.println(Arrays.toString(result2));
        System.out.println(msg1026.toString());
        System.out.println(msg10261.toString());
        System.out.println(new BitUtils(result1).toString(' '));
        System.out.println(new BitUtils(result2).toString(' '));
        Assert.assertArrayEquals(result1, result2);
    }

}