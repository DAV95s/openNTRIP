package org.dav95s.openNTRIP.commons;

public class Message {
    public final String name;
    public final byte[] bytes;


    public Message(String name, byte[] bytes) {
        this.name = name;
        this.bytes = bytes;
    }

    public Message(short name, byte[] bytes) {
        this.name = String.valueOf(name);
        this.bytes = bytes;
    }

}
