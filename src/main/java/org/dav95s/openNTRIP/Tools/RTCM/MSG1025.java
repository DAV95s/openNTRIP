package org.dav95s.openNTRIP.Tools.RTCM;

import com.google.common.primitives.Bytes;
import org.dav95s.openNTRIP.Tools.BitUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MSG1025 {

    private int messageNumber;
    private int SystemIdentificationNumber;
    private int ProjectionType;
    private BigDecimal LaNO;
    private BigDecimal LoNO;
    private BigDecimal addSNO;
    private BigDecimal FalseEasting;
    private BigDecimal FalseNorthing;

    private BigDecimal degRes = BigDecimal.valueOf(0.000000011d);

    public MSG1025(byte[] msg) {
        BitUtils bitUtils = new BitUtils(msg);
        bitUtils.setShiftPointer(24);
        messageNumber = bitUtils.getUnsignedInt(12);
        SystemIdentificationNumber = bitUtils.getUnsignedInt(8);
        ProjectionType = bitUtils.getUnsignedInt(6);
        LaNO = new BigDecimal(bitUtils.getSignedLong(34)).multiply(degRes);
        LoNO = new BigDecimal(bitUtils.getSignedLong(35)).multiply(degRes);
        addSNO = new BigDecimal(bitUtils.getUnsignedLong(30)).multiply(BigDecimal.valueOf(0.00001d)).add(BigDecimal.valueOf(993000));
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
        bitUtils.setLong(addSNO.subtract(BigDecimal.valueOf(993000)).divide(BigDecimal.valueOf(0.00001d), RoundingMode.HALF_EVEN).longValue(), 30);
        bitUtils.setLong(FalseEasting.divide(BigDecimal.valueOf(0.001d), RoundingMode.HALF_EVEN).longValue(), 36);
        bitUtils.setLong(FalseNorthing.divide(BigDecimal.valueOf(0.001d), RoundingMode.HALF_EVEN).longValue(), 35);
        byte[] bytes = bitUtils.makeByteArr();
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
                ", addSNO=" + addSNO +
                ", FalseEasting=" + FalseEasting +
                ", FalseNorthing=" + FalseNorthing +
                '}';
    }
}
