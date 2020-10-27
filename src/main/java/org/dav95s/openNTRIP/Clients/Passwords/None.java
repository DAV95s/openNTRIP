package org.dav95s.openNTRIP.Clients.Passwords;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class None implements PasswordHandler {
    final static private Logger logger = LogManager.getLogger(None.class.getName());

    @Override
    public boolean Compare(String fromDB, String fromUser) {
        if (fromDB == null) {
            return false;
        }

        return fromDB.equals(fromUser);
    }
}
