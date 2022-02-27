package org.dav95s.openNTRIP.users.passwords;

import java.security.MessageDigest;

public class SHA256 implements PasswordHandler {


    @Override
    public boolean compare(String fromDB, String fromUser) {
        if (fromDB == null || fromUser == null) {
            return false;
        }

        String userHash = sha256(fromUser);
        return fromDB.toLowerCase().equals(userHash);

    }

    @Override
    public String hash(String rawPassword) {
        if (rawPassword == null)
            throw new IllegalArgumentException("Input string is null");

        return sha256(rawPassword);
    }

    private String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
