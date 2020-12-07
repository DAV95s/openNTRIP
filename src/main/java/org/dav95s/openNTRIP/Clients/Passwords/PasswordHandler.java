package org.dav95s.openNTRIP.Clients.Passwords;

public interface PasswordHandler {
    boolean compare(String fromDB, String fromUser);
    String hash(String rawPassword);
}
