package org.adv25.ADVNTRIP.Tools.RTCM;

import org.json.simple.JSONObject;

import java.nio.ByteBuffer;

public class MSG1006 implements IRTCM {

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

    @Override
    public MSG1006 parse(byte[] msg) {
        int preableIndex = 0;
        int length = msg.length;
        ByteBuffer bb = ByteBuffer.wrap(msg);
        bb.position(preableIndex);
        int lastIndex = preableIndex + length;
        String binaryMessage = "";
        while (bb.position() < lastIndex)
            binaryMessage += String.format("%8s", Integer.toBinaryString(bb.get() & 0xFF)).replace(' ', '0');

        MessageNumber = Integer.parseUnsignedInt(binaryMessage.substring(16, 28), 2);//1005 1006
        StationID = Integer.parseUnsignedInt(binaryMessage.substring(28, 40), 2);
        ITRFyear = Integer.parseUnsignedInt(binaryMessage.substring(40, 46), 2);
        GPS = binaryMessage.charAt(46) == RTCM.BIT1;
        GLONASS = binaryMessage.charAt(47) == RTCM.BIT1;
        Galileo = binaryMessage.charAt(48) == RTCM.BIT1;
        ReferenceStation = binaryMessage.charAt(49) == RTCM.BIT1;
        ECEFX = RTCM.toSignedInt(binaryMessage.substring(50, 88)) * 0.0001d;
        Oscillator = binaryMessage.charAt(88) == RTCM.BIT1;
        Reserverd = binaryMessage.charAt(89) == RTCM.BIT1;
        ECEFY = RTCM.toSignedInt(binaryMessage.substring(90, 128)) * 0.0001d;
        QuarterCycle = Integer.parseUnsignedInt(binaryMessage.substring(128, 130), 2);
        ECEFZ = RTCM.toSignedInt(binaryMessage.substring(130, 168)) * 0.0001d;
        if (MessageNumber == 1006)
            AntennaHeight = Integer.parseUnsignedInt(binaryMessage.substring(168, 184), 2) * 0.0001d;
        return this;
    }

    @Override
    public void Write() {

    }

    @Override
    public String lookup(String field) {
        switch (field) {
            case "MessageNumber":
                return String.valueOf(MessageNumber);
            case "StationID":
                return String.valueOf(StationID);
            case "ITRFyear":
                return String.valueOf(ITRFyear);
            case "GPS":
                return String.valueOf(GPS);
            case "GLONASS":
                return String.valueOf(GLONASS);
            case "Galileo":
                return String.valueOf(Galileo);
            case "ReferenceStation":
                return String.valueOf(ReferenceStation);
            case "ECEFX":
                return String.valueOf(ECEFX);
            case "Oscillator":
                return String.valueOf(Oscillator);
            case "Reserved":
                return String.valueOf(Reserverd);
            case "ECEFY":
                return String.valueOf(ECEFY);
            case "QuarterCycle":
                return String.valueOf(QuarterCycle);
            case "ECEFZ":
                return String.valueOf(ECEFZ);
            case "AntennaHeight":
                if (MessageNumber == 1006)
                    return String.valueOf(AntennaHeight);
                return null;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return String.format("MSG %s: ID:%s ITRF:%s X=%s Y=%s Z=%s AntH:%s", MessageNumber, StationID, ITRFyear, ECEFX,
                ECEFY, ECEFZ, AntennaHeight);
    }

    @Override
    public String getJson() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("MessageNumber", MessageNumber);
        jsonObject.put("StationID", StationID);
        jsonObject.put("ITRFyear", ITRFyear);
        jsonObject.put("GPS", GPS);
        jsonObject.put("GLONASS", GLONASS);
        jsonObject.put("Galileo", Galileo);
        jsonObject.put("ReferenceStation", ReferenceStation);
        jsonObject.put("ECEFX", ECEFX);
        jsonObject.put("Oscillator", Oscillator);
        jsonObject.put("Reserverd", Reserverd);
        jsonObject.put("ECEFY", ECEFY);
        jsonObject.put("QuarterCycle", QuarterCycle);
        jsonObject.put("ECEFZ", ECEFZ);
        if (MessageNumber == 1006)
            jsonObject.put("AntennaHeight", AntennaHeight);

        return jsonObject.toJSONString();
    }
}