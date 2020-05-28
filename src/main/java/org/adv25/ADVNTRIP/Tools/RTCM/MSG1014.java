package org.adv25.ADVNTRIP.Tools.RTCM;

public class MSG1014 extends RTCM {
    private int messageNumber;
    private int NetworkID;
    private int SubnetworkID;
    private int NumberAuxiliaryStationsTransmitted;
    private int MasterStation;
    private int AuxiliaryReferenceStationID;
    private int AuxMasterDeltaLatitude;
    private int AuxMasterDeltaLongitude;
    private int AuxMasterDeltaHeight;

    public MSG1014(byte[] msg) {
        super.rawMsg = msg;
        super.setToBinaryBuffer(msg);

        messageNumber = toUnsignedInt(getBinary(16, 12));
        NetworkID = toUnsignedInt(getBinary(28, 8));
        SubnetworkID = toUnsignedInt(getBinary(36, 4));
        NumberAuxiliaryStationsTransmitted = toUnsignedInt(getBinary(40,5));
        MasterStation = toUnsignedInt(getBinary(45, 12));
        AuxiliaryReferenceStationID = toUnsignedInt(getBinary(57, 12));
        AuxMasterDeltaLatitude = toSignedInt(getBinary(69, 20));
        AuxMasterDeltaLongitude = toSignedInt(getBinary(89, 21));
        AuxMasterDeltaHeight = toSignedInt(getBinary(110, 23));
    }
}
