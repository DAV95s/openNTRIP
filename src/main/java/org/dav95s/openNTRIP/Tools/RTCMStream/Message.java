package org.dav95s.openNTRIP.Tools.RTCMStream;

public class Message {
    int nmb;
    byte[] bytes;

    public Message(int nmb , byte[] bytes) {
        this.bytes = bytes;
        this.nmb = nmb;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getNmb() {
        return nmb;
    }
}
