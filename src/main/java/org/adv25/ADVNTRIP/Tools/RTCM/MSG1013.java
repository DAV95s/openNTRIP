package org.adv25.ADVNTRIP.Tools.RTCM;

public class MSG1013 extends RTCM {
    private int messageNumber;
    private int stationID;
    private int MJD; //Modified Julian Day Number
    private int UTC; //Seconds of Day UTC
    private int LeapSeconds; // Leap Seconds, GPS-UTC
    private Message[] list;


    public MSG1013(byte[] msg) {
        super.rawMsg = msg;
        super.setToBinaryBuffer(msg);

        messageNumber = toUnsignedInt(getBinary(16, 12));
        stationID = toUnsignedInt(getBinary(28, 12));
        MJD = toUnsignedInt(getBinary(40, 16));
        UTC = toUnsignedInt(getBinary(56, 17));
        int messageCounter = toUnsignedInt(getBinary(73, 5));
        list = new Message[messageCounter];
        LeapSeconds = toUnsignedInt(getBinary(78, 8));//86

        for (int i = 0; i < messageCounter; i++) {
            int shift = i * 29;
            Message m = new Message();

            m.setMessageID(toUnsignedInt(getBinary(89 + shift, 12)));
            m.setSyncFlag(toUnsignedInt(getBinary(101 + shift, 1)));
            m.setTransmissionInterval(toUnsignedInt(getBinary(102 + shift, 16)));

            list[i] = m;
        }

    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public int getStationID() {
        return stationID;
    }

    public int getMJD() {
        return MJD;
    }

    public int getUTC() {
        return UTC;
    }

    public int getLeapSeconds() {
        return LeapSeconds;
    }

    public Message[] getList() {
        return list;
    }

    public class Message {
        private int MessageID;
        private int SyncFlag;
        private int TransmissionInterval;

        public int getMessageID() {
            return MessageID;
        }

        public void setMessageID(int messageID) {
            MessageID = messageID;
        }

        public int getSyncFlag() {
            return SyncFlag;
        }

        public void setSyncFlag(int syncFlag) {
            SyncFlag = syncFlag;
        }

        public int getTransmissionInterval() {
            return TransmissionInterval;
        }

        public void setTransmissionInterval(int transmissionInterval) {
            TransmissionInterval = transmissionInterval;
        }

    }
}


