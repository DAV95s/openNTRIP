package org.dav95s.openNTRIP.Tools.RTCM;

import com.google.common.primitives.Bytes;
import org.dav95s.openNTRIP.Tools.BitUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MSG1021 extends RTCM {

    private int messageNumber;
    private int SourceNameCounter;
    private String SourceName;
    private int TargetNameCounter;
    private String TargetName;
    private int SystemIdentificationNumber;
    private int UtilizedTransformationMessageIndicator;
    private int PlateNumber;
    private int ComputationIndicator;
    private int HeightIndicator;
    private int B_valid; //ΦV, //Latitude of Origin, Area of Validity
    private int L_valid; //Longitude of Origin, Area of Validity
    private int dB_valid; //∆φV – N/S Extension, Area of Validity
    private int dL_valid; //∆λV – E/W Extension, Area of Validity
    private BigDecimal dX; //dX – Translation in X-direction
    private BigDecimal dY; //dY – Translation in Y-direction
    private BigDecimal dZ; //dZ – Translation in Z-direction
    private BigDecimal Rx; //R1 – Rotation Around the X-axis
    private BigDecimal Ry; //R2 – Rotation Around the Y-axis
    private BigDecimal Rz; //R3 – Rotation Around the Z-axis
    private BigDecimal dS; //dS – Scale Correction
    private BigDecimal add_as; //add aS – Semi-major Axis of Source System Ellipsoid
    private BigDecimal add_bs; //add bS – Semi-minor Axis of Source System Ellipsoid
    private BigDecimal add_at; //add aT – Semi-major Axis of Target System Ellipsoid
    private BigDecimal add_bt; //add bT – Semi-minor Axis of Target System Ellipsoid
    private int HrInd; //Horizontal Helmert/Molodenski Quality Indicator
    private int VrInd; //Vertical Helmert/Molodenski Quality Indicator

    private final BigDecimal a_base = BigDecimal.valueOf(6370000);
    private final BigDecimal b_base = BigDecimal.valueOf(6350000);

    public enum msg {
        MSG1021(1021), MSG1022(1022);
        int msgNum;

        public int getValue() {
            return msgNum;
        }

        msg(int msgNum) {
            this.msgNum = msgNum;
        }
    }

    public enum ComputationIndicator {
        sevenParameterApproximation,
        sevenParameterStrict,
        MolodenskiAbridged,
        MolodenskiBadekas,
    }


    public enum Plates {
        AFRC(1), //Africa
        ANTA(2), //Antarctica
        ARAB(3), //Arabia
        AUST(4), //Australia
        CARB(5), //Caribbea
        COCO(6), //Cocos
        EURA(7), //Eurasia
        INDI(8), //India
        NOAM(9), //N. America
        NAZC(10), //Nazca
        PCFC(11), //Pacific
        SOAM(12), //S. America
        JUFU(13), //Juan de Fuca
        PHIL(14), //Philippine
        RIVR(15), //Rivera
        SCOT(16); //Scotia

        private int id;

        public int getId() {
            return id;
        }

        Plates(int i) {
            id = i;
        }
    }

    public MSG1021() {

    }

    public MSG1021(byte[] msg) {
        BitUtils bitUtils = new BitUtils(msg);
        bitUtils.setShiftPointer(24);

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
        add_as = new BigDecimal(bitUtils.getUnsignedLong(24)).multiply(BigDecimal.valueOf(0.001)).add(a_base);
        add_bs = new BigDecimal(bitUtils.getUnsignedLong(25)).multiply(BigDecimal.valueOf(0.001)).add(b_base);
        add_at = new BigDecimal(bitUtils.getUnsignedLong(24)).multiply(BigDecimal.valueOf(0.001)).add(a_base);
        add_bt = new BigDecimal(bitUtils.getUnsignedLong(25)).multiply(BigDecimal.valueOf(0.001)).add(b_base);
        HrInd = bitUtils.getUnsignedInt(3);
        VrInd = bitUtils.getUnsignedInt(3);
    }

    public byte[] write() {
        BitUtils bitUtils = new BitUtils();
        bitUtils.setBitString("11010011000000"); //preamble + 6 reserved bit
        bitUtils.setInt(52 + SourceName.length() + TargetName.length(), 10);
        bitUtils.setInt(messageNumber, 12);
        bitUtils.setInt(SourceName.length(), 5);
        bitUtils.setString(SourceName);
        bitUtils.setInt(TargetName.length(), 5);
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
        bitUtils.setInt(add_as.subtract(a_base).divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_EVEN).intValue(), 24);
        bitUtils.setInt(add_bs.subtract(b_base).divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_EVEN).intValue(), 25);
        bitUtils.setInt(add_at.subtract(a_base).divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_EVEN).intValue(), 24);
        bitUtils.setInt(add_bt.subtract(b_base).divide(BigDecimal.valueOf(0.001), RoundingMode.HALF_EVEN).intValue(), 25);
        bitUtils.setInt(HrInd, 3);
        bitUtils.setInt(VrInd, 3);
        byte[] bytes = bitUtils.makeByteArr();
        return Bytes.concat(bytes, bitUtils.crc24q(bytes, bytes.length, 0));
    }

    @Override
    public String toString() {
        return "MSG1021{" +
                "MessageNumber=" + messageNumber +
                ", SourceNameCounter=" + SourceNameCounter +
                ", SourceName='" + SourceName + '\'' +
                ", TargetNameCounter=" + TargetNameCounter +
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
                ", add_as=" + add_as +
                ", add_bs=" + add_bs +
                ", add_at=" + add_at +
                ", add_bt=" + add_bt +
                ", HrInd=" + HrInd +
                ", VrInd=" + VrInd +
                '}';
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    public String getSourceName() {
        return SourceName;
    }

    public void setSourceName(String sourceName) {
        SourceName = sourceName;
    }


    public String getTargetName() {
        return TargetName;
    }

    public void setTargetName(String targetName) {
        TargetName = targetName;
    }

    public int getSystemIdentificationNumber() {
        return SystemIdentificationNumber;
    }

    public void setSystemIdentificationNumber(int systemIdentificationNumber) {
        SystemIdentificationNumber = systemIdentificationNumber;
    }

    public int getUtilizedTransformationMessageIndicator() {
        return UtilizedTransformationMessageIndicator;
    }

    public void setUtilizedTransformationMessageIndicator(int utilizedTransformationMessageIndicator) {
        UtilizedTransformationMessageIndicator = utilizedTransformationMessageIndicator;
    }

    public int getPlateNumber() {
        return PlateNumber;
    }

    public void setPlateNumber(int plateNumber) {
        PlateNumber = plateNumber;
    }

    public int getComputationIndicator() {
        return ComputationIndicator;
    }

    public void setComputationIndicator(int computationIndicator) {
        ComputationIndicator = computationIndicator;
    }

    public int getHeightIndicator() {
        return HeightIndicator;
    }

    public void setHeightIndicator(int heightIndicator) {
        HeightIndicator = heightIndicator;
    }

    public int getB_valid() {
        return B_valid;
    }

    public void setB_valid(int b_valid) {
        B_valid = b_valid;
    }

    public int getL_valid() {
        return L_valid;
    }

    public void setL_valid(int l_valid) {
        L_valid = l_valid;
    }

    public int getdB_valid() {
        return dB_valid;
    }

    public void setdB_valid(int dB_valid) {
        this.dB_valid = dB_valid;
    }

    public int getdL_valid() {
        return dL_valid;
    }

    public void setdL_valid(int dL_valid) {
        this.dL_valid = dL_valid;
    }

    public BigDecimal getdX() {
        return dX;
    }

    public void setdX(BigDecimal dX) {
        this.dX = dX;
    }

    public BigDecimal getdY() {
        return dY;
    }

    public void setdY(BigDecimal dY) {
        this.dY = dY;
    }

    public BigDecimal getdZ() {
        return dZ;
    }

    public void setdZ(BigDecimal dZ) {
        this.dZ = dZ;
    }

    public BigDecimal getRx() {
        return Rx;
    }

    public void setRx(BigDecimal rx) {
        Rx = rx;
    }

    public BigDecimal getRy() {
        return Ry;
    }

    public void setRy(BigDecimal ry) {
        Ry = ry;
    }

    public BigDecimal getRz() {
        return Rz;
    }

    public void setRz(BigDecimal rz) {
        Rz = rz;
    }

    public BigDecimal getdS() {
        return dS;
    }

    public void setdS(BigDecimal dS) {
        this.dS = dS;
    }

    public BigDecimal getAdd_as() {
        return add_as;
    }

    public void setAdd_as(BigDecimal add_as) {
        this.add_as = add_as;
    }

    public BigDecimal getAdd_bs() {
        return add_bs;
    }

    public void setAdd_bs(BigDecimal add_bs) {
        this.add_bs = add_bs;
    }

    public BigDecimal getAdd_at() {
        return add_at;
    }

    public void setAdd_at(BigDecimal add_at) {
        this.add_at = add_at;
    }

    public BigDecimal getAdd_bt() {
        return add_bt;
    }

    public void setAdd_bt(BigDecimal add_bt) {
        this.add_bt = add_bt;
    }

    public int getHrInd() {
        return HrInd;
    }

    public void setHrInd(int hrInd) {
        HrInd = hrInd;
    }

    public int getVrInd() {
        return VrInd;
    }

    public void setVrInd(int vrInd) {
        VrInd = vrInd;
    }

    public BigDecimal getA_base() {
        return a_base;
    }

    public BigDecimal getB_base() {
        return b_base;
    }
}