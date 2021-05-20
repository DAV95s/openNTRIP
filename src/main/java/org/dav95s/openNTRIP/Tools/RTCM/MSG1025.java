package org.dav95s.openNTRIP.Tools.RTCM;

import com.google.common.primitives.Bytes;
import org.dav95s.openNTRIP.Tools.RTCMStream.BitUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.google.common.base.Preconditions.*;

public class MSG1025 {

    private int messageNumber;
    private int SystemIdentificationNumber;
    private int ProjectionType;
    private BigDecimal LaNO;
    private BigDecimal LoNO;
    private BigDecimal S;
    private BigDecimal FalseEasting;
    private BigDecimal FalseNorthing;

    private BigDecimal degRes = BigDecimal.valueOf(0.000000011d);

    public MSG1025() {
        messageNumber = 1025;
    }

    public MSG1025(byte[] msg) {
        BitUtils bitUtils = new BitUtils(msg);
        bitUtils.setPointer(24);
        messageNumber = bitUtils.getUnsignedInt(12);
        SystemIdentificationNumber = bitUtils.getUnsignedInt(8);
        ProjectionType = bitUtils.getUnsignedInt(6);
        LaNO = new BigDecimal(bitUtils.getSignedLong(34)).multiply(degRes);
        LoNO = new BigDecimal(bitUtils.getSignedLong(35)).multiply(degRes);
        S = new BigDecimal(bitUtils.getUnsignedLong(30)).multiply(BigDecimal.valueOf(0.00001d)).add(BigDecimal.valueOf(993000));
        FalseEasting = new BigDecimal(bitUtils.getUnsignedLong(36)).multiply(BigDecimal.valueOf(0.001d));
        FalseNorthing = new BigDecimal(bitUtils.getSignedLong(35)).multiply(BigDecimal.valueOf(0.001d));
    }

    public byte[] write() {
        BitUtils bitUtils = new BitUtils();
        bitUtils.setBitString("11010011000000"); //preamble + 6 reserved bit
        bitUtils.setInt(25, 10);
        bitUtils.setInt(messageNumber, 12);
        bitUtils.setInt(SystemIdentificationNumber, 8);
        bitUtils.setInt(ProjectionType, 6);
        bitUtils.setLong(LaNO.divide(degRes, RoundingMode.HALF_EVEN).longValue(), 34);
        bitUtils.setLong(LoNO.divide(degRes, RoundingMode.HALF_EVEN).longValue(), 35);
        bitUtils.setLong(S.subtract(BigDecimal.valueOf(993000)).divide(BigDecimal.valueOf(0.00001d), RoundingMode.HALF_EVEN).longValue(), 30);
        bitUtils.setLong(FalseEasting.divide(BigDecimal.valueOf(0.001d), RoundingMode.HALF_EVEN).longValue(), 36);
        bitUtils.setLong(FalseNorthing.divide(BigDecimal.valueOf(0.001d), RoundingMode.HALF_EVEN).longValue(), 35);
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

    public BigDecimal getLaNO() {
        return LaNO;
    }

    public void setLaNO(BigDecimal laNO) {
        checkArgument(laNO.compareTo(new BigDecimal(-90)) >= 0);
        checkArgument(laNO.compareTo(new BigDecimal(90)) <= 0);
        LaNO = laNO;
    }

    public BigDecimal getLoNO() {
        return LoNO;
    }

    public void setLoNO(BigDecimal loNO) {
        checkArgument(loNO.compareTo(new BigDecimal(-180)) >= 0);
        checkArgument(loNO.compareTo(new BigDecimal(180)) <= 0);
        LoNO = loNO;
    }

    public BigDecimal getS() {
        return S;
    }

    public void setS(BigDecimal s) {
        checkArgument(s.compareTo(BigDecimal.ZERO) >= 0);
        checkArgument(s.compareTo(new BigDecimal("10737.41823")) <= 0);
        this.S = s;
    }

    public BigDecimal getFalseEasting() {
        return FalseEasting;
    }

    public void setFalseEasting(BigDecimal falseEasting) {
        checkArgument(falseEasting.compareTo(BigDecimal.ZERO) >= 0);
        checkArgument(falseEasting.compareTo(new BigDecimal("68719476.735")) <= 0);
        FalseEasting = falseEasting;
    }

    public BigDecimal getFalseNorthing() {
        return FalseNorthing;
    }

    public void setFalseNorthing(BigDecimal falseNorthing) {
        checkArgument(falseNorthing.compareTo(new BigDecimal("-17179869.183")) >= 0);
        checkArgument(falseNorthing.compareTo(new BigDecimal("+17179869.183")) <= 0);
        FalseNorthing = falseNorthing;
    }
}
