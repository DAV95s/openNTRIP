package org.dav95s.openNTRIP.Tools.RTCM;

import com.google.common.primitives.Bytes;
import org.dav95s.openNTRIP.Tools.RTCMStream.BitUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class MSG1023 {
    private int messageNumber;
    private int SystemIdentificationNumber;
    private int HorizontalShiftIndicator;
    private int VerticalShiftIndicator;
    private BigDecimal F0;
    private BigDecimal L0;
    private BigDecimal dF;
    private BigDecimal dA;
    private BigDecimal MdF;
    private BigDecimal MdA;
    private BigDecimal MdH;
    Grid[] gridMap = new Grid[16];
    private int HorizontalInterpolationMethodIndicator;
    private int VerticalInterpolationMethodIndicator;
    private int HorizontalGridQualityIndicator;
    private int VerticalGridQualityIndicator;
    private int ModifiedJulianDayNumber;

    BigDecimal gridResolution = BigDecimal.valueOf(0.00003d);

    public MSG1023(byte[] msg) {
        BitUtils bitUtils = new BitUtils(msg);
        bitUtils.setPointer(24);
        messageNumber = bitUtils.getUnsignedInt(12);
        SystemIdentificationNumber = bitUtils.getUnsignedInt(8);
        HorizontalShiftIndicator = bitUtils.getUnsignedInt(1);
        VerticalShiftIndicator = bitUtils.getUnsignedInt(1);
        F0 = BigDecimal.valueOf(bitUtils.getSignedLong(21)).multiply(BigDecimal.valueOf(0.5d));
        L0 = BigDecimal.valueOf(bitUtils.getSignedInt(22)).multiply(BigDecimal.valueOf(0.5d));
        dF = BigDecimal.valueOf(bitUtils.getUnsignedInt(12)).multiply(BigDecimal.valueOf(0.5d));
        dA = BigDecimal.valueOf(bitUtils.getUnsignedInt(12)).multiply(BigDecimal.valueOf(0.5d));
        MdF = BigDecimal.valueOf(bitUtils.getSignedInt(8)).multiply(BigDecimal.valueOf(0.001d));
        MdA = BigDecimal.valueOf(bitUtils.getSignedInt(8)).multiply(BigDecimal.valueOf(0.001d));
        MdH = BigDecimal.valueOf(bitUtils.getSignedInt(15)).multiply(BigDecimal.valueOf(0.01d));

        for (int i = 0; i < gridMap.length; i++) {
            gridMap[i] = new Grid();
            gridMap[i].dFi = BigDecimal.valueOf(bitUtils.getSignedInt(9)).multiply(gridResolution);
            gridMap[i].dAi = BigDecimal.valueOf(bitUtils.getSignedInt(9)).multiply(gridResolution);
            gridMap[i].dHi = BigDecimal.valueOf(bitUtils.getSignedInt(9)).multiply(gridResolution);
        }

        HorizontalInterpolationMethodIndicator = bitUtils.getUnsignedInt(2);
        VerticalInterpolationMethodIndicator = bitUtils.getUnsignedInt(2);
        HorizontalGridQualityIndicator = bitUtils.getUnsignedInt(3);
        VerticalGridQualityIndicator = bitUtils.getUnsignedInt(3);
        ModifiedJulianDayNumber = bitUtils.getUnsignedInt(16);
    }

    public byte[] write() {
        BitUtils bitUtils = new BitUtils();
        bitUtils.setBitString("11010011000000"); //preamble + 6 reserved bit
        bitUtils.setInt(73, 10);
        bitUtils.setInt(messageNumber, 12);
        bitUtils.setInt(SystemIdentificationNumber, 8);
        bitUtils.setInt(HorizontalShiftIndicator, 1);
        bitUtils.setInt(VerticalShiftIndicator, 1);
        bitUtils.setInt( (F0.divide(BigDecimal.valueOf(0.5d), RoundingMode.HALF_EVEN).intValue()), 21);
        bitUtils.setInt((L0.divide(BigDecimal.valueOf(0.5d), RoundingMode.HALF_EVEN).intValue()), 22);
        bitUtils.setInt((dF.divide(BigDecimal.valueOf(0.5d), RoundingMode.HALF_EVEN).intValue()), 12);
        bitUtils.setInt( (dA.divide(BigDecimal.valueOf(0.5d), RoundingMode.HALF_EVEN).intValue()), 12);
        bitUtils.setInt( (MdF.divide(BigDecimal.valueOf(0.001d), RoundingMode.HALF_EVEN).intValue()), 8);
        bitUtils.setInt((MdA.divide(BigDecimal.valueOf(0.001d), RoundingMode.HALF_EVEN).intValue()), 8);
        bitUtils.setInt( (MdH.divide(BigDecimal.valueOf(0.01d), RoundingMode.HALF_EVEN).intValue()), 15);

        for (Grid grid : gridMap) {
            bitUtils.setInt(grid.dFi.divide(gridResolution, RoundingMode.HALF_EVEN).intValue(), 9);
            bitUtils.setInt(grid.dAi.divide(gridResolution, RoundingMode.HALF_EVEN).intValue(), 9);
            bitUtils.setInt(grid.dHi.divide(gridResolution, RoundingMode.HALF_EVEN).intValue(), 9);
        }

        bitUtils.setInt(HorizontalInterpolationMethodIndicator, 2);
        bitUtils.setInt(VerticalInterpolationMethodIndicator, 2);
        bitUtils.setInt(HorizontalGridQualityIndicator, 3);
        bitUtils.setInt(VerticalGridQualityIndicator, 3);
        bitUtils.setInt(ModifiedJulianDayNumber, 16);
        byte[] bytes = bitUtils.getByteArray();
        return Bytes.concat(bytes, bitUtils.crc24q(bytes, bytes.length, 0));
    }

    @Override
    public String toString() {
        return "MSG1023{" +
                "messageNumber=" + messageNumber +
                ", SystemIdentificationNumber=" + SystemIdentificationNumber +
                ", HorizontalShiftIndicator=" + HorizontalShiftIndicator +
                ", VerticalShiftIndicator=" + VerticalShiftIndicator +
                ", F0=" + F0.divide(new BigDecimal(3600), RoundingMode.HALF_UP) +
                ", L0=" + L0 .divide(new BigDecimal(3600), RoundingMode.HALF_UP)+
                ", dF=" + dF +
                ", dA=" + dA +
                ", MdF=" + MdF +
                ", MdA=" + MdA +
                ", MdH=" + MdH +
                ", gridMap=" + Arrays.toString(gridMap) +
                ", HorizontalInterpolationMethodIndicator=" + HorizontalInterpolationMethodIndicator +
                ", VerticalInterpolationMethodIndicator=" + VerticalInterpolationMethodIndicator +
                ", HorizontalGridQualityIndicator=" + HorizontalGridQualityIndicator +
                ", VerticalGridQualityIndicator=" + VerticalGridQualityIndicator +
                ", ModifiedJulianDayNumber=" + ModifiedJulianDayNumber +
                '}';
    }
}

class Grid {
    public BigDecimal dFi;
    public BigDecimal dAi;
    public BigDecimal dHi;

    @Override
    public String toString() {
        return "Grid{" +
                "dFi=" + dFi +
                ", dAi=" + dAi +
                ", dHi=" + dHi +
                '}';
    }
}


