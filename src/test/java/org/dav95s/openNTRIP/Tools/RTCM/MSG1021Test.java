package org.dav95s.openNTRIP.Tools.RTCM;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class MSG1021Test extends TestCase {
    @Test
    public void msg1021MaxValues() {
        MSG1021 msg1021Max = new MSG1021();
        msg1021Max.setMessageNumber(1021);
        msg1021Max.setSourceName("0123456789012345678901234567891");
        msg1021Max.setTargetName("0123456789012345678901234567891");
        msg1021Max.setSystemIdentificationNumber(255);
        msg1021Max.setUtilizedTransformationMessageIndicator(10);
        msg1021Max.setPlateNumber(31);
        msg1021Max.setComputationIndicator(15);
        msg1021Max.setHeightIndicator(3);
        msg1021Max.setB_valid(-324000);
        msg1021Max.setL_valid(648000);
        msg1021Max.setdB_valid(32766);
        msg1021Max.setdL_valid(32766);
        msg1021Max.setdX(new BigDecimal("4194.303"));
        msg1021Max.setdY(new BigDecimal("4194.303"));
        msg1021Max.setdZ(new BigDecimal("4194.303"));
        msg1021Max.setRx(new BigDecimal("-42949.67294"));
        msg1021Max.setRy(new BigDecimal("42949.67294"));
        msg1021Max.setRz(new BigDecimal("-42949.67294"));
        msg1021Max.setdS(new BigDecimal("-167.77215"));
        msg1021Max.setAdd_as(new BigDecimal("16777.215"));
        msg1021Max.setAdd_bs(new BigDecimal("33554.431"));
        msg1021Max.setAdd_at(new BigDecimal("16777.215"));
        msg1021Max.setAdd_bt(new BigDecimal("33554.431"));
        msg1021Max.setHrInd(7);
        msg1021Max.setVrInd(7);

        byte[] check1 = msg1021Max.write();
        MSG1021 msg10212 = new MSG1021(check1);
        byte[] check2 = msg10212.write();

        Assert.assertArrayEquals(check1, check2);

    }

}