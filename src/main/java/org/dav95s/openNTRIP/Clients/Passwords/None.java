package org.dav95s.openNTRIP.Clients.Passwords;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class None implements PasswordHandler {
    final static private Logger logger = LogManager.getLogger(None.class.getName());

    @Override
    public boolean compare(String fromDB, String fromUser) {
        if (fromDB == null) {
            return false;
        }

        return fromDB.equals(fromUser);
    }

    @Override
    public String hash(String rawPassword) throws IllegalArgumentException {
        if (rawPassword == null)
            throw new IllegalArgumentException("Can't hash of null!");

        if (rawPassword == "")
            throw new IllegalArgumentException("Can't hash of empty string!");

        return rawPassword;
    }
}
