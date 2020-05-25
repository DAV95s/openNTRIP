package org.adv25.ADVNTRIP.Tools.RTCM;

public class MSG1001 extends RTCM {
    // i cant find raw 1001 msg for test
    private int messageNumber;
    private int stationID;
    private int TOW;
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

    public MSG1001(byte[] msg) {
        rawMsg = msg;

        for (int i = 1; i < msg.length; i++) {
            binaryBuffer += toBinaryString(msg[i]);
        }

        messageNumber = Integer.parseUnsignedInt(binaryBuffer.substring(16, 28), 2);//1005 1006
        stationID = Integer.parseUnsignedInt(binaryBuffer.substring(28, 40), 2);
        TOW = Integer.parseInt(binaryBuffer.substring(40, 70));
        synchronous = binaryBuffer.charAt(70) == RTCM.BIT1;
        signalsProcessed = Integer.parseInt(binaryBuffer.substring(71, 76));


    }
    class Satellite {
        private int ID;
        private boolean codeIndicator;
        private int pseudoRange;
        private int deltaRange;
        private int lockTime;
    }
}


