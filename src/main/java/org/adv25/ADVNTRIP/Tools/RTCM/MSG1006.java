package org.adv25.ADVNTRIP.Tools.RTCM;

import org.json.simple.JSONObject;

import java.nio.ByteBuffer;

public class MSG1006 extends RTCM {

    private int MessageNumber;
    private int StationID;
    private int ITRFyear;
    private boolean GPS;
    private boolean GLONASS;
    private boolean Galileo;
    private boolean ReferenceStation;
    private double ECEFX;
    private boolean Oscillator;
    private boolean Reserverd;
    private double ECEFY;
    private int QuarterCycle;
    private double ECEFZ;
    private double AntennaHeight;


    public MSG1006(byte[] msg) {
        rawMsg = msg;

        for (int i = 0; i < msg.length; i++) {
            binaryBuffer += toBinaryString(msg[i]);
        }
//        int preableIndex = 0;
//        int length = msg.length;
//        ByteBuffer bb = ByteBuffer.wrap(msg);
//        bb.position(preableIndex);
//        String binaryMessage = "";
//        while (bb.position() < length)
//            binaryMessage += toBinaryString(bb.get());
//                    //String.format("%8s", Integer.toBinaryString(bb.get() & 0xFF)).replace(' ', '0');

        MessageNumber = Integer.parseUnsignedInt(binaryBuffer.substring(16, 28), 2);//1005 1006
        StationID = Integer.parseUnsignedInt(binaryBuffer.substring(28, 40), 2);
        ITRFyear = Integer.parseUnsignedInt(binaryBuffer.substring(40, 46), 2);
        GPS = binaryBuffer.charAt(46) == RTCM.BIT1;
        GLONASS = binaryBuffer.charAt(47) == RTCM.BIT1;
        Galileo = binaryBuffer.charAt(48) == RTCM.BIT1;
        ReferenceStation = binaryBuffer.charAt(49) == RTCM.BIT1;
        ECEFX = toSignedInt(binaryBuffer.substring(50, 88)) * 0.0001d;
        Oscillator = binaryBuffer.charAt(88) == RTCM.BIT1;
        Reserverd = binaryBuffer.charAt(89) == RTCM.BIT1;
        ECEFY = toSignedInt(binaryBuffer.substring(90, 128)) * 0.0001d;
        QuarterCycle = Integer.parseUnsignedInt(binaryBuffer.substring(128, 130), 2);
        ECEFZ = toSignedInt(binaryBuffer.substring(130, 168)) * 0.0001d;
        if (MessageNumber == 1006)
            AntennaHeight = Integer.parseUnsignedInt(binaryBuffer.substring(168, 184), 2) * 0.0001d;
    }

    public void Write() {

    }

    @Override
    public String toString() {
        return String.format("MSG %s: ID:%s ITRF:%s X=%s Y=%s Z=%s AntH:%s", MessageNumber, StationID, ITRFyear, ECEFX,
                ECEFY, ECEFZ, AntennaHeight);
    }

    public int getMessageNumber() {
        return MessageNumber;
    }

    public int getStationID() {
        return StationID;
    }

    public int getITRFyear() {
        return ITRFyear;
    }

    public boolean isGPS() {
        return GPS;
    }

    public boolean isGLONASS() {
        return GLONASS;
    }

    public boolean isGalileo() {
        return Galileo;
    }

    public boolean isReferenceStation() {
        return ReferenceStation;
    }

    public double getECEFX() {
        return ECEFX;
    }

    public boolean isOscillator() {
        return Oscillator;
    }

    public boolean isReserverd() {
        return Reserverd;
    }

    public double getECEFY() {
        return ECEFY;
    }

    public int getQuarterCycle() {
        return QuarterCycle;
    }

    public double getECEFZ() {
        return ECEFZ;
    }

    public double getAntennaHeight() {
        return AntennaHeight;
    }
}