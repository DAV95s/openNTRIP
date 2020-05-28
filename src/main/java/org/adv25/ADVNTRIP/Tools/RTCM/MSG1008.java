package org.adv25.ADVNTRIP.Tools.RTCM;

public class MSG1008 extends RTCM {

    private int antennaID;
    private int messageNumber;
    private int stationID;
    private String antennaDescriptor;
    private String serialNumber;

    public MSG1008(byte[] msg) {
        super.rawMsg = msg;

        super.setToBinaryBuffer(msg);

        messageNumber = toUnsignedInt(getBinary(16, 12));
        stationID = toUnsignedInt(getBinary(28, 12));
        int descriptorCounter = toUnsignedInt(getBinary(40, 8));

        /* antennaDescriptor */
        antennaDescriptor = "";
        for (int i = 0; i < descriptorCounter; i++) {
            antennaDescriptor += (char) toUnsignedInt(getBinary(48 + (i * 8), 8));
        }
        /* antennaDescriptor */

        int pointer = 48 + (descriptorCounter * 8); //pointer to next
        antennaID = toUnsignedInt(getBinary(pointer, 8));

        /* serialNumber */
        int serialNumberCounter = toUnsignedInt(getBinary(pointer + 8, 8));
        serialNumber = "";
        for (int i = 0; i < serialNumberCounter; i++) {
            serialNumber += (char) toUnsignedInt(getBinary((pointer + 16) + (i * 8), 8));
        }
        /* serialNumber */

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

    public String getAntennaDescriptor() {
        return antennaDescriptor;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
}
