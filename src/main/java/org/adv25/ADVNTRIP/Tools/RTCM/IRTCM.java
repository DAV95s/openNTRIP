package org.adv25.ADVNTRIP.Tools.RTCM;

public interface IRTCM {
    IRTCM parse(byte[] msg);

    void Write();

    String lookup(String field);

    String toString();

    String getJson();
}
