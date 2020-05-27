package org.adv25.ADVNTRIP.Clients.Passwords;

public interface PasswordHandler {
    boolean Compare(String fromDB, String fromUser);
}
