package org.dav95s.openNTRIP.Tools.RTCM;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Bytes;
import org.dav95s.openNTRIP.Tools.RTCMStream.BitUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MSG1022 extends MSG1021 {

    BigDecimal Xp;
    BigDecimal Yp;
    BigDecimal Zp;

    public MSG1022(byte[] msg) {
        BitUtils bitUtils = new BitUtils(msg);
        bitUtils.setPointer(24);

        messageNumber = bitUtils.getUnsignedInt(12);
        SourceNameCounter = bitUtils.getUnsignedInt(5);
        SourceName = bitUtils.getString(SourceNameCounter * 8);
        TargetNameCounter = bitUtils.getUnsignedInt(5);
        TargetName = bitUtils.getString(TargetNameCounter * 8);
        SystemIdentificationNumber = bitUtils.getUnsignedInt(8);
        UtilizedTransformationMessageIndicator = bitUtils.getUnsignedInt(10);
        PlateNumber = bitUtils.getUnsignedInt(5);
        ComputationIndicator = bitUtils.getUnsignedInt(4);
        HeightIndicator = bitUtils.getUnsignedInt(2);
        B_valid = bitUtils.getSignedInt(19) * 2;
        L_valid = bitUtils.getSignedInt(20) * 2;
        dB_valid = bitUtils.getUnsignedInt(14) * 2;
        dL_valid = bitUtils.getUnsignedInt(14) * 2;
        dX = new BigDecimal(bitUtils.getSignedInt(23)).multiply(BigDecimal.valueOf(0.0001));
        dY = new BigDecimal(bitUtils.getSignedInt(23)).multiply(BigDecimal.valueOf(0.0001));
        dZ = new BigDecimal(bitUtils.getSignedInt(23)).multiply(BigDecimal.valueOf(0.0001));
        Rx = new BigDecimal(bitUtils.getSignedLong(32)).multiply(BigDecimal.valueOf(0.00002));
        Ry = new BigDecimal(bitUtils.getSignedLong(32)).multiply(BigDecimal.valueOf(0.00002));
        Rz = new BigDecimal(bitUtils.getSignedLong(32)).multiply(BigDecimal.valueOf(0.00002));
        dS = new BigDecimal(bitUtils.getSignedLong(25)).multiply(BigDecimal.valueOf(0.00001));
        Xp = new BigDecimal(bitUtils.getSignedLong(35)).multiply(BigDecimal.valueOf(0.001));
        Yp = new BigDecimal(bitUtils.getSignedLong(35)).multiply(BigDecimal.valueOf(0.001));
        Zp = new BigDecimal(bitUtils.getSignedLong(35)).multiply(BigDecimal.valueOf(0.001));
        As = new BigDecimal(bitUtils.getUnsignedLong(24)).multiply(BigDecimal.valueOf(0.001)).add(a_base);
        Bs = new BigDecimal(bitUtils.getUnsignedLong(25)).multiply(BigDecimal.valueOf(0.001)).add(b_base);
        At = new BigDecimal(bitUtils.getUnsignedLong(24)).multiply(BigDecimal.valueOf(0.001)).add(a_base);
        Bt = new BigDecimal(bitUtils.getUnsignedLong(25)).multiply(BigDecimal.valueOf(0.001)).add(b_base);
        HorizontalQuality = bitUtils.getUnsignedInt(3);
        VerticalQuality = bitUtils.getUnsignedInt(3);
    }

    public byte[] write() {
        BitUtils bitUtils = new BitUtils();
        bitUtils.setBitString("11010011000000"); //preamble + 6 reserved bit
        bitUtils.setInt(52 + SourceNameCounter + TargetNameCounter, 10);
        bitUtils.setInt(messageNumber, 12);
        bitUtils.setInt(SourceNameCounter, 5);
        bitUtils.setString(SourceName);
        bitUtils.setInt(TargetNameCounter, 5);
        bitUtils.setString(TargetName);
        bitUtils.setInt(SystemIdentificationNumber, 8);
        bitUtils.setInt(UtilizedTransformationMessageIndicator, 10);
        bitUtils.setInt(PlateNumber, 5);
        bitUtils.setInt(ComputationIndicator, 4);
        bitUtils.setInt(HeightIndicator, 2);
        bitUtils.setInt(B_valid / 2, 19);
        bitUtils.setInt(L_valid / 2, 20);
        bitUtils.setInt(dB_valid / 2, 14);
        bitUtils.setInt(dL_valid / 2, 14);
        bitUtils.setInt(dX.divide(BigDecimal.valueOf(0.0001), RoundingMode.HALF_EVEN).intValue(), 23);
        bitUtils.setInt(dY.divide(BigDecimal.valueOf(0.0001), RoundingMode.HALF_EVEN).intValue(), 23);
        bitUtils.setInt(dZ.divide(BigDecimal.valueOf(0.0001), RoundingMode.HALF_EVEN).intValue(), 23);
        bitUtils.setInt(Rx.divide(BigDecimal.valueOf(0.00002), RoundingMode.HALF_EVEN).intValue(), 32);
        bitUtils.setInt(Ry.divide(BigDecimal.valueOf(0.00002), RoundingMode.HALF_EVEN).intValue(), 32);
        bitUtils.setInt(Rz.divide(BigDecimal.valueOf(0.00002), RoundingMode.HALF_EVEN).intValue(), 32);
        bitUtils.setInt(dS.divide(BigDecimal.valueOf(0.00001), RoundingMode.HALF_EVEN).intValue(), 25);
        bitUtils.setInt(Xp.divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_EVEN).intValue(), 35);
        bitUtils.setInt(Yp.divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_EVEN).intValue(), 35);
        bitUtils.setInt(Zp.divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_EVEN).intValue(), 35);
        bitUtils.setInt(As.subtract(a_base).divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_EVEN).intValue(), 24);
        bitUtils.setInt(Bs.subtract(b_base).divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_EVEN).intValue(), 25);
        bitUtils.setInt(At.subtract(a_base).divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_EVEN).intValue(), 24);
        bitUtils.setInt(Bt.subtract(b_base).divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_EVEN).intValue(), 25);
        bitUtils.setInt(HorizontalQuality, 3);
        bitUtils.setInt(VerticalQuality, 3);
        byte[] bytes = bitUtils.getByteArray();
        return Bytes.concat(bytes, bitUtils.crc24q(bytes, bytes.length, 0));
    }

    public BigDecimal getXp() {
        return Xp;
    }

    public void setXp(BigDecimal xp) {
        Preconditions.checkArgument(xp.compareTo(new BigDecimal("17179869.184")) <= 0);
        Preconditions.checkArgument(xp.compareTo(new BigDecimal("-17179869.184")) >= 0);
        Xp = xp;
    }

    public BigDecimal getYp() {
        return Yp;
    }

    public void setYp(BigDecimal yp) {
        Preconditions.checkArgument(yp.compareTo(new BigDecimal("17179869.184")) <= 0);
        Preconditions.checkArgument(yp.compareTo(new BigDecimal("-17179869.184")) >= 0);
        Yp = yp;
    }

    public BigDecimal getZp() {
        return Zp;
    }

    public void setZp(BigDecimal zp) {
        Preconditions.checkArgument(zp.compareTo(new BigDecimal("17179869.184")) <= 0);
        Preconditions.checkArgument(zp.compareTo(new BigDecimal("-17179869.184")) >= 0);
        Zp = zp;
    }
}
