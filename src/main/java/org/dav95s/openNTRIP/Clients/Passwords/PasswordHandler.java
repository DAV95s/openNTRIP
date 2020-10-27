package org.dav95s.openNTRIP.Clients.Passwords;

public interface PasswordHandler {
    boolean Compare(String fromDB, String fromUser);
}
