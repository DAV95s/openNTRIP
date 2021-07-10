package org.dav95s.openNTRIP.Tools.RTCM;

import com.google.common.primitives.Bytes;
import org.dav95s.openNTRIP.Tools.RTCM.Assets.CRS2;
import org.dav95s.openNTRIP.Tools.RTCMStream.BitUtils;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;

public class MSG1023 implements CRS2 {
    private int messageNumber = 1023;
    private int SystemIdentificationNumber;
    private boolean HorizontalShiftIndicator;
    private boolean VerticalShiftIndicator;
    private double Lat0;
    private double Lon0;
    private double dLat0;
    private double dLon0;
    private double MdLat;
    private double MdLon;
    private double MdH;
    Grid[] gridMap = new Grid[16];
    private int HorizontalInterpolationMethodIndicator;
    private int VerticalInterpolationMethodIndicator;
    private int HorizontalGridQualityIndicator;
    private int VerticalGridQualityIndicator;
    private int ModifiedJulianDayNumber;

    public MSG1023(byte[] msg) {
        BitUtils bitUtils = new BitUtils(msg);
        bitUtils.setPointer(24);
        messageNumber = bitUtils.getUnsignedInt(12);
        setSystemIdentificationNumber(bitUtils.getUnsignedInt(8));
        setHorizontalShiftIndicator(bitUtils.getBoolean());
        setVerticalShiftIndicator(bitUtils.getBoolean());
        setLat0(bitUtils.getSignedLong(21) * 0.5d / 3600);
        setLon0(bitUtils.getSignedInt(22) * 0.5d / 3600);
        setdLat0(bitUtils.getUnsignedInt(12) * 0.5d / 3600);
        setdLon0(bitUtils.getUnsignedInt(12) * 0.5d / 3600);
        setMdLat(bitUtils.getSignedInt(8) * 0.001d);
        setMdLon(bitUtils.getSignedInt(8) * 0.001d);
        setMdH(bitUtils.getSignedInt(15) * 0.01d);

        for (int i = 0; i < gridMap.length; i++) {
            gridMap[i] = new Grid();
            gridMap[i].dLat = BitUtils.normalize(bitUtils.getSignedInt(9) * 0.00003d, 8);
            gridMap[i].dLon = BitUtils.normalize(bitUtils.getSignedInt(9) * 0.00003d, 8);
            gridMap[i].dH = BitUtils.normalize(bitUtils.getSignedInt(9) * 0.00003d, 8);
        }

        setHorizontalInterpolationMethodIndicator(bitUtils.getUnsignedInt(2));
        setVerticalInterpolationMethodIndicator(bitUtils.getUnsignedInt(2));
        setHorizontalGridQualityIndicator(bitUtils.getUnsignedInt(3));
        setVerticalGridQualityIndicator(bitUtils.getUnsignedInt(3));
        setModifiedJulianDayNumber(bitUtils.getUnsignedInt(16));
    }

    public byte[] write() {
        BitUtils bitUtils = new BitUtils();
        bitUtils.setBitString("11010011000000"); //preamble + 6 reserved bit
        bitUtils.setInt(73, 10);
        bitUtils.setInt(messageNumber, 12);
        bitUtils.setInt(SystemIdentificationNumber, 8);
        bitUtils.setBoolean(HorizontalShiftIndicator);
        bitUtils.setBoolean(VerticalShiftIndicator);
        bitUtils.setInt((int) Math.round(Lat0 / 0.5d * 3600), 21);
        bitUtils.setInt((int) Math.round(Lon0 / 0.5d * 3600), 22);
        bitUtils.setInt((int) Math.round(dLat0 / 0.5d * 3600), 12);
        bitUtils.setInt((int) Math.round(dLon0 / 0.5d * 3600), 12);
        bitUtils.setInt((int) Math.round(MdLat / 0.001d), 8);
        bitUtils.setInt((int) Math.round(MdLon / 0.001d), 8);
        bitUtils.setInt((int) Math.round(MdH / 0.01d), 15);

        for (Grid grid : gridMap) {
            bitUtils.setInt((int) Math.round(grid.dLat / 0.00003), 9);
            bitUtils.setInt((int) Math.round(grid.dLon / 0.00003), 9);
            bitUtils.setInt((int) Math.round(grid.dH / 0.00003d), 9);
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
                ", Lat0=" + Lat0 +
                ", Lon0=" + Lon0 +
                ", dLat0=" + dLat0 +
                ", dLon0=" + dLon0 +
                ", MdLat=" + MdLat +
                ", MdLon=" + MdLon +
                ", MdH=" + MdH +
                ", gridMap=" + Arrays.toString(gridMap) +
                ", HorizontalInterpolationMethodIndicator=" + HorizontalInterpolationMethodIndicator +
                ", VerticalInterpolationMethodIndicator=" + VerticalInterpolationMethodIndicator +
                ", HorizontalGridQualityIndicator=" + HorizontalGridQualityIndicator +
                ", VerticalGridQualityIndicator=" + VerticalGridQualityIndicator +
                ", ModifiedJulianDayNumber=" + ModifiedJulianDayNumber +
                '}';
    }

    public int getSystemIdentificationNumber() {
        return SystemIdentificationNumber;
    }

    public void setSystemIdentificationNumber(int systemIdentificationNumber) {
        checkArgument(systemIdentificationNumber < 256);
        SystemIdentificationNumber = systemIdentificationNumber;
    }

    public boolean isHorizontalShiftIndicator() {
        return HorizontalShiftIndicator;
    }

    public void setHorizontalShiftIndicator(boolean horizontalShiftIndicator) {
        HorizontalShiftIndicator = horizontalShiftIndicator;
    }

    public boolean isVerticalShiftIndicator() {
        return VerticalShiftIndicator;
    }

    public void setVerticalShiftIndicator(boolean verticalShiftIndicator) {
        VerticalShiftIndicator = verticalShiftIndicator;
    }

    public double getLat0() {
        return Lat0;
    }

    public void setLat0(double lat0) {
        double normalized = BitUtils.normalize(lat0, 6);
        checkArgument(-90 <= normalized && normalized <= 90);
        Lat0 = normalized;
    }

    public double getLon0() {
        return Lon0;
    }

    public void setLon0(double lon0) {
        double normalized = BitUtils.normalize(lon0, 6);
        checkArgument(-180 <= normalized && normalized <= 180);
        Lon0 = normalized;
    }

    public double getdLat0() {
        return dLat0;
    }

    public void setdLat0(double dLat0) {
        double normalized = BitUtils.normalize(dLat0, 6);
        checkArgument(0 <= normalized && normalized <= 0.56875);
        this.dLat0 = normalized;
    }

    public double getdLon0() {
        return dLon0;
    }

    public void setdLon0(double dLon0) {
        double normalized = BitUtils.normalize(dLon0, 6);
        checkArgument(0 <= normalized && normalized <= 0.56875);
        this.dLon0 = normalized;
    }

    public double getMdLat() {
        return MdLat;
    }

    public void setMdLat(double mdLat) {
        double normalized = BitUtils.normalize(mdLat, 4);
        checkArgument(-0.127 <= normalized && normalized <= 0.127);
        MdLat = normalized;
    }

    public double getMdLon() {
        return MdLon;
    }

    public void setMdLon(double mdLon) {
        double normalized = BitUtils.normalize(mdLon, 4);
        checkArgument(-0.127 <= normalized && normalized <= 0.127);
        MdLon = normalized;
    }

    public double getMdH() {
        return MdH;
    }

    public void setMdH(double mdH) {
        double normalized = BitUtils.normalize(mdH, 4);
        checkArgument(-163.84 <= normalized && normalized <= 163.84);
        MdH = normalized;
    }

    public Grid[] getGridMap() {
        return gridMap;
    }

    public void setGridMap(Grid[] gridMap) {
        this.gridMap = gridMap;
    }

    public int getHorizontalInterpolationMethodIndicator() {
        return HorizontalInterpolationMethodIndicator;
    }

    public void setHorizontalInterpolationMethodIndicator(int horizontalInterpolationMethodIndicator) {
        HorizontalInterpolationMethodIndicator = horizontalInterpolationMethodIndicator;
    }

    public int getVerticalInterpolationMethodIndicator() {
        return VerticalInterpolationMethodIndicator;
    }

    public void setVerticalInterpolationMethodIndicator(int verticalInterpolationMethodIndicator) {
        VerticalInterpolationMethodIndicator = verticalInterpolationMethodIndicator;
    }

    public int getHorizontalGridQualityIndicator() {
        return HorizontalGridQualityIndicator;
    }

    public void setHorizontalGridQualityIndicator(int horizontalGridQualityIndicator) {
        HorizontalGridQualityIndicator = horizontalGridQualityIndicator;
    }

    public int getVerticalGridQualityIndicator() {
        return VerticalGridQualityIndicator;
    }

    public void setVerticalGridQualityIndicator(int verticalGridQualityIndicator) {
        VerticalGridQualityIndicator = verticalGridQualityIndicator;
    }

    public int getModifiedJulianDayNumber() {
        return ModifiedJulianDayNumber;
    }

    public void setModifiedJulianDayNumber(int modifiedJulianDayNumber) {
        ModifiedJulianDayNumber = modifiedJulianDayNumber;
    }
}

class Grid {
    public double dLat;
    public double dLon;
    public double dH;

    @Override
    public String toString() {
        return "Grid{" +
                "dFi=" + dLat +
                ", dAi=" + dLon +
                ", dHi=" + dH +
                '}';
    }
}


