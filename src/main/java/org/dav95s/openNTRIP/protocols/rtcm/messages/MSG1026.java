package org.dav95s.openNTRIP.protocols.rtcm.messages;

import com.google.common.primitives.Bytes;
import org.dav95s.openNTRIP.protocols.rtcm.assets.CRS3;
import org.dav95s.openNTRIP.utils.binaryParse.BitUtils;
import org.dav95s.openNTRIP.utils.binaryParse.Crc24q;
import org.dav95s.openNTRIP.utils.binaryParse.Normalize;

import static com.google.common.base.Preconditions.checkArgument;

public class MSG1026 implements CRS3 {

    private final int messageNumber;
    private int SystemIdentificationNumber;
    private int ProjectionType;
    double LaFO; // – Latitude of False Origin
    double LoFO; // – Longitude of False Origin
    double LaSP1; // – Latitude of Standard Parallel No. 1
    double LaSP2; // – Latitude of Standard Parallel No. 2
    double EFO; // – Easting of False Origin
    double NFO; // – Northing of False Origin

    public MSG1026() {
        messageNumber = 1026;
    }

    public MSG1026(byte[] msg) {
        BitUtils bitUtils = new BitUtils(msg);
        bitUtils.setPointer(24);
        messageNumber = bitUtils.getUnsignedInt(12);
        SystemIdentificationNumber = bitUtils.getUnsignedInt(8);
        ProjectionType = bitUtils.getUnsignedInt(6);
        setLaFO(bitUtils.getSignedLong(34) * 0.000000011);
        setLoFO(bitUtils.getSignedLong(35) * 0.000000011);
        setLaSP1(bitUtils.getSignedLong(34) * 0.000000011);
        setLaSP2(bitUtils.getSignedLong(34) * 0.000000011);
        setEFO(bitUtils.getUnsignedLong(36) * 0.001);
        setNFO(bitUtils.getSignedLong(35) * 0.001);
    }

    public byte[] getBytes() {
        BitUtils bitUtils = new BitUtils();
        bitUtils.setBitString("11010011000000"); //preamble + 6 reserved bit
        bitUtils.setInt(30, 10);//todo length message !!!!!!
        bitUtils.setInt(messageNumber, 12);
        bitUtils.setInt(SystemIdentificationNumber, 8);
        bitUtils.setInt(ProjectionType, 6);
        bitUtils.setLong(Math.round(LaFO / 0.000000011), 34);
        bitUtils.setLong(Math.round(LoFO / 0.000000011), 35);
        bitUtils.setLong(Math.round(LaSP1 / 0.000000011), 34);
        bitUtils.setLong(Math.round(LaSP2 / 0.000000011), 34);
        bitUtils.setLong(Math.round(EFO / 0.001), 36);
        bitUtils.setLong(Math.round(NFO / 0.001), 35);

        byte[] bytes = bitUtils.getByteArray();
        return Bytes.concat(bytes, Crc24q.crc24q(bytes, bytes.length, 0));
    }

    public int getMessageNumber(){
        return messageNumber;
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

    public double getLaFO() {

        return LaFO;
    }

    public void setLaFO(double laFO) {
        double normalized = Normalize.normalize(laFO, 8);
        checkArgument(-90 <= normalized && normalized <= 90);
        LaFO = normalized;
    }

    public double getLoFO() {
        return LoFO;
    }

    public void setLoFO(double loFO) {
        double normalized = Normalize.normalize(loFO, 8);
        checkArgument(-180 <= normalized && normalized <= 180);
        LoFO = normalized;
    }

    public double getLaSP1() {
        return LaSP1;
    }

    public void setLaSP1(double laSP1) {
        double normalized = Normalize.normalize(laSP1, 8);
        checkArgument(-90 <= normalized && normalized <= 90);
        LaSP1 = normalized;

    }

    public double getLaSP2() {
        return LaSP2;
    }

    public void setLaSP2(double laSP2) {
        double normalized = Normalize.normalize(laSP2, 8);
        checkArgument(-90 <= normalized && normalized <= 90);
        LaSP2 = normalized;

    }

    public double getEFO() {
        return EFO;
    }

    public void setEFO(double EFO) {
        double normalized = Normalize.normalize(EFO, 4);
        checkArgument(0 <= normalized && normalized <= 68719476.735);
        this.EFO = normalized;
    }

    public double getNFO() {
        return NFO;
    }

    public void setNFO(double NFO) {
        double normalized = Normalize.normalize(NFO, 4);
        checkArgument(-17179869.183 <= normalized && normalized <= 17179869.183);
        this.NFO = normalized;
    }

    @Override
    public String toString() {
        return "MSG1026{" +
                "messageNumber=" + messageNumber +
                ", SystemIdentificationNumber=" + SystemIdentificationNumber +
                ", ProjectionType=" + ProjectionType +
                ", LaFO=" + LaFO +
                ", LoFO=" + LoFO +
                ", LaSP1=" + LaSP1 +
                ", LaSP2=" + LaSP2 +
                ", EFO=" + EFO +
                ", NFO=" + NFO +
                '}';
    }
}
