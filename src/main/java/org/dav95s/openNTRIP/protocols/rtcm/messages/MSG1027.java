package org.dav95s.openNTRIP.protocols.rtcm.messages;

import com.google.common.primitives.Bytes;
import org.dav95s.openNTRIP.protocols.rtcm.assets.CRS3;
import org.dav95s.openNTRIP.utils.binaryParse.BitUtils;
import org.dav95s.openNTRIP.utils.binaryParse.Crc24q;
import org.dav95s.openNTRIP.utils.binaryParse.Normalize;

import static com.google.common.base.Preconditions.checkArgument;

public class MSG1027 implements CRS3 {

    private int messageNumber;
    private int SystemIdentificationNumber;
    private int ProjectionType;
    private boolean RectificationFlag = false;
    private double LaPC;
    private double LoPC;
    private double AzIL;
    private double RectifiedToSkew;
    private double SILppm;
    private double EPC;
    private double NPC;

    public MSG1027() {
        messageNumber = 1027;
    }

    public MSG1027(byte[] msg) {
        BitUtils bitUtils = new BitUtils(msg);
        bitUtils.setPointer(24);
        messageNumber = bitUtils.getUnsignedInt(12);
        SystemIdentificationNumber = bitUtils.getUnsignedInt(8);
        ProjectionType = bitUtils.getUnsignedInt(6);

        RectificationFlag = (bitUtils.getBoolean());
        setLaPC(bitUtils.getSignedLong(34) * 0.000000011);
        setLoPC(bitUtils.getSignedLong(35) * 0.000000011);
        setAzIL(bitUtils.getUnsignedLong(35) * 0.000000011);
        if (RectificationFlag) {
            setRectifiedToSkew(bitUtils.getSignedInt(26) * 0.000000011 + AzIL);
        } else {
            setRectifiedToSkew(bitUtils.getSignedInt(26));
        }
        setSILppm((bitUtils.getUnsignedLong(30) * 0.00001d + 993000) / 1000000);
        setEPC(bitUtils.getUnsignedLong(36) * 0.001);
        setNPC(bitUtils.getSignedLong(35) * 0.001);
    }

    public byte[] getBytes() {
        BitUtils bitUtils = new BitUtils();
        bitUtils.setBitString("11010011000000"); //preamble + 6 reserved bit
        bitUtils.setInt(33, 10);
        bitUtils.setInt(messageNumber, 12);
        bitUtils.setInt(SystemIdentificationNumber, 8);
        bitUtils.setInt(ProjectionType, 6);
        bitUtils.setBoolean(RectificationFlag);
        bitUtils.setLong(Math.round(LaPC / 0.000000011), 34);
        bitUtils.setLong(Math.round(LoPC / 0.000000011), 35);
        bitUtils.setLong(Math.round(AzIL / 0.000000011), 35);
        if (RectificationFlag) {
            bitUtils.setLong(Math.round((RectifiedToSkew - AzIL) / 0.000000011), 26);
        } else {
            bitUtils.setLong(0, 26);
        }
        bitUtils.setLong(Math.round((SILppm * 1000000 - 993000) / 0.00001d), 30);
        bitUtils.setLong(Math.round(EPC / 0.001), 36);
        bitUtils.setLong(Math.round(NPC / 0.001), 35);

        byte[] bytes = bitUtils.getByteArray();
        return Bytes.concat(bytes, Crc24q.crc24q(bytes, bytes.length, 0));
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

    public double getLaPC() {
        return LaPC;
    }

    public void setLaPC(double laPC) {
        double normalized = Normalize.normalize(laPC, 8);
        checkArgument(-90 <= normalized && normalized <= 90);
        LaPC = normalized;
    }

    public double getLoPC() {
        return LoPC;
    }

    public void setLoPC(double loPC) {
        double normalized = Normalize.normalize(loPC, 8);
        checkArgument(-180 <= normalized && normalized <= 180);
        LoPC = normalized;
    }

    public double getAzIL() {
        return AzIL;
    }

    public void setAzIL(double azIL) {
        double normalized = Normalize.normalize(azIL, 8);
        checkArgument(0 <= normalized && normalized <= 360);
        AzIL = normalized;
    }

    public double getRectifiedToSkew() {
        return RectifiedToSkew;
    }

    public void setRectifiedToSkew(double rectifiedToSkew) {
        double normalized = Normalize.normalize(rectifiedToSkew, 8);
        checkArgument(-0.369098741 <= normalized - AzIL && normalized - AzIL <= 0.369098741);
        RectificationFlag = true;
        RectifiedToSkew = normalized;
    }

    public double getSILppm() {
        return SILppm;
    }

    public void setSILppm(double SILppm) {
        double normalized = Normalize.normalize(SILppm, 9);
        checkArgument(0.993 <= normalized && normalized <= 1.003737418);
        this.SILppm = normalized;
    }

    public double getEPC() {
        return EPC;
    }

    public void setEPC(double EPC) {
        double normalized = Normalize.normalize(EPC, 3);
        checkArgument(0 <= normalized && normalized <= 68719476.735);
        this.EPC = normalized;
    }

    public double getNPC() {
        return NPC;
    }

    public void setNPC(double NPC) {
        double normalized = Normalize.normalize(NPC, 3);
        checkArgument(-17179869.183 <= normalized && normalized <= 17179869.183);
        this.NPC = normalized;
    }

    @Override
    public String toString() {
        return "MSG1027{" +
                "messageNumber=" + messageNumber +
                ", SystemIdentificationNumber=" + SystemIdentificationNumber +
                ", ProjectionType=" + ProjectionType +
                ", RectificationFlag=" + RectificationFlag +
                ", LaPC=" + LaPC +
                ", LoPC=" + LoPC +
                ", AzIL=" + AzIL +
                ", RectifiedToSkew=" + RectifiedToSkew +
                ", SIL=" + SILppm +
                ", EPC=" + EPC +
                ", NPC=" + NPC +
                '}';
    }
}
