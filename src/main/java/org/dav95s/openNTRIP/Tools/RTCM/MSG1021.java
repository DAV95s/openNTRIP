package org.dav95s.openNTRIP.Tools.RTCM;

import com.google.common.primitives.Bytes;
import org.dav95s.openNTRIP.Tools.RTCM.Assets.CRS1;
import org.dav95s.openNTRIP.Tools.RTCMStream.BitUtils;

import java.util.BitSet;

import static com.google.common.base.Preconditions.checkArgument;

public class MSG1021 implements CRS1 {

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
    protected double LatValid; //ΦV, Latitude of Origin, Area of Validity
    protected double LonValid; //Longitude of Origin, Area of Validity
    protected double dLatValid; //∆φV – N/S Extension, Area of Validity
    protected double dLonValid; //∆λV – E/W Extension, Area of Validity
    protected double dX; //dX – Translation in X-direction
    protected double dY; //dY – Translation in Y-direction
    protected double dZ; //dZ – Translation in Z-direction
    protected double Rx; //R1 – Rotation Around the X-axis
    protected double Ry; //R2 – Rotation Around the Y-axis
    protected double Rz; //R3 – Rotation Around the Z-axis
    protected double dS; //dS – Scale Correction
    protected double As; //add aS – Semi-major Axis of Source System Ellipsoid
    protected double Bs; //add bS – Semi-minor Axis of Source System Ellipsoid
    protected double At; //add aT – Semi-major Axis of Target System Ellipsoid
    protected double Bt; //add bT – Semi-minor Axis of Target System Ellipsoid
    protected int HorizontalQuality; //Horizontal Helmert/Molodenski Quality Indicator
    protected int VerticalQuality; //Vertical Helmert/Molodenski Quality Indicator

    public MSG1021() {
        messageNumber = 1021;
    }

    public MSG1021(byte[] msg) {
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
        bitUtils.setInt((int) Math.round((As - 6370000) * 1000), 24);
        bitUtils.setInt((int) Math.round((Bs - 6350000) * 1000), 25);
        bitUtils.setInt((int) Math.round((At - 6370000) * 1000), 24);
        bitUtils.setInt((int) Math.round((Bt - 6350000) * 1000), 25);
        bitUtils.setInt(HorizontalQuality, 3);
        bitUtils.setInt(VerticalQuality, 3);
        byte[] bytes = bitUtils.getByteArray();
        return Bytes.concat(bytes, bitUtils.crc24q(bytes, bytes.length, 0));
    }

    @Override
    public String toString() {
        return "MSG1021{" +
                "messageNumber=" + messageNumber +
                ", SourceName='" + SourceName + '\'' +
                ", TargetName='" + TargetName + '\'' +
                ", SystemIdentificationNumber=" + SystemIdentificationNumber +
                ", UtilizedTransformationMessageIndicator=" + UtilizedTransformationMessageIndicator +
                ", PlateNumber=" + PlateNumber +
                ", ComputationIndicator=" + ComputationIndicator +
                ", HeightIndicator=" + HeightIndicator +
                ", LatValid=" + LatValid +
                ", LonValid=" + LonValid +
                ", dLatValid=" + dLatValid +
                ", dLonValid=" + dLonValid +
                ", dX=" + dX +
                ", dY=" + dY +
                ", dZ=" + dZ +
                ", Rx=" + Rx +
                ", Ry=" + Ry +
                ", Rz=" + Rz +
                ", dS=" + dS +
                ", As=" + As +
                ", Bs=" + Bs +
                ", At=" + At +
                ", Bt=" + Bt +
                ", HorizontalQuality=" + HorizontalQuality +
                ", VerticalQuality=" + VerticalQuality +
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
        BitSet set = new BitSet(5);

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

    public double getLatValid() {
        return LatValid;
    }

    /**
     * @param latValid Latitude of Origin (sec)
     */
    public void setLatValid(double latValid) {
        double normalized = BitUtils.normalize(latValid, 6);
        checkArgument(-90 <= normalized && normalized <= 90);
        LatValid = normalized;
    }

    public double getLonValid() {
        return LonValid;
    }

    /**
     * @param lonValid Longitude of Origin (sec);
     */
    public void setLonValid(double lonValid) {
        double normalized = BitUtils.normalize(lonValid, 6);
        checkArgument(-180 <= normalized && normalized <= 180);
        LonValid = normalized;
    }

    public double getdLatValid() {
        return dLatValid;
    }

    /**
     * @param dLatValid Area Extension to North and to South
     *                  0 - undefined
     */
    public void setdLatValid(double dLatValid) {
        double normalized = BitUtils.normalize(dLatValid, 6);
        checkArgument(0 <= normalized && normalized <= 9.10166);
        this.dLatValid = normalized;
    }

    public double getdLonValid() {
        return dLonValid;
    }

    /**
     * @param dLonValid Area Extension to East and to West
     *                  0 - undefined
     */
    public void setdLonValid(double dLonValid) {
        double normalized = BitUtils.normalize(dLonValid, 6);
        checkArgument(0 <= normalized && normalized <= 9.10166);
        this.dLonValid = normalized;
    }

    public double getdX() {
        return dX;
    }

    /**
     * @param dX Translation in X
     *           ± 4194.303 m
     */
    public void setdX(double dX) {
        double normalized = BitUtils.normalize(dX, 4);
        checkArgument(-4194.303 <= normalized && normalized <= 4194.303);
        this.dX = normalized;
    }

    public double getdY() {
        return dY;
    }

    /**
     * @param dY Translation in Y
     *           ± 4194.303 m
     */
    public void setdY(double dY) {
        double normalized = BitUtils.normalize(dY, 4);
        checkArgument(-4194.303 <= normalized && normalized <= 4194.303);
        this.dY = normalized;
    }

    public double getdZ() {
        return dZ;
    }

    /**
     * @param dZ Translation in Z
     *           ± 4194.303 m
     */
    public void setdZ(double dZ) {
        double normalized = BitUtils.normalize(dZ, 4);
        checkArgument(-4194.303 <= normalized && normalized <= 4194.303);
        this.dZ = normalized;
    }

    public double getRx() {
        return Rx;
    }

    /**
     * @param rx Rotation around the X-axis in arc seconds
     *           ± 42949.67294"
     */
    public void setRx(double rx) {
        double normalized = BitUtils.normalize(rx, 6);
        checkArgument(-42949.67294 <= normalized && normalized <= 42949.67294);
        Rx = normalized;
    }


    public double getRy() {
        return Ry;
    }

    /**
     * @param ry Rotation around the Y-axis in arc seconds
     *           ± 42949.67294"
     */
    public void setRy(double ry) {
        double normalized =BitUtils.normalize(ry, 6);
        checkArgument(-42949.67294 <= normalized && normalized <= 42949.67294);
        Ry = normalized;
    }

    public double getRz() {
        return Rz;
    }

    /**
     * @param rz Rotation around the Z-axis in arc seconds
     *           ± 42949.67294"
     */
    public void setRz(double rz) {
        double normalized = BitUtils.normalize(rz, 6);
        checkArgument(-42949.67294 <= normalized && normalized <= 42949.67294);
        Rz = normalized;
    }

    public double getdS() {
        return dS;
    }

    /**
     * @param dS scale correction
     *           ± 167.77215 PPM
     */
    public void setdS(double dS) {
        double normalized = BitUtils.normalize(dS, 5);
        checkArgument(-167.77215 <= normalized && normalized <= 167.77215);
        this.dS = normalized;
    }

    public double getAs() {
        return As;
    }

    /**
     * @param as Semi-major axis of source system ellipsoid
     */
    public void setAs(double as) {
        double normalized = BitUtils.normalize(as, 4);
        checkArgument(6370000 <= normalized && normalized <= 6386777.215);
        this.As = normalized;
    }

    public double getBs() {
        return Bs;
    }

    /**
     * @param bs Semi-minor axis of source system ellipsoid
     */
    public void setBs(double bs) {
        double normalized = BitUtils.normalize(bs, 4);
        checkArgument(6350000 <= normalized && normalized <= 6383554.431);
        this.Bs = normalized;
    }

    public double getAt() {
        return At;
    }

    /**
     * @param at Semi-major axis of target system ellipsoid
     *           0 – 16777.215m
     */
    public void setAt(double at) {
        double normalized =BitUtils.normalize(at, 4);
        checkArgument(6370000 <= normalized && normalized <= 6386777.215);
        this.At = normalized;
    }

    public double getBt() {
        return Bt;
    }

    /**
     * @param bt Semi-minor axis of target system ellipsoid
     *           0 – 33554.431m
     */
    public void setBt(double bt) {
        double normalized = BitUtils.normalize(bt, 4);
        checkArgument(6350000 <= normalized && normalized <= 6383554.431);
        this.Bt = normalized;
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

