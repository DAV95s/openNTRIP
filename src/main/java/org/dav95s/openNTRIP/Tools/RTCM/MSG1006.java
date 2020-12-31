package org.dav95s.openNTRIP.Tools.RTCM;

import org.dav95s.openNTRIP.Tools.BitUtil;

public class MSG1006 extends RTCM {

    private int messageNumber;
    private int stationID;
    private int ITRF;
    private boolean gpsIndicator;
    private boolean gloIndicator;
    private boolean galIndicator;
    private boolean referenceStationIndicator;
    private long ECEFX;
    private boolean oscillator;
    private boolean reserverd;
    private long ECEFY;
    private int quarterCycle;
    private long ECEFZ;
    private int antennaHeight;

    public MSG1006() {

    }

    public MSG1006(byte[] msg) {
        messageNumber = (int) BitUtil.bytesDecodeR(msg, 24, 12);
        stationID = (int) BitUtil.bytesDecodeR(msg, 36, 12);
        ITRF = (int) BitUtil.bytesDecodeR(msg, 48, 6);
        gpsIndicator = BitUtil.bytesDecodeR(msg, 54, 1) == 1;
        gloIndicator = BitUtil.bytesDecodeR(msg, 55, 1) == 1;
        galIndicator = BitUtil.bytesDecodeR(msg, 56, 1) == 1;
        referenceStationIndicator = BitUtil.bytesDecodeR(msg, 57, 1) == 1;
        ECEFX = BitUtil.getDouble(BitUtil.bytesDecodeR(msg, 58, 38), 38);
        oscillator = BitUtil.bytesDecodeR(msg, 96, 1) == 1;
        reserverd = BitUtil.bytesDecodeR(msg, 97, 1) == 1;
        ECEFY = BitUtil.getDouble(BitUtil.bytesDecodeR(msg, 98, 38), 38);
        quarterCycle = (int) BitUtil.bytesDecodeR(msg, 136, 2);
        ECEFZ = BitUtil.getDouble(BitUtil.bytesDecodeR(msg, 138, 38), 38);

        if (messageNumber == 1006) {
            antennaHeight = (int) BitUtil.bytesDecodeR(msg, 176, 16);
        }
    }

    public byte[] Write() {
        byte[] buffer;

        if (messageNumber == 1006) {
            buffer = new byte[27];
        } else {
            buffer = new byte[25];
        }

        buffer[0] = -45;
        buffer[1] = 0;
        buffer[2] = 19;
        buffer[3] = (byte) (messageNumber >> 4);
        buffer[4] = (byte) (messageNumber << 4);
        buffer[4] |= (byte) (stationID >> 8);
        buffer[5] = (byte) (stationID);
        buffer[6] = (byte) (ITRF << 2);
        buffer[6] |= gpsIndicator ? 0b0000_0010 : 0b0000_0000;
        buffer[6] |= gloIndicator ? 0b0000_0001 : 0b0000_0000;
        buffer[7] |= galIndicator ? 0b1000_0000 : 0b0000_0000;
        buffer[7] |= referenceStationIndicator ? 0b0100_0000 : 0b0000_0000;
        buffer[7] |= (byte) (ECEFX >> 32) & 0b0011_1111;
        buffer[8] = (byte) (ECEFX >> 24);
        buffer[9] = (byte) (ECEFX >> 16);
        buffer[10] = (byte) (ECEFX >> 8);
        buffer[11] = (byte) ECEFX;
        buffer[12] |= oscillator ? 0b1000_0000 : 0b0000_0000;
        buffer[12] |= reserverd ? 0b0100_0000 : 0b0000_0000;
        buffer[12] |= (byte) (ECEFY >> 32) & 0b0011_1111;
        buffer[13] = (byte) (ECEFY >> 24);
        buffer[14] = (byte) (ECEFY >> 16);
        buffer[15] = (byte) (ECEFY >> 8);
        buffer[16] = (byte) ECEFY;
        buffer[17] = (byte) (quarterCycle << 6);
        buffer[17] |= (byte) (ECEFZ >> 32) & 0b0011_1111;
        buffer[18] = (byte) (ECEFZ >> 24);
        buffer[19] = (byte) (ECEFZ >> 16);
        buffer[20] = (byte) (ECEFZ >> 8);
        buffer[21] = (byte) ECEFZ;

        if (messageNumber == 1006) {
            buffer[22] = (byte) (antennaHeight >> 8);
            buffer[23] = (byte) (antennaHeight);
            byte[] crc = BitUtil.crc24q(buffer, 22, 0);
            buffer[24] = crc[0];
            buffer[25] = crc[1];
            buffer[26] = crc[2];
        } else {
            byte[] crc = BitUtil.crc24q(buffer, 22, 0);
            buffer[22] = crc[0];
            buffer[23] = crc[1];
            buffer[24] = crc[2];
        }
        return buffer;
    }

    @Override
    public String toString() {
        return "MSG1005{" +
                "messageNumber=" + messageNumber +
                ", stationID=" + stationID +
                ", ITRFyear=" + ITRF +
                ", GPS=" + gpsIndicator +
                ", GLONASS=" + gloIndicator +
                ", Galileo=" + galIndicator +
                ", referenceStation=" + referenceStationIndicator +
                ", ECEFX=" + ECEFX +
                ", oscillator=" + oscillator +
                ", reserverd=" + reserverd +
                ", ECEFY=" + ECEFY +
                ", quarterCycle=" + quarterCycle +
                ", ECEFZ=" + ECEFZ +
                '}';
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public int getStationID() {
        return stationID;
    }

    public int getITRF() {
        return ITRF;
    }

    public boolean isGpsIndicator() {
        return gpsIndicator;
    }

    public boolean isGloIndicator() {
        return gloIndicator;
    }

    public boolean isGalIndicator() {
        return galIndicator;
    }

    public boolean isReferenceStationIndicator() {
        return referenceStationIndicator;
    }

    public boolean isOscillator() {
        return oscillator;
    }

    public boolean isReserverd() {
        return reserverd;
    }

    public int getQuarterCycle() {
        return quarterCycle;
    }

    public double getECEFX() {
        return ECEFX / 10000d;
    }

    public double getECEFY() {
        return ECEFY / 10000d;
    }

    public double getECEFZ() {
        return ECEFZ / 10000d;
    }

    public double getAntennaHeight() {
        return antennaHeight;
    }
}