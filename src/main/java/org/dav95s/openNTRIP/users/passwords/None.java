package org.dav95s.openNTRIP.users.passwords;



public class None implements PasswordHandler {


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


        return rawPassword;
    }
}
