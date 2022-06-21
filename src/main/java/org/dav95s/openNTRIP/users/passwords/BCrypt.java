package org.dav95s.openNTRIP.users.passwords;

import static at.favre.lib.crypto.bcrypt.BCrypt.verifyer;

public class BCrypt {


    public boolean compare(String DBPassword, String UserPassword) {
        if (DBPassword == null || UserPassword == null) {
            return false;
        }
        DBPassword = DBPassword.trim();
        UserPassword = UserPassword.trim();
//        if (DBPassword.isEmpty() && UserPassword.isEmpty()) {
//            return true;
//        }

        return verifyer().verify(UserPassword.getBytes(), DBPassword.getBytes()).verified;
    }

    public String hash(String rawPassword, int BCryptRounds) {
        if (rawPassword == null) {
            throw new NullPointerException("Input string is null");
        }

        byte[] rawResult = at.favre.lib.crypto.bcrypt.BCrypt.withDefaults().hash(BCryptRounds, rawPassword.getBytes());
        return new String(rawResult);
    }


    public String hash(String rawPassword) {
        return hash(rawPassword, 12);
    }

}