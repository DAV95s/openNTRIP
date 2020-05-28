package org.adv25.ADVNTRIP.Tools.RTCM;

public class MSG1007 extends RTCM {

    private int antennaID;
    private int messageNumber;
    private int stationID;
    private int descriptorCounter;
    private String antennaDescriptor;

    public MSG1007(byte[] msg) {
        super.rawMsg = msg;

        super.setToBinaryBuffer(msg);

        messageNumber = toUnsignedInt(getBinary(16, 12));
        stationID = toUnsignedInt(getBinary(28, 12));
        descriptorCounter = toUnsignedInt(getBinary(40, 8));
        antennaDescriptor = "";
        for (int i = 0; i < descriptorCounter; i++) {
            antennaDescriptor += (char) toUnsignedInt(getBinary(48 + (i * 8), 8));
        }
        int pointer = 48 + (descriptorCounter * 8);
        antennaID = toUnsignedInt(getBinary(pointer, 8));

    }

    public int getAntennaID() {
        return antennaID;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public int getStationID() {
        return stationID;
    }

    public int getDescriptorCounter() {
        return descriptorCounter;
    }

    public String getAntennaDescriptor() {
        return antennaDescriptor;
    }
}
