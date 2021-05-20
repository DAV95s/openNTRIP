package org.dav95s.openNTRIP.Tools.RTCM;

import com.google.common.primitives.Bytes;
import org.dav95s.openNTRIP.Tools.RTCMStream.BitUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.google.common.base.Preconditions.checkArgument;

public class MSG1027 {

    private int messageNumber;
    private int SystemIdentificationNumber;
    private int ProjectionType;
    private boolean RectificationFlag = false;
    private final BigDecimal degRes = new BigDecimal("0.000000011");
    private BigDecimal LaPC;
    private BigDecimal LoPC;
    private BigDecimal AzIL;
    private BigDecimal RectifiedToSkew;
    private BigDecimal SILppm;
    private BigDecimal EPC;
    private BigDecimal NPC;

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
        setLaPC(new BigDecimal(bitUtils.getSignedLong(34)).multiply(degRes));
        setLoPC(new BigDecimal(bitUtils.getSignedLong(35)).multiply(degRes));
        setAzIL(new BigDecimal(bitUtils.getUnsignedLong(35)).multiply(degRes));
        if (RectificationFlag) {
            setRectifiedToSkew(new BigDecimal(bitUtils.getSignedInt(26)).multiply(degRes).add(AzIL));
        } else {
            setRectifiedToSkew(new BigDecimal(bitUtils.getSignedInt(26)));
        }
        setSILppm(new BigDecimal(bitUtils.getUnsignedLong(30)).multiply(BigDecimal.valueOf(0.00001d)).add(BigDecimal.valueOf(993000)));
        setEPC(new BigDecimal(bitUtils.getUnsignedLong(36))
                .multiply(BigDecimal.valueOf(0.001)));
        setNPC(new BigDecimal(bitUtils.getSignedLong(35))
                .multiply(BigDecimal.valueOf(0.001)));

    }

    public byte[] write() {
        BitUtils bitUtils = new BitUtils();
        bitUtils.setBitString("11010011000000"); //preamble + 6 reserved bit
        bitUtils.setInt(30, 10);//todo length message !!!!!!
        bitUtils.setInt(messageNumber, 12);
        bitUtils.setInt(SystemIdentificationNumber, 8);
        bitUtils.setInt(ProjectionType, 6);
        bitUtils.setBoolean(RectificationFlag);

        bitUtils.setLong(LaPC.divide(degRes, RoundingMode.HALF_UP).longValue(), 34);
        bitUtils.setLong(LoPC.divide(degRes, RoundingMode.HALF_UP).longValue(), 35);
        bitUtils.setLong(AzIL.divide(degRes, RoundingMode.HALF_UP).longValue(), 35);
        if (RectificationFlag) {
            bitUtils.setLong(RectifiedToSkew.subtract(AzIL)//RectifiedToSkew
                    .divide(degRes, RoundingMode.HALF_UP).longValue(), 26);
        } else {
            bitUtils.setLong(BigDecimal.ZERO.longValue(), 26);
        }
        bitUtils.setLong(SILppm.subtract(BigDecimal.valueOf(993000)).divide(BigDecimal.valueOf(0.00001d), RoundingMode.HALF_EVEN).longValue(), 30);
        bitUtils.setLong(EPC.divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_UP).longValue(), 36);
        bitUtils.setLong(NPC.divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_UP).longValue(), 35);

        byte[] bytes = bitUtils.getByteArray();
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

    public BigDecimal getLaPC() {
        return LaPC;
    }

    public void setLaPC(BigDecimal laPC) {
        checkArgument(laPC.compareTo(new BigDecimal(-90)) >= 0);
        checkArgument(laPC.compareTo(new BigDecimal(90)) <= 0);
        LaPC = laPC.setScale(9, RoundingMode.HALF_UP);
    }

    public BigDecimal getLoPC() {
        return LoPC;
    }

    public void setLoPC(BigDecimal loPC) {
        checkArgument(loPC.compareTo(new BigDecimal(-180)) >= 0);
        checkArgument(loPC.compareTo(new BigDecimal(180)) <= 0);
        LoPC = loPC.setScale(9, RoundingMode.HALF_UP);
    }

    public BigDecimal getAzIL() {
        return AzIL;
    }

    public void setAzIL(BigDecimal azIL) {
        checkArgument(azIL.compareTo(new BigDecimal(0)) >= 0);
        checkArgument(azIL.compareTo(new BigDecimal(360)) <= 0);
        AzIL = azIL.setScale(9, RoundingMode.HALF_UP);
    }

    public BigDecimal getRectifiedToSkew() {
        return RectifiedToSkew;
    }

    public void setRectifiedToSkew(BigDecimal rectifiedToSkew) {
        checkArgument(rectifiedToSkew.subtract(AzIL).compareTo(new BigDecimal("0.369098741")) <= 0);
        checkArgument(rectifiedToSkew.subtract(AzIL).compareTo(new BigDecimal("-0.369098741")) >= 0);
        RectificationFlag = true;
        RectifiedToSkew = rectifiedToSkew.setScale(9, RoundingMode.HALF_UP);
    }

    public BigDecimal getSILppm() {
        return SILppm;
    }

    public void setSILppm(BigDecimal SILppm) {
        checkArgument(SILppm.compareTo(new BigDecimal(0)) >= 0);
        checkArgument(SILppm.compareTo(new BigDecimal("1003737.418")) <= 0);
        this.SILppm = SILppm.setScale(3, RoundingMode.HALF_UP);
    }

    public BigDecimal getEPC() {
        return EPC;
    }

    public void setEPC(BigDecimal EPC) {
        checkArgument(EPC.compareTo(new BigDecimal(0)) >= 0);
        checkArgument(EPC.compareTo(new BigDecimal("68719476.735")) <= 0);
        this.EPC = EPC;
    }

    public BigDecimal getNPC() {
        return NPC;
    }

    public void setNPC(BigDecimal NPC) {
        checkArgument(NPC.compareTo(new BigDecimal("-17179869.183")) >= 0);
        checkArgument(NPC.compareTo(new BigDecimal("17179869.183")) <= 0);
        this.NPC = NPC;
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
