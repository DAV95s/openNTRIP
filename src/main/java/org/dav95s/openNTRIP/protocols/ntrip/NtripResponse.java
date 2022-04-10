package org.dav95s.openNTRIP.protocols.ntrip;

public class NtripResponse {
    public static final byte[] OK_MESSAGE = "ICY 200 OK\r\n".getBytes();
    public static final byte[] BAD_PASSWORD = "ERROR - Bad Password\r\n".getBytes();
}
