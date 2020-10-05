package org.adv25.openNTRIP.Clients.Passwords;

public interface PasswordHandler {
    boolean Compare(String fromDB, String fromUser);
}
