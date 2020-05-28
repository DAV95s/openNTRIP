package org.adv25.ADVNTRIP.Tools.RTCM;

public class MSG1020 extends RTCM {
    private int MessageNumber;
    private int SatelliteID;
    private int SatelliteChannel;
    private int almanacHealth; //almanac health (Cn word)
    private int almanacAvailabilityIndicator;
    private int P1;
    private int tk;
    private int MSB_of_Bn;
    private int P2;
    private int tb;
    private int xn_f; //first derivative
    private int xn;
    private int xn_s; //second derivative
    private int yn_f; //first derivative
    private int yn;
    private int yn_s; //second derivative
    private int zn_f; //first derivative
    private int zn;
    private int zn_s; //second derivative
    private int P3;
    private int Gamma_n;
    private int M_P;
    private int M_ln; // (third string)
    private int Tn;
    private int M_dTn;
    private int En;
    private int M_P4;
    private int M_FT;
    private int M_NT;
    private int M_M;
    private int Availability_Additional_Data;
    private int NA; // N^A
    private int Tc;
    private int M_N4;
    private int M_Tgps;
    private int M_ln_fi; // (fifth string)
    private int reserved;

    public MSG1020(byte[] msg) {
        MessageNumber = toUnsignedInt(getBinary(16, 12));
        SatelliteID = toUnsignedInt(getBinary(28, 6));
        SatelliteChannel = toUnsignedInt(getBinary(34, 5));
        almanacHealth = toUnsignedInt(getBinary(39, 1));
        almanacAvailabilityIndicator = toUnsignedInt(getBinary(40, 1));
        P1 = toUnsignedInt(getBinary(41, 2));
        tk = toUnsignedInt(getBinary(43, 12));
        MSB_of_Bn = toUnsignedInt(getBinary(55, 1));
        P2 = toUnsignedInt(getBinary(56, 1));
        tb = toUnsignedInt(getBinary(57, 7));
        xn_f = toIntS(getBinary(64, 24));
        xn = toIntS(getBinary(88, 27));
        xn_s = toIntS(getBinary(115, 5));
        yn_f = toIntS(getBinary(120, 24));
        yn = toIntS(getBinary(144, 27));
        yn_s = toIntS(getBinary(171, 5));
        zn_f = toIntS(getBinary(176, 24));
        zn = toIntS(getBinary(200, 27));
        zn_s = toIntS(getBinary(227, 5));
        P3 = toUnsignedInt(getBinary(232, 1));
        Gamma_n = toIntS(getBinary(233, 11));
        M_P = toUnsignedInt(getBinary(244, 2));
        M_ln = toUnsignedInt(getBinary(246, 1));
        Tn = toIntS(getBinary(247, 22));
        M_dTn = toIntS(getBinary(269, 5));
        En = toUnsignedInt(getBinary(274, 5));
        M_P4 = toUnsignedInt(getBinary(279, 1));
        M_FT = toUnsignedInt(getBinary(280, 4));
        M_NT = toUnsignedInt(getBinary(284, 11));
        M_M = toUnsignedInt(getBinary(295, 2));
        Availability_Additional_Data = toUnsignedInt(getBinary(297, 1));
        NA = toUnsignedInt(getBinary(298, 11));
        Tc = toIntS(getBinary(309, 32));
        M_N4 = toUnsignedInt(getBinary(341, 5));
        M_Tgps = toIntS(getBinary(346, 22));
        M_ln_fi = toUnsignedInt(getBinary(368, 1));
        reserved = toUnsignedInt(getBinary(369, 7));
    }

    public int getMessageNumber() {
        return MessageNumber;
    }

    public int getSatelliteID() {
        return SatelliteID;
    }

    public int getSatelliteChannel() {
        return SatelliteChannel;
    }

    public int getAlmanacHealth() {
        return almanacHealth;
    }

    public int getAlmanacAvailabilityIndicator() {
        return almanacAvailabilityIndicator;
    }

    public int getP1() {
        return P1;
    }

    public int getTk() {
        return tk;
    }

    public int getMSB_of_Bn() {
        return MSB_of_Bn;
    }

    public int getP2() {
        return P2;
    }

    public int getTb() {
        return tb;
    }

    public int getXn_f() {
        return xn_f;
    }

    public int getXn() {
        return xn;
    }

    public int getXn_s() {
        return xn_s;
    }

    public int getYn_f() {
        return yn_f;
    }

    public int getYn() {
        return yn;
    }

    public int getYn_s() {
        return yn_s;
    }

    public int getZn_f() {
        return zn_f;
    }

    public int getZn() {
        return zn;
    }

    public int getZn_s() {
        return zn_s;
    }

    public int getP3() {
        return P3;
    }

    public int getGamma_n() {
        return Gamma_n;
    }

    public int getM_P() {
        return M_P;
    }

    public int getM_ln() {
        return M_ln;
    }

    public int getTn() {
        return Tn;
    }

    public int getM_dTn() {
        return M_dTn;
    }

    public int getEn() {
        return En;
    }

    public int getM_P4() {
        return M_P4;
    }

    public int getM_FT() {
        return M_FT;
    }

    public int getM_NT() {
        return M_NT;
    }

    public int getM_M() {
        return M_M;
    }

    public int getAvailability_Additional_Data() {
        return Availability_Additional_Data;
    }

    public int getNA() {
        return NA;
    }

    public int getTc() {
        return Tc;
    }

    public int getM_N4() {
        return M_N4;
    }

    public int getM_Tgps() {
        return M_Tgps;
    }

    public int getM_ln_fi() {
        return M_ln_fi;
    }

    public int getReserved() {
        return reserved;
    }
}
