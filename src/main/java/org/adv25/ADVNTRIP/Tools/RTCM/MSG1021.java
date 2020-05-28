package org.adv25.ADVNTRIP.Tools.RTCM;

public class MSG1021 extends RTCM {

    private int MessageNumber;
    private String SourceName;
    private String TargetName;
    private int SystemIdentificationNumber;
    private int UtilizedTransformationMessageIndicator;
    private int PlateNumber;
    private int ComputationIndicator;
    private int HeightIndicator;
    private int Fv; //ΦV - Latitude of Origin, Area of Validity
    private int Lv; //Longitude of Origin, Area of Validity
    private int dFv; //∆φV – N/S Extension, Area of Validity
    private int dLv; //∆λV – E/W Extension, Area of Validity
    private int dX; //dX – Translation in X-direction
    private int dY; //dY – Translation in Y-direction
    private int dZ; //dZ – Translation in Z-direction
    private int Rx; //R1 – Rotation Around the X-axis
    private int Ry; //R2 – Rotation Around the Y-axis
    private int Rz; //R3 – Rotation Around the Z-axis
    private int dS; //dS – Scale Correction
    private int add_as; //add aS – Semi-major Axis of Source System Ellipsoid
    private int add_bs; //add bS – Semi-minor Axis of Source System Ellipsoid
    private int add_at; //add aT – Semi-major Axis of Target System Ellipsoid
    private int add_bt; //add bT – Semi-minor Axis of Target System Ellipsoid
    private int HrInd; //Horizontal Helmert/Molodenski Quality Indicator
    private int VrInd; //Vertical Helmert/Molodenski Quality Indicator

    public MSG1021(byte[] msg) {
        super.rawMsg = msg;
        super.setToBinaryBuffer(msg);

        MessageNumber = toUnsignedInt(getBinary(16, 12));
        int Source_Name_Counter = toUnsignedInt(getBinary(28, 5));
        for (int i = 0; i < Source_Name_Counter; i++) {
            SourceName += toUnsignedInt(getBinary(33 + 8 * i, 8));
        }

        int pointer = Source_Name_Counter * 8;

        int Target_Name_Counter = toUnsignedInt(getBinary(pointer, 5));

        for (int i = 0; i < Target_Name_Counter; i++) {
            TargetName += toUnsignedInt(getBinary(pointer + 5 + (8 * i), 8));
        }

        pointer = pointer + 5 + 8 * Target_Name_Counter;

        SystemIdentificationNumber = toUnsignedInt(getBinary(pointer, 8));
        UtilizedTransformationMessageIndicator = toUnsignedInt(getBinary(pointer + 8, 10));
        PlateNumber = toUnsignedInt(getBinary(pointer + 18, 5));
        ComputationIndicator = toUnsignedInt(getBinary(pointer + 23, 4));
        HeightIndicator = toUnsignedInt(getBinary(pointer + 27, 2));
        Fv = toSignedInt(getBinary(pointer + 29, 19));
        Lv = toSignedInt(getBinary(pointer + 48, 20));
        dFv = toUnsignedInt(getBinary(pointer + 68, 14));
        dLv = toUnsignedInt(getBinary(pointer + 82, 14));
        dX = toSignedInt(getBinary(pointer + 96, 23));
        dY = toSignedInt(getBinary(pointer + 119, 23));
        dZ = toSignedInt(getBinary(pointer + 142, 23));
        Rx = toSignedInt(getBinary(pointer + 165, 32));
        Ry = toSignedInt(getBinary(pointer + 197, 32));
        Rz = toSignedInt(getBinary(pointer + 229, 32));
        dS = toSignedInt(getBinary(pointer + 261, 25));
        add_as = toUnsignedInt(getBinary(pointer + 286, 24));
        add_bs = toUnsignedInt(getBinary(pointer + 310, 25));
        add_at = toUnsignedInt(getBinary(pointer + 335, 24));
        add_bt = toUnsignedInt(getBinary(pointer + 359, 25));
        HrInd = toUnsignedInt(getBinary(pointer + 384 , 3));
        VrInd = toUnsignedInt(getBinary(pointer + 387 , 3));
    }
}