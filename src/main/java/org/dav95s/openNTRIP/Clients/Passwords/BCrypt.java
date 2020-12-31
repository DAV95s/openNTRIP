package org.dav95s.openNTRIP.Clients.Passwords;


import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import static at.favre.lib.crypto.bcrypt.BCrypt.*;

public class BCrypt implements PasswordHandler {
    public static final Logger log = Logger.getLogger(SHA256.class.getName());

    @Override
    public boolean compare(String fromDB, String fromUser) {
        if (fromDB == null) {
            log.log(Level.WARNING, "Password fromDB is NULL");
            return false;
        }

        if (fromUser == null) {
            log.log(Level.WARNING, "User password is NULL");
            return false;
        }

        if (fromDB == "") {
            log.log(Level.WARNING, "Password fromDB is empty");
            return false;
        }

        Result rr = verifyer().verify(fromUser.getBytes(), fromDB.getBytes());

        if (rr.verified)
            return true;

        return false;
    }

    //todo need write
    @Override
    public String hash(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Input null string!");
        }
        if (rawPassword == "")
            throw new IllegalArgumentException("Can't hash of empty string!");

        byte[] rawResult = at.favre.lib.crypto.bcrypt.BCrypt.withDefaults().hash(12, rawPassword.getBytes());
        return new String(rawResult);
    }


}
