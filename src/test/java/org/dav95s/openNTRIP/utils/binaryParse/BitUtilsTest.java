package org.dav95s.openNTRIP.utils.binaryParse;

import org.junit.Assert;
import org.junit.Test;

public class BitUtilsTest {
    @Test
    public void IntUnsignedTest() {
        BitUtils bitUtils = new BitUtils();
        bitUtils.setInt(2147483647, 31);
        bitUtils.setInt(16777215, 24);
        bitUtils.setPointer(0);
        Assert.assertEquals(2147483647, bitUtils.getUnsignedInt(31));
        Assert.assertEquals(16777215, bitUtils.getUnsignedInt(24));
    }

    @Test
    public void IntSignedTest() {
        BitUtils bitUtils = new BitUtils();
        bitUtils.setInt(0b10000000, 8);
        bitUtils.setInt(0b1111111110000000, 16);
        bitUtils.setInt(0b1101111110000000, 16);
        bitUtils.setPointer(0);
        Assert.assertEquals(-128, bitUtils.getSignedInt(8));
        Assert.assertEquals(-128, bitUtils.getSignedInt(16));
        Assert.assertEquals(-8320, bitUtils.getSignedInt(16));
    }

}