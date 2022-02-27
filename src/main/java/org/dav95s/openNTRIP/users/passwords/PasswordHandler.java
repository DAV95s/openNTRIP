package org.dav95s.openNTRIP.users.passwords;

public interface PasswordHandler {
    boolean compare(String fromDB, String fromUser);
    String hash(String rawPassword);
}
