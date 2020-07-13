package org.adv25.ADVNTRIP.Tools;

public class Msg {
    int nmb;
    byte[] bytes;


    public Msg(int nmb ,byte[] bytes) {
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
