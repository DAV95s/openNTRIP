package org.dav95s.openNTRIP.Tools.RTCM;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Bytes;
import org.dav95s.openNTRIP.Tools.RTCM.Assets.CRS1;
import org.dav95s.openNTRIP.Tools.RTCMStream.BitUtils;

public class MSG1022 extends MSG1021 implements CRS1 {

    double Xp;
    double Yp;
    double Zp;

    public MSG1022(byte[] msg) {
        BitUtils bitUtils = new BitUtils(msg);
        bitUtils.setPointer(24);

        messageNumber = bitUtils.getUnsignedInt(12);
        SourceNameCounter = bitUtils.getUnsignedInt(5);
        setSourceName(bitUtils.getString(SourceNameCounter * 8));
        TargetNameCounter = bitUtils.getUnsignedInt(5);
        setTargetName(bitUtils.getString(TargetNameCounter * 8));
        setSystemIdentificationNumber(bitUtils.getUnsignedInt(8));
        setUtilizedTransformationMessageIndicator(bitUtils.getUnsignedInt(10));
        setPlateNumber(bitUtils.getUnsignedInt(5));
        setComputationIndicator(bitUtils.getUnsignedInt(4));
        setHeightIndicator(bitUtils.getUnsignedInt(2));
        setLatValid(bitUtils.getSignedInt(19) * 2 / 3600d);
        setLonValid(bitUtils.getSignedInt(20) * 2 / 3600d);
        setdLatValid(bitUtils.getUnsignedInt(14) * 2 / 3600d);
        setdLonValid(bitUtils.getUnsignedInt(14) * 2 / 3600d);
        setdX(bitUtils.getSignedInt(23) * 0.0001);
        setdY(bitUtils.getSignedInt(23) * 0.0001);
        setdZ(bitUtils.getSignedInt(23) * 0.0001);
        setRx(bitUtils.getSignedInt(32) * 0.00002);
        setRy(bitUtils.getSignedInt(32) * 0.00002);
        setRz(bitUtils.getSignedInt(32) * 0.00002);
        setdS(bitUtils.getSignedInt(25) * 0.00001);
        setXp(bitUtils.getSignedLong(35) * 0.001);
        setYp(bitUtils.getSignedLong(35) * 0.001);
        setZp(bitUtils.getSignedLong(35) * 0.001);
        setAs(bitUtils.getUnsignedLong(24) * 0.001 + 6370000);
        setBs(bitUtils.getUnsignedLong(25) * 0.001 + 6350000);
        setAt(bitUtils.getUnsignedLong(24) * 0.001 + 6370000);
        setBt(bitUtils.getUnsignedLong(25) * 0.001 + 6350000);
        setHorizontalQuality(bitUtils.getUnsignedInt(3));
        setVerticalQuality(bitUtils.getUnsignedInt(3));
    }

    public byte[] write() {
        BitUtils bitUtils = new BitUtils();
        bitUtils.setBitString("11010011000000"); //preamble + 6 reserved bit
        bitUtils.setInt(65 + SourceNameCounter + TargetNameCounter, 10);
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
        bitUtils.setInt((int) Math.round(LatValid * 3600 / 2), 19);
        bitUtils.setInt((int) Math.round(LonValid * 3600 / 2), 20);
        bitUtils.setInt((int) Math.round(dLatValid * 3600 / 2), 14);
        bitUtils.setInt((int) Math.round(dLonValid * 3600 / 2), 14);
        bitUtils.setInt((int) Math.round(dX * 10000), 23);
        bitUtils.setInt((int) Math.round(dY * 10000), 23);
        bitUtils.setInt((int) Math.round(dZ * 10000), 23);
        bitUtils.setInt((int) Math.round(Rx / 0.00002), 32);
        bitUtils.setInt((int) Math.round(Ry / 0.00002), 32);
        bitUtils.setInt((int) Math.round(Rz / 0.00002), 32);
        bitUtils.setInt((int) Math.round(dS * 100000), 25);
        bitUtils.setInt((int) Math.round(Xp * 1000), 35);
        bitUtils.setInt((int) Math.round(Yp * 1000), 35);
        bitUtils.setInt((int) Math.round(Zp * 1000), 35);
        bitUtils.setInt((int) Math.round((As - 6370000) * 1000), 24);
        bitUtils.setInt((int) Math.round((Bs - 6350000) * 1000), 25);
        bitUtils.setInt((int) Math.round((At - 6370000) * 1000), 24);
        bitUtils.setInt((int) Math.round((Bt - 6350000) * 1000), 25);
        bitUtils.setInt(HorizontalQuality, 3);
        bitUtils.setInt(VerticalQuality, 3);
        byte[] bytes = bitUtils.getByteArray();
        return Bytes.concat(bytes, bitUtils.crc24q(bytes, bytes.length, 0));
    }

    public double getXp() {
        return Xp;
    }

    public void setXp(double xp) {
        double normalized = BitUtils.normalize(xp, 4);
        Preconditions.checkArgument(-17179869.184 <= normalized && normalized <= 17179869.184);
        Xp = normalized;
    }

    public double getYp() {
        return Yp;
    }

    public void setYp(double yp) {
        double normalized = BitUtils.normalize(yp, 4);
        Preconditions.checkArgument(-17179869.184 <= normalized && normalized <= 17179869.184);
        Yp = normalized;
    }

    public double getZp() {
        return Zp;
    }

    public void setZp(double zp) {
        double normalized = BitUtils.normalize(zp, 4);
        Preconditions.checkArgument(-17179869.184 <= normalized && normalized <= 17179869.184);
        Zp = normalized;
    }
}
