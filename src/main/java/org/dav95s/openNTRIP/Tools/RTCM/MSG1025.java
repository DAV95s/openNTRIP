package org.dav95s.openNTRIP.Tools.RTCM;

import com.google.common.primitives.Bytes;
import org.dav95s.openNTRIP.Tools.RTCMStream.BitUtils;

import java.math.RoundingMode;

import static com.google.common.base.Preconditions.*;

public class MSG1025 {

    private int messageNumber;
    private int SystemIdentificationNumber;
    private int ProjectionType;
    private double LaNO;
    private double LoNO;
    private double S;
    private double FalseEasting;
    private double FalseNorthing;

    public MSG1025() {
        messageNumber = 1025;
    }

    public MSG1025(byte[] msg) {
        BitUtils bitUtils = new BitUtils(msg);
        bitUtils.setPointer(24);
        messageNumber = bitUtils.getUnsignedInt(12);
        setSystemIdentificationNumber(bitUtils.getUnsignedInt(8));
        setProjectionType(bitUtils.getUnsignedInt(6));
        setLaNO(bitUtils.getSignedLong(34) * 0.000000011d);
        setLoNO(bitUtils.getSignedLong(35) * 0.000000011d);
        setS((bitUtils.getUnsignedLong(30) * 0.00001d + 993000) / 1000000);
        setFalseEasting(bitUtils.getUnsignedLong(36) * 0.001d);
        setFalseNorthing(bitUtils.getSignedLong(35) * 0.001d);
    }

    public byte[] write() {
        BitUtils bitUtils = new BitUtils();
        bitUtils.setBitString("11010011000000"); //preamble + 6 reserved bit
        bitUtils.setInt(25, 10);
        bitUtils.setInt(messageNumber, 12);
        bitUtils.setInt(SystemIdentificationNumber, 8);
        bitUtils.setInt(ProjectionType, 6);
        bitUtils.setLong(Math.round(LaNO / 0.000000011d), 34);
        bitUtils.setLong(Math.round(LoNO / 0.000000011d), 35);
        bitUtils.setLong(Math.round((S * 1000000 - 993000) / 0.00001d), 30);
        bitUtils.setLong(Math.round(FalseEasting / 0.001d), 36);
        bitUtils.setLong(Math.round(FalseNorthing / 0.001d), 35);
        byte[] bytes = bitUtils.getByteArray();
        return Bytes.concat(bytes, bitUtils.crc24q(bytes, bytes.length, 0));
    }

    @Override
    public String toString() {
        return "MSG1025{" +
                "messageNumber=" + messageNumber +
                ", SystemIdentificationNumber=" + SystemIdentificationNumber +
                ", ProjectionType=" + ProjectionType +
                ", LaNO=" + LaNO +
                ", LoNO=" + LoNO +
                ", S=" + S +
                ", FalseEasting=" + FalseEasting +
                ", FalseNorthing=" + FalseNorthing +
                '}';
    }

    public int getMessageNumber() {
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

    public double getLaNO() {
        return LaNO;
    }

    public void setLaNO(double laNO) {
        double normalized = BitUtils.normalize(laNO, 8);
        checkArgument(-90 <= normalized && normalized <= 90);
        LaNO = normalized;
    }

    public double getLoNO() {
        return LoNO;
    }

    public void setLoNO(double loNO) {
        double normalized = BitUtils.normalize(loNO, 8);
        checkArgument(-180 <= normalized && normalized <= 180);
        LoNO = normalized;
    }

    public double getS() {
        return S;
    }

    public void setS(double s) {
        double normalized = BitUtils.normalize(s, 6);
        checkArgument(0.993 <= normalized && normalized <= 1.003737418);
        this.S = normalized;
    }

    public double getFalseEasting() {
        return FalseEasting;
    }

    public void setFalseEasting(double falseEasting) {
        double normalized = BitUtils.normalize(falseEasting, 4);
        checkArgument(0 <= normalized && normalized <= 68719476.735);
        FalseEasting = normalized;
    }

    public double getFalseNorthing() {
        return FalseNorthing;
    }

    public void setFalseNorthing(double falseNorthing) {
        double normalized = BitUtils.normalize(falseNorthing, 4);
        checkArgument(-17179869.183 <= normalized && normalized <= 17179869.183);
        FalseNorthing = normalized;
    }
}
