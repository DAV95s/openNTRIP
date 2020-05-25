package org.adv25.ADVNTRIP.Tools.RTCM;

import java.util.ArrayList;
import java.util.Arrays;

public class MSG1004 extends RTCM {

    private int messageNumber;
    private int stationID;
    private double TOW;
    private boolean synchronous;
    private int signalsProcessed; //No. of GPS Satellite Signals Processed
    private boolean smoothingIndicator;
    private int smoothingInterval;

    String[] smoothing = new String[]{
            "No smoothing",
            "< 30 s",
            "30-60 s",
            "1-2 min",
            "2-4 min",
            "4-8 min",
            ">8 min",
            "Unlimited smoothing interval"
    };

    Satellite[] listSatellites;

    public MSG1004(byte[] msg) {
        rawMsg = msg;

        for (int i = 1; i < msg.length; i++) {
            binaryBuffer += toBinaryString(msg[i]);
        }

        messageNumber = Integer.parseUnsignedInt(binaryBuffer.substring(16, 28), 2);//1005 1006
        stationID = Integer.parseUnsignedInt(binaryBuffer.substring(28, 40), 2);
        TOW = toUnsignedInt(binaryBuffer.substring(40, 70)) / 1000.0d;
        synchronous = binaryBuffer.charAt(70) == RTCM.BIT1;
        signalsProcessed = toUnsignedInt(binaryBuffer.substring(71, 76));
        listSatellites = new Satellite[signalsProcessed];
        smoothingIndicator = binaryBuffer.charAt(76) == RTCM.BIT1;
        smoothingInterval = toUnsignedInt(binaryBuffer.substring(77, 80));

    }


    private class Satellite {
        //Psr - PseudoRange
        //Phr - PhaseRange
        private int ID;
        private boolean L1CodeIndicator;
        private int L1Psr;
        private int delta_L1Phr_L1Psr;
        private int L1LockTimeIndicator;
        private int L1PsrAmbiguity;
        private int L1CNR;
        private int L2CodeIndicator;
        private int delta_L2Psr_L1Psr;
        private int delta_2Phr_L1Psr;
        private int L2LockTimeIndicator;
        private int L2CNR;

        /*L1 Code Indicator
        0 - C/A Code
        1 - P(Y) Code Direct
         */
        /*GPS L2 Code Indicator
        0 - C/A or L2C code
        1 - P(Y) code direct
        2 - P(Y) code cross-correlated
        3 - Correlated P/Y
         */

    }
}
