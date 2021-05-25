package org.dav95s.openNTRIP.Tools.RTCM;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Bytes;
import org.dav95s.openNTRIP.Tools.RTCMStream.BitUtils;

public class MSG1006 {

    private int messageNumber = 1005;
    private int stationID;
    private int ITRFyear;
    private boolean gpsIndicator;
    private boolean gloIndicator;
    private boolean galIndicator;
    private boolean referenceStationIndicator;
    private double ECEFX;
    private boolean oscillator;
    private boolean reserverd;
    private double ECEFY;
    private int quarterCycle;
    private double ECEFZ;
    private double antennaHeight;

    public MSG1006() {

    }

    public MSG1006(byte[] msg) {
        BitUtils bits = new BitUtils(msg);
        bits.setPointer(24);
        setMessageNumber(bits.getUnsignedInt(12));
        setStationID(bits.getUnsignedInt(12));
        setITRFyear(bits.getUnsignedInt(6));
        setGpsIndicator(bits.getBoolean());
        setGloIndicator(bits.getBoolean());
        setGalIndicator(bits.getBoolean());
        setReferenceStationIndicator(bits.getBoolean());
        setECEFX(bits.getSignedLong(38) * 0.0001);
        setOscillator(bits.getBoolean());
        setReserverd(bits.getBoolean());
        setECEFY(bits.getSignedLong(38) * 0.0001);
        setQuarterCycle(bits.getUnsignedInt(2));
        setECEFZ(bits.getSignedLong(38) * 0.0001);

        if (messageNumber == 1006) {
            setAntennaHeight(bits.getUnsignedInt(16) * 0.0001);
        }
    }

    public byte[] getBytes() {
        BitUtils bitUtils = new BitUtils();
        bitUtils.setBitString("11010011000000"); //preamble + 6 reserved bit
        if (messageNumber == 1005) {
            bitUtils.setInt(19, 10);
        } else {
            bitUtils.setInt(21, 10);
        }
        bitUtils.setInt(messageNumber, 12);
        bitUtils.setInt(stationID, 12);
        bitUtils.setInt(ITRFyear, 6);
        bitUtils.setBoolean(gpsIndicator);
        bitUtils.setBoolean(gloIndicator);
        bitUtils.setBoolean(galIndicator);
        bitUtils.setBoolean(referenceStationIndicator);
        bitUtils.setLong(Math.round(ECEFX * 10000), 38);
        bitUtils.setBoolean(oscillator);
        bitUtils.setBoolean(reserverd);
        bitUtils.setLong(Math.round(ECEFY * 10000), 38);
        bitUtils.setInt(quarterCycle, 2);
        bitUtils.setLong(Math.round(ECEFZ * 10000), 38);
        if (messageNumber == 1006) {
            bitUtils.setLong((long) (antennaHeight * 10000), 16);
        }

        byte[] bytes = bitUtils.getByteArray();
        return Bytes.concat(bytes, bitUtils.crc24q(bytes, bytes.length, 0));

    }

    @Override
    public String toString() {
        return "MSG1006{" +
                "messageNumber=" + messageNumber +
                ", stationID=" + stationID +
                ", ITRFyear=" + ITRFyear +
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
                ", AntennaHeigth=" + antennaHeight +
                '}';
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    public int getStationID() {
        return stationID;
    }

    public void setStationID(int stationID) {
        this.stationID = stationID;
    }

    public int getITRFyear() {
        return ITRFyear;
    }

    /**
     * The ITRFyear realization year
     *
     * @param ITRFyear
     */
    public void setITRFyear(int ITRFyear) {
        this.ITRFyear = ITRFyear;
    }

    public boolean isGpsIndicator() {
        return gpsIndicator;
    }

    public void setGpsIndicator(boolean gpsIndicator) {
        this.gpsIndicator = gpsIndicator;
    }

    public boolean isGloIndicator() {
        return gloIndicator;
    }

    public void setGloIndicator(boolean gloIndicator) {
        this.gloIndicator = gloIndicator;
    }

    public boolean isGalIndicator() {
        return galIndicator;
    }

    public void setGalIndicator(boolean galIndicator) {
        this.galIndicator = galIndicator;
    }

    /**
     * false - Real, Physical Reference Station
     * true - Non-Physical or Computed Reference Station
     *
     * @return
     */
    public boolean isReferenceStationIndicator() {
        return referenceStationIndicator;
    }

    public void setReferenceStationIndicator(boolean referenceStationIndicator) {
        this.referenceStationIndicator = referenceStationIndicator;
    }

    public double getECEFX() {
        return ECEFX;
    }

    /**
     * X-coordinate is referenced to ITRF epoch
     *
     * @param ECEFX
     */
    public void setECEFX(double ECEFX) {
        Preconditions.checkArgument(-13743895.3471 < ECEFX && ECEFX < 13743895.3471);
        this.ECEFX = BitUtils.normalize(ECEFX, 4);

    }

    public boolean isOscillator() {
        return oscillator;
    }

    public void setOscillator(boolean oscillator) {
        this.oscillator = oscillator;
    }

    public boolean isReserverd() {
        return reserverd;
    }

    public void setReserverd(boolean reserverd) {
        this.reserverd = reserverd;
    }

    public double getECEFY() {
        return ECEFY;
    }

    /**
     * Y-coordinate is referenced to ITRF epoch
     *
     * @param ECEFY
     */
    public void setECEFY(double ECEFY) {
        Preconditions.checkArgument(-13743895.3471 < ECEFX && ECEFX < 13743895.3471);
        this.ECEFY = BitUtils.normalize(ECEFY, 4);
    }

    public int getQuarterCycle() {
        return quarterCycle;
    }

    public void setQuarterCycle(int quarterCycle) {
        this.quarterCycle = quarterCycle;
    }

    public double getECEFZ() {
        return ECEFZ;
    }

    /**
     * Z-coordinate is referenced to ITRF epoch
     *
     * @param ECEFZ
     */
    public void setECEFZ(double ECEFZ) {
        Preconditions.checkArgument(-13743895.3471 < ECEFX && ECEFX < 13743895.3471);
        this.ECEFZ = BitUtils.normalize(ECEFZ, 4);
    }

    public double getAntennaHeight() {
        return antennaHeight;
    }

    public void setAntennaHeight(double antennaHeight) {
        this.antennaHeight = antennaHeight;
    }
}