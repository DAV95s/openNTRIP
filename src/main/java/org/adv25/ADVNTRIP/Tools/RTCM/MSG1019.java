package org.adv25.ADVNTRIP.Tools.RTCM;

public class MSG1019 extends RTCM {

    private int messageNumber;
    private int SatelliteID;
    private int WeekNumber;
    private int SV_ACCURACY;
    private int CODE_ON_L2;
    private int IDOT;
    private int IODE;
    private int toc;
    private int af2;
    private int af1;
    private int af0;
    private int IODC;
    private int Crs;
    private int DELTAn;
    private int M0;
    private int Cuc;
    private int e;
    private int Cus;
    private int sqA; //(A)^1/2
    private int toe;
    private int Cic;
    private int OMEGA0;
    private int Cis;
    private int i0;
    private int Crc;
    private int Argument_of_Perigee;
    private int OMEGADOT; //Rate of Right Ascension
    private int tGD;
    private int SV_HEALTH;
    private int L2_P_data_flag;
    private int Fit_Interval;

    public MSG1019(byte[] msg) {
        super.rawMsg = msg;
        super.setToBinaryBuffer(msg);

        messageNumber = toUnsignedInt(getBinary(16, 12));
        SatelliteID = toUnsignedInt(getBinary(28, 6));
        WeekNumber = toUnsignedInt(getBinary(34, 10));
        SV_ACCURACY = toUnsignedInt(getBinary(44, 4));
        CODE_ON_L2 = toUnsignedInt(getBinary(48, 2));
        IDOT = toSignedInt(getBinary(50, 14));
        IODE = toUnsignedInt(getBinary(64, 8));
        toc = toUnsignedInt(getBinary(72, 16));
        af2 = toSignedInt(getBinary(88, 8));
        af1 = toSignedInt(getBinary(96, 16));
        af0 = toSignedInt(getBinary(112, 22));
        IODC = toUnsignedInt(getBinary(134, 10));
        Crs = toSignedInt(getBinary(144, 16));
        DELTAn = toSignedInt(getBinary(160, 16));
        M0 = toSignedInt(getBinary(176, 32));
        Cuc = toSignedInt(getBinary(208, 16));
        e = toUnsignedInt(getBinary(224, 32));
        Cus = toSignedInt(getBinary(256, 16));
        sqA = toUnsignedInt(getBinary(272, 32)); //(A)^1/2
        toe = toUnsignedInt(getBinary(304, 16));
        Cic = toSignedInt(getBinary(320, 16));
        OMEGA0 = toSignedInt(getBinary(336, 32));
        Cis = toSignedInt(getBinary(368, 16));
        i0 = toSignedInt(getBinary(384, 32));
        Crc = toSignedInt(getBinary(416, 16));
        Argument_of_Perigee = toSignedInt(getBinary(432, 32));
        OMEGADOT = toSignedInt(getBinary(464, 24)); //Rate of Right Ascension
        tGD = toSignedInt(getBinary(488, 8));
        SV_HEALTH = toUnsignedInt(getBinary(496, 6));
        L2_P_data_flag = toUnsignedInt(getBinary(502, 1));
        Fit_Interval = toUnsignedInt(getBinary(503, 1));
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public int getSatelliteID() {
        return SatelliteID;
    }

    public int getWeekNumber() {
        return WeekNumber;
    }

    public int getSV_ACCURACY() {
        return SV_ACCURACY;
    }

    public int getCODE_ON_L2() {
        return CODE_ON_L2;
    }

    public int getIDOT() {
        return IDOT;
    }

    public int getIODE() {
        return IODE;
    }

    public int getToc() {
        return toc;
    }

    public int getAf2() {
        return af2;
    }

    public int getAf1() {
        return af1;
    }

    public int getAf0() {
        return af0;
    }

    public int getIODC() {
        return IODC;
    }

    public int getCrs() {
        return Crs;
    }

    public int getDELTAn() {
        return DELTAn;
    }

    public int getM0() {
        return M0;
    }

    public int getCuc() {
        return Cuc;
    }

    public int getE() {
        return e;
    }

    public int getCus() {
        return Cus;
    }

    public int getSqA() {
        return sqA;
    }

    public int getToe() {
        return toe;
    }

    public int getCic() {
        return Cic;
    }

    public int getOMEGA0() {
        return OMEGA0;
    }

    public int getCis() {
        return Cis;
    }

    public int getI0() {
        return i0;
    }

    public int getCrc() {
        return Crc;
    }

    public int getArgument_of_Perigee() {
        return Argument_of_Perigee;
    }

    public int getOMEGADOT() {
        return OMEGADOT;
    }

    public int gettGD() {
        return tGD;
    }

    public int getSV_HEALTH() {
        return SV_HEALTH;
    }

    public int getL2_P_data_flag() {
        return L2_P_data_flag;
    }

    public int getFit_Interval() {
        return Fit_Interval;
    }
}