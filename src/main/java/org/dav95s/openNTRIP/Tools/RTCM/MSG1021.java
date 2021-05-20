package org.dav95s.openNTRIP.Tools.RTCM;

import com.google.common.primitives.Bytes;
import org.dav95s.openNTRIP.Tools.RTCMStream.BitUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class MSG1021 extends RTCM {

    protected int messageNumber;
    protected int SourceNameCounter;
    protected String SourceName;
    protected int TargetNameCounter;
    protected String TargetName;
    protected int SystemIdentificationNumber;
    protected int UtilizedTransformationMessageIndicator;
    protected int PlateNumber;
    protected int ComputationIndicator;
    protected int HeightIndicator;
    protected int B_valid; //ΦV, Latitude of Origin, Area of Validity
    protected int L_valid; //Longitude of Origin, Area of Validity
    protected int dB_valid; //∆φV – N/S Extension, Area of Validity
    protected int dL_valid; //∆λV – E/W Extension, Area of Validity
    protected BigDecimal dX; //dX – Translation in X-direction
    protected BigDecimal dY; //dY – Translation in Y-direction
    protected BigDecimal dZ; //dZ – Translation in Z-direction
    protected BigDecimal Rx; //R1 – Rotation Around the X-axis
    protected BigDecimal Ry; //R2 – Rotation Around the Y-axis
    protected BigDecimal Rz; //R3 – Rotation Around the Z-axis
    protected BigDecimal dS; //dS – Scale Correction
    protected BigDecimal As; //add aS – Semi-major Axis of Source System Ellipsoid
    protected BigDecimal Bs; //add bS – Semi-minor Axis of Source System Ellipsoid
    protected BigDecimal At; //add aT – Semi-major Axis of Target System Ellipsoid
    protected BigDecimal Bt; //add bT – Semi-minor Axis of Target System Ellipsoid
    protected int HorizontalQuality; //Horizontal Helmert/Molodenski Quality Indicator
    protected int VerticalQuality; //Vertical Helmert/Molodenski Quality Indicator

    protected final BigDecimal a_base = BigDecimal.valueOf(6370000);
    protected final BigDecimal b_base = BigDecimal.valueOf(6350000);

    public enum Plates {
        Africa(1),
        Antarctica(2),
        Arabia(3),
        Australia(4),
        Caribbean(5),
        Cocos(6),
        Eurasia(7),
        India(8),
        NorthAmerica(9),
        Nazca(10),
        Pacific(11),
        SouthAmerica(12),
        JuandeFuca(13),
        Philippine(14),
        Rivera(15),
        Scotia(16);

        private int id;

        public int getId() {
            return id;
        }

        Plates(int i) {
            id = i;
        }
    }

    public MSG1021() {
        messageNumber = 1021;
    }

    public MSG1021(byte[] msg) {
        BitUtils bitUtils = new BitUtils(msg);
        bitUtils.setPointer(24);

        messageNumber = bitUtils.getUnsignedInt(12);
        SourceNameCounter = bitUtils.getUnsignedInt(5);
        SourceName = bitUtils.getString(SourceNameCounter * 8);
        TargetNameCounter = bitUtils.getUnsignedInt(5);
        TargetName = bitUtils.getString(TargetNameCounter * 8);
        SystemIdentificationNumber = bitUtils.getUnsignedInt(8);
        UtilizedTransformationMessageIndicator = bitUtils.getUnsignedInt(10);
        PlateNumber = bitUtils.getUnsignedInt(5);
        ComputationIndicator = bitUtils.getUnsignedInt(4);
        HeightIndicator = bitUtils.getUnsignedInt(2);
        B_valid = bitUtils.getSignedInt(19) * 2;
        L_valid = bitUtils.getSignedInt(20) * 2;
        dB_valid = bitUtils.getUnsignedInt(14) * 2;
        dL_valid = bitUtils.getUnsignedInt(14) * 2;
        dX = new BigDecimal(bitUtils.getSignedInt(23)).multiply(BigDecimal.valueOf(0.0001));
        dY = new BigDecimal(bitUtils.getSignedInt(23)).multiply(BigDecimal.valueOf(0.0001));
        dZ = new BigDecimal(bitUtils.getSignedInt(23)).multiply(BigDecimal.valueOf(0.0001));
        Rx = new BigDecimal(bitUtils.getSignedLong(32)).multiply(BigDecimal.valueOf(0.00002));
        Ry = new BigDecimal(bitUtils.getSignedLong(32)).multiply(BigDecimal.valueOf(0.00002));
        Rz = new BigDecimal(bitUtils.getSignedLong(32)).multiply(BigDecimal.valueOf(0.00002));
        dS = new BigDecimal(bitUtils.getSignedLong(25)).multiply(BigDecimal.valueOf(0.00001));
        As = new BigDecimal(bitUtils.getUnsignedLong(24)).multiply(BigDecimal.valueOf(0.001)).add(a_base);
        Bs = new BigDecimal(bitUtils.getUnsignedLong(25)).multiply(BigDecimal.valueOf(0.001)).add(b_base);
        At = new BigDecimal(bitUtils.getUnsignedLong(24)).multiply(BigDecimal.valueOf(0.001)).add(a_base);
        Bt = new BigDecimal(bitUtils.getUnsignedLong(25)).multiply(BigDecimal.valueOf(0.001)).add(b_base);
        HorizontalQuality = bitUtils.getUnsignedInt(3);
        VerticalQuality = bitUtils.getUnsignedInt(3);
    }

    public byte[] write() {
        BitUtils bitUtils = new BitUtils();
        bitUtils.setBitString("11010011000000"); //preamble + 6 reserved bit
        bitUtils.setInt(52 + SourceNameCounter + TargetNameCounter, 10);
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
        bitUtils.setInt(B_valid / 2, 19);
        bitUtils.setInt(L_valid / 2, 20);
        bitUtils.setInt(dB_valid / 2, 14);
        bitUtils.setInt(dL_valid / 2, 14);
        bitUtils.setInt(dX.divide(BigDecimal.valueOf(0.0001), RoundingMode.HALF_EVEN).intValue(), 23);
        bitUtils.setInt(dY.divide(BigDecimal.valueOf(0.0001), RoundingMode.HALF_EVEN).intValue(), 23);
        bitUtils.setInt(dZ.divide(BigDecimal.valueOf(0.0001), RoundingMode.HALF_EVEN).intValue(), 23);
        bitUtils.setInt(Rx.divide(BigDecimal.valueOf(0.00002), RoundingMode.HALF_EVEN).intValue(), 32);
        bitUtils.setInt(Ry.divide(BigDecimal.valueOf(0.00002), RoundingMode.HALF_EVEN).intValue(), 32);
        bitUtils.setInt(Rz.divide(BigDecimal.valueOf(0.00002), RoundingMode.HALF_EVEN).intValue(), 32);
        bitUtils.setInt(dS.divide(BigDecimal.valueOf(0.00001), RoundingMode.HALF_EVEN).intValue(), 25);
        bitUtils.setInt(As.subtract(a_base).divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_EVEN).intValue(), 24);
        bitUtils.setInt(Bs.subtract(b_base).divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_EVEN).intValue(), 25);
        bitUtils.setInt(At.subtract(a_base).divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_EVEN).intValue(), 24);
        bitUtils.setInt(Bt.subtract(b_base).divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_EVEN).intValue(), 25);
        bitUtils.setInt(HorizontalQuality, 3);
        bitUtils.setInt(VerticalQuality, 3);
        byte[] bytes = bitUtils.getByteArray();
        return Bytes.concat(bytes, bitUtils.crc24q(bytes, bytes.length, 0));
    }

    @Override
    public String toString() {
        return "MSG1021{" +
                "MessageNumber=" + messageNumber +
                ", SourceName='" + SourceName + '\'' +
                ", TargetName='" + TargetName + '\'' +
                ", SystemIdentificationNumber=" + SystemIdentificationNumber +
                ", UtilizedTransformationMessageIndicator=" + UtilizedTransformationMessageIndicator +
                ", PlateNumber=" + PlateNumber +
                ", ComputationIndicator=" + ComputationIndicator +
                ", HeightIndicator=" + HeightIndicator +
                ", Fv=" + B_valid / 3600d +
                ", Lv=" + L_valid / 3600d +
                ", dFv=" + dB_valid / 3600d +
                ", dLv=" + dL_valid / 3600d +
                ", dX=" + dX +
                ", dY=" + dY +
                ", dZ=" + dZ +
                ", Rx=" + Rx +
                ", Ry=" + Ry +
                ", Rz=" + Rz +
                ", dS=" + dS +
                ", add_as=" + As +
                ", add_bs=" + Bs +
                ", add_at=" + At +
                ", add_bt=" + Bt +
                ", HrInd=" + HorizontalQuality +
                ", VrInd=" + VerticalQuality +
                '}';
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public void setMessageNumber(int messageNumber) {
        checkArgument(messageNumber == 1021 || messageNumber == 1022);
        this.messageNumber = messageNumber;
    }

    public String getSourceName() {
        return SourceName;
    }

    public void setSourceName(String sourceName) {
        checkArgument(sourceName.length() < 32);
        SourceName = sourceName;
        SourceNameCounter = sourceName.length();
    }

    public String getTargetName() {
        return TargetName;
    }

    public void setTargetName(String targetName) {
        checkArgument(targetName.length() < 32);
        TargetName = targetName;
        TargetNameCounter = targetName.length();
    }

    public int getSystemIdentificationNumber() {
        return SystemIdentificationNumber;
    }

    public void setSystemIdentificationNumber(int systemIdentificationNumber) {
        checkArgument(systemIdentificationNumber < 256);
        SystemIdentificationNumber = systemIdentificationNumber;
    }

    public int getUtilizedTransformationMessageIndicator() {
        return UtilizedTransformationMessageIndicator;
    }

    /**
     * @param utilizedTransformationMessageIndicator 0 - Message not utilized
     *                                               1 - Message utilized
     *                                               Bit(1) - 1023
     *                                               Bit(2) - 1024
     *                                               Bit(3) - 1025
     *                                               Bit(4) - 1026
     *                                               Bit(5) - 1027
     */
    public void setUtilizedTransformationMessageIndicator(int utilizedTransformationMessageIndicator) {
        checkArgument(utilizedTransformationMessageIndicator < 1024);
        UtilizedTransformationMessageIndicator = utilizedTransformationMessageIndicator;
    }

    public int getPlateNumber() {
        return PlateNumber;
    }

    /**
     * @param plateNumber 0: unknown plate
     *                    1: AFRC - Africa
     *                    2: ANTA - Antarctica
     *                    3: ARAB - Arabia
     *                    4: AUST - Australia
     *                    5: CARB - Caribbea
     *                    6: COCO - Cocos
     *                    7: EURA - Eurasia
     *                    8: INDI - India
     *                    9: NOAM - N. America
     *                    10: NAZC - Nazca
     *                    11: PCFC - Pacific
     *                    12: SOAM - S. America
     *                    13: JUFU - Juan de Fuca
     *                    14: PHIL - Philippine
     *                    15: RIVR - Rivera
     *                    16: SCOT - Scotia
     */
    public void setPlateNumber(int plateNumber) {
        checkArgument(plateNumber < 32);
        PlateNumber = plateNumber;
    }

    public int getComputationIndicator() {
        return ComputationIndicator;
    }

    /**
     * @param computationIndicator 0 - standard seven parameter, approximation
     *                             1 - standard seven parameter, strict formula
     *                             2 - Molodenski, abridged
     *                             3 - Molodenski-Badekas
     */
    public void setComputationIndicator(int computationIndicator) {
        checkArgument(computationIndicator < 16);
        ComputationIndicator = computationIndicator;
    }

    public int getHeightIndicator() {
        return HeightIndicator;
    }

    /**
     * @param heightIndicator 0 - Geometric heights result
     *                        1 - Physical heights result
     *                        2 - Physical heights result
     */
    public void setHeightIndicator(int heightIndicator) {
        checkArgument(heightIndicator < 4);
        HeightIndicator = heightIndicator;
    }

    public int getB_valid() {
        return B_valid;
    }

    /**
     * @param b_valid Latitude of Origin (sec)
     */
    public void setB_valid(double b_valid) {
        checkArgument(-90 <= b_valid && b_valid <= 90);
        B_valid = (int) (b_valid * 3600);
    }

    public int getL_valid() {
        return L_valid;
    }

    /**
     * @param l_valid Longitude of Origin (sec);
     */
    public void setL_valid(double l_valid) {
        checkArgument(-180 <= l_valid && l_valid <= 180);
        L_valid = (int) (l_valid * 3600);
    }

    public int getdB_valid() {
        return dB_valid;
    }

    /**
     * @param dB_valid Area Extension to North and to South
     *                 0 - undefined
     */
    public void setdB_valid(double dB_valid) {
        checkArgument(0 <= dB_valid && dB_valid <= 9.10);
        this.dB_valid = (int) (dB_valid * 3600);
        ;
    }

    public int getdL_valid() {
        return dL_valid;
    }

    /**
     * @param dL_valid Area Extension to East and to West
     *                 0 - undefined
     */
    public void setdL_valid(double dL_valid) {
        checkArgument(0 <= dL_valid && dL_valid <= 9.10);
        this.dL_valid = (int) (dL_valid * 3600);
    }

    public BigDecimal getdX() {
        return dX;
    }

    /**
     * @param dX Translation in X
     *           ± 4194.303 m
     */
    public void setdX(BigDecimal dX) {
        checkNotNull(dX);
        checkArgument(dX.compareTo(new BigDecimal("4194.303")) <= 0);
        checkArgument(dX.compareTo(new BigDecimal("-4194.303")) >= 0);
        this.dX = dX;
    }

    public BigDecimal getdY() {
        return dY;
    }

    /**
     * @param dY Translation in Y
     *           ± 4194.303 m
     */
    public void setdY(BigDecimal dY) {
        checkNotNull(dY);
        checkArgument(dY.compareTo(new BigDecimal("4194.303")) <= 0);
        checkArgument(dY.compareTo(new BigDecimal("-4194.303")) >= 0);
        this.dY = dY;
    }

    public BigDecimal getdZ() {
        return dZ;
    }

    /**
     * @param dZ Translation in Z
     *           ± 4194.303 m
     */
    public void setdZ(BigDecimal dZ) {
        checkNotNull(dZ);
        checkArgument(dZ.compareTo(new BigDecimal("4194.303")) <= 0);
        checkArgument(dZ.compareTo(new BigDecimal("-4194.303")) >= 0);
        this.dZ = dZ;
    }

    public BigDecimal getRx() {
        return Rx;
    }

    /**
     * @param rx Rotation around the X-axis in arc seconds
     *           ± 42949.67294"
     */
    public void setRx(BigDecimal rx) {
        checkNotNull(rx);
        checkArgument(rx.compareTo(new BigDecimal("42949.67294")) <= 0);
        checkArgument(rx.compareTo(new BigDecimal("-42949.67294")) >= 0);
        Rx = rx;
    }


    public BigDecimal getRy() {
        return Ry;
    }

    /**
     * @param ry Rotation around the Y-axis in arc seconds
     *           ± 42949.67294"
     */
    public void setRy(BigDecimal ry) {
        checkNotNull(ry);
        checkArgument(ry.compareTo(new BigDecimal("42949.67294")) <= 0);
        checkArgument(ry.compareTo(new BigDecimal("-42949.67294")) >= 0);
        Ry = ry;
    }

    public BigDecimal getRz() {
        return Rz;
    }

    /**
     * @param rz Rotation around the Z-axis in arc seconds
     *           ± 42949.67294"
     */
    public void setRz(BigDecimal rz) {
        checkNotNull(rz);
        checkArgument(rz.compareTo(new BigDecimal("42949.67294")) <= 0);
        checkArgument(rz.compareTo(new BigDecimal("-42949.67294")) >= 0);
        Rz = rz;
    }

    public BigDecimal getdS() {
        return dS;
    }

    /**
     * @param dS scale correction
     *           ± 167.77215 PPM
     */
    public void setdS(BigDecimal dS) {
        checkNotNull(dS);
        checkArgument(dS.compareTo(new BigDecimal("42949.67294")) <= 0);
        checkArgument(dS.compareTo(new BigDecimal("-42949.67294")) >= 0);
        this.dS = dS;
    }

    public BigDecimal getAs() {
        return As;
    }

    /**
     * @param as Semi-major axis of source system ellipsoid
     *           0 – 16777.215m
     */
    public void setAs(BigDecimal as) {
        checkNotNull(as);
        checkArgument(as.signum() >= 0);
        checkArgument(as.compareTo(new BigDecimal("16777.215")) >= 0);
        this.As = as;
    }

    public BigDecimal getBs() {
        return Bs;
    }

    /**
     * @param bs Semi-minor axis of source system ellipsoid
     *           0 – 33554.431m
     */
    public void setBs(BigDecimal bs) {
        checkNotNull(bs);
        checkArgument(bs.signum() >= 0);
        checkArgument(bs.compareTo(new BigDecimal("33554.431")) >= 0);
        this.Bs = bs;
    }

    public BigDecimal getAt() {
        return At;
    }

    /**
     * @param at Semi-major axis of target system ellipsoid
     *           0 – 16777.215m
     */
    public void setAt(BigDecimal at) {
        checkNotNull(at);
        checkArgument(at.signum() >= 0);
        checkArgument(at.compareTo(new BigDecimal("16777.215")) >= 0);
        this.At = at;
    }

    public BigDecimal getBt() {
        return Bt;
    }

    /**
     * @param bt Semi-minor axis of target system ellipsoid
     *           0 – 33554.431m
     */
    public void setBt(BigDecimal bt) {
        checkNotNull(bt);
        checkArgument(bt.signum() >= 0);
        checkArgument(bt.compareTo(new BigDecimal("33554.431")) >= 0);
        this.Bt = bt;
    }

    public int getHorizontalQuality() {
        return HorizontalQuality;
    }

    /**
     * @param horizontalQuality 0 - Unknown quality
     *                          1 - Quality better 21 Millimeters
     *                          2 - Quality 21 to 50 Millimeters
     *                          3 - Quality 51 to 200 Millimeters
     *                          4 - Quality 201 to 500 Millimeters
     *                          5 - Quality 501 to 2000 Millimeters
     *                          6 - Quality 2001 to 5000 Millimeters
     *                          7 - Quality worse than 5001 Millimeters
     */
    public void setHorizontalQuality(int horizontalQuality) {
        HorizontalQuality = horizontalQuality;
    }

    public int getVerticalQuality() {
        return VerticalQuality;
    }

    /**
     * @param verticalQuality 0 - Unknown quality
     *                        1 - Quality better 21 Millimeters
     *                        2 - Quality 21 to 50 Millimeters
     *                        3 - Quality 51 to 200 Millimeters
     *                        4 - Quality 201 to 500 Millimeters
     *                        5 - Quality 501 to 2000 Millimeters
     *                        6 - Quality 2001 to 5000 Millimeters
     *                        7 - Quality worse than 5001 Millimeters
     */
    public void setVerticalQuality(int verticalQuality) {
        VerticalQuality = verticalQuality;
    }
}