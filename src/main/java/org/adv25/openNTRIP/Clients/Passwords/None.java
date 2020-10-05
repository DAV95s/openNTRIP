package org.adv25.openNTRIP.Clients.Passwords;

import java.util.logging.Level;
import java.util.logging.Logger;

public class None implements PasswordHandler {
    public static final Logger log = Logger.getLogger(None.class.getName());

    @Override
    public boolean Compare(String fromDB, String fromUser) {
        if (fromDB == null) {
            log.log(Level.WARNING, "Password fromDB is NULL");
            return false;
        }

        if (fromDB == ""){
            log.log(Level.WARNING, "Password fromDB is empty");
            return false;
        }


        return fromDB.equals(fromUser);

    }
}
