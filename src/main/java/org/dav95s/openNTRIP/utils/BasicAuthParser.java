package org.dav95s.openNTRIP.utils;

import java.util.Base64;

public class BasicAuthParser {
    public final String account;
    public final String password;

    public BasicAuthParser(String authorization) {
        String[] split = authorization.split(" ", 2);
        if (split.length != 2 || !split[0].equals("Basic")) {
            throw new IllegalArgumentException();
        }

        byte[] accPassBytes = Base64.getDecoder().decode(split[1]);
        String accPass = new String(accPassBytes);
        split = accPass.split(":");

        if (split.length == 1) {
            this.account = split[0];
            this.password = "";
        } else if (split.length == 2) {
            this.account = split[0];
            this.password = split[1];
        } else {
            throw new IllegalArgumentException();
        }
    }
}
