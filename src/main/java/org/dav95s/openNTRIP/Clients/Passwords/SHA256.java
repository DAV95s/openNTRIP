package org.dav95s.openNTRIP.Clients.Passwords;



import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SHA256 implements PasswordHandler {
    public static final Logger log = Logger.getLogger(SHA256.class.getName());

    @Override
    public boolean compare(String fromDB, String fromUser) {
        if (fromDB == null) {
            log.log(Level.WARNING, "Password fromDB is NULL");
            return false;
        }

        if (fromUser == null) {
            log.log(Level.WARNING, "User password is NULL");
            return false;
        }

        String userHash = sha256(fromUser);
        return fromDB.toLowerCase().equals(userHash);

    }

    @Override
    public String hash(String rawPassword) {
        if (rawPassword == null)
            throw new IllegalArgumentException("Can't hash of null object!");

        return sha256(rawPassword);
    }

    private static String sha256(String base) {
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
