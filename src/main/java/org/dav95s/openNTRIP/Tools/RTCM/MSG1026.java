package org.dav95s.openNTRIP.Tools.RTCM;

import com.google.common.primitives.Bytes;
import org.dav95s.openNTRIP.Tools.RTCMStream.BitUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.google.common.base.Preconditions.checkArgument;

public class MSG1026 {

    private int messageNumber;
    private int SystemIdentificationNumber;
    private int ProjectionType;
    private BigDecimal degRes = new BigDecimal("0.000000011");
    BigDecimal LaFO; // – Latitude of False Origin
    BigDecimal LoFO; // – Longitude of False Origin
    BigDecimal LaSP1; // – Latitude of Standard Parallel No. 1
    BigDecimal LaSP2; // – Latitude of Standard Parallel No. 2
    BigDecimal EFO; // – Easting of False Origin
    BigDecimal NFO; // – Northing of False Origin

    public MSG1026() {
        messageNumber = 1026;
    }

    public MSG1026(byte[] msg) {
        BitUtils bitUtils = new BitUtils(msg);
        bitUtils.pointerShift(24);
        messageNumber = bitUtils.getUnsignedInt(12);
        SystemIdentificationNumber = bitUtils.getUnsignedInt(8);
        ProjectionType = bitUtils.getUnsignedInt(6);
        LaFO = new BigDecimal(bitUtils.getSignedLong(34)).multiply(degRes);
        LoFO = new BigDecimal(bitUtils.getSignedLong(35)).multiply(degRes);
        LaSP1 = new BigDecimal(bitUtils.getSignedLong(34)).multiply(degRes);
        LaSP2 = new BigDecimal(bitUtils.getSignedLong(34)).multiply(degRes);
        EFO = new BigDecimal(bitUtils.getUnsignedLong(36)).multiply(BigDecimal.valueOf(0.001));
        NFO = new BigDecimal(bitUtils.getUnsignedLong(35)).multiply(BigDecimal.valueOf(0.001));
    }

    public byte[] write() {
        BitUtils bitUtils = new BitUtils();
        bitUtils.setBitString("11010011000000"); //preamble + 6 reserved bit
        bitUtils.setInt(30, 10);
        bitUtils.setInt(messageNumber, 12);
        bitUtils.setInt(SystemIdentificationNumber, 8);
        bitUtils.setInt(ProjectionType, 6);
        bitUtils.setLong(LaFO.divide(degRes, RoundingMode.HALF_EVEN).longValue(), 34);
        bitUtils.setLong(LoFO.divide(degRes, RoundingMode.HALF_EVEN).longValue(), 35);
        bitUtils.setLong(LaSP1.divide(degRes, RoundingMode.HALF_EVEN).longValue(), 34);
        bitUtils.setLong(LaSP2.divide(degRes, RoundingMode.HALF_EVEN).longValue(), 34);
        bitUtils.setLong(EFO.divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_EVEN).longValue(), 36);
        bitUtils.setLong(NFO.divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_EVEN).longValue(), 35);

        byte[] bytes = bitUtils.makeByteArray();
        return Bytes.concat(bytes, bitUtils.crc24q(bytes, bytes.length, 0));
    }

    public int getSystemIdentificationNumber() {
        return SystemIdentificationNumber;
    }

    public void setSystemIdentificationNumber(int systemIdentificationNumber) {
        checkArgument(0 <= systemIdentificationNumber && systemIdentificationNumber <= 255);
        SystemIdentificationNumber = systemIdentificationNumber;
    }

    public int getProjectionType() {
        return ProjectionType;
    }

    public void setProjectionType(int projectionType) {
        checkArgument(0 <= projectionType && projectionType <= 11);
        ProjectionType = projectionType;
    }

    public BigDecimal getLaFO() {

        return LaFO;
    }

    public void setLaFO(BigDecimal laFO) {
        checkArgument(laFO.compareTo(new BigDecimal(-90)) >= 0);
        checkArgument(laFO.compareTo(new BigDecimal(90)) <= 0);
        LaFO = laFO;
    }

    public BigDecimal getLoFO() {
        return LoFO;
    }

    public void setLoFO(BigDecimal loFO) {
        checkArgument(loFO.compareTo(new BigDecimal(-180)) >= 0);
        checkArgument(loFO.compareTo(new BigDecimal(180)) <= 0);
        LoFO = loFO;
    }

    public BigDecimal getLaSP1() {
        return LaSP1;
    }

    public void setLaSP1(BigDecimal laSP1) {
        checkArgument(laSP1.compareTo(new BigDecimal(-90)) >= 0);
        checkArgument(laSP1.compareTo(new BigDecimal(90)) <= 0);
        LaSP1 = laSP1;
    }

    public BigDecimal getLaSP2() {
        return LaSP2;
    }

    public void setLaSP2(BigDecimal laSP2) {
        checkArgument(laSP2.compareTo(new BigDecimal(-90)) >= 0);
        checkArgument(laSP2.compareTo(new BigDecimal(90)) <= 0);
        LaSP2 = laSP2;
    }

    public BigDecimal getEFO() {
        return EFO;
    }

    public void setEFO(BigDecimal EFO) {
        checkArgument(EFO.compareTo(BigDecimal.ZERO) >= 0);
        checkArgument(EFO.compareTo(new BigDecimal("68719476.735")) <= 0);
        this.EFO = EFO;
    }

    public BigDecimal getNFO() {
        return NFO;
    }

    public void setNFO(BigDecimal NFO) {
        checkArgument(NFO.compareTo(BigDecimal.ZERO) >= 0);
        checkArgument(NFO.compareTo(new BigDecimal("17179869.183")) <= 0);
        this.NFO = NFO;
    }

    @Override
    public String toString() {
        return "MSG1026{" +
                "messageNumber=" + messageNumber +
                ", SystemIdentificationNumber=" + SystemIdentificationNumber +
                ", ProjectionType=" + ProjectionType +
                ", degRes=" + degRes +
                ", LaFO=" + LaFO +
                ", LoFO=" + LoFO +
                ", LaSP1=" + LaSP1 +
                ", LaSP2=" + LaSP2 +
                ", EFO=" + EFO +
                ", NFO=" + NFO +
                '}';
    }
}
