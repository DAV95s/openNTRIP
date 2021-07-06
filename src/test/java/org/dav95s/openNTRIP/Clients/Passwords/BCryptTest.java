package org.dav95s.openNTRIP.Clients.Passwords;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BCryptTest {

    @Test
    public void testCompare() {
        BCrypt bCrypt = new BCrypt();

        assertTrue(bCrypt.compare("$2y$12$P07ACb3XmtIvHJXrzhTULeOUAF1Q0zh3STfPpjZTu9z8JVD4580Uu", "password"));
        assertTrue(bCrypt.compare("$2a$10$eGYNvSMbYMw2ks818r6HdOlqiPRR1ETqTATsdxWzoO8aug7sxx.mC", "ffaqq"));
        assertTrue(bCrypt.compare("$2a$04$YFzshOrWKAZ8iu4gW0/NhOo9B.A1Lnn8k6uzXivraiAKv66ByW1q6", "rqrqrqr1144"));
        assertTrue(bCrypt.compare("$2b$10$uayG5HrmJSRK7.gDW9QX7.e9RYM0lwlUbzieDbVCcVrKJ14XFHwx6", "ttt4444ccz"));
        assertTrue(bCrypt.compare("$2a$04$AQgn0ch7ZWrrfP8Opq/k8.xazPZcqyvbz/8xf52W69utG9onGhi16", "yyytttqqee4444"));
        assertTrue(bCrypt.compare("$2y$12$8TkvffyD05BaK7KGKNDTD.AdvYKs0W6dfoWGvQUmbZy.9kNf0jze6", ""));


        assertFalse(bCrypt.compare("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", "hallo"));
        assertFalse(bCrypt.compare("$2y$12$8TkvffyD05BaK7KGKNDTD.AdvYKs0W6dfoWGvQUmbZy.9kNf0jze6", "!2@@#31"));
        assertFalse(bCrypt.compare(null, "11313"));
        assertFalse(bCrypt.compare(null, null));
        assertFalse(bCrypt.compare("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", null));

    }

    @Test
    public void testHash() {
        BCrypt bCrypt = new BCrypt();

        String example = "12asdfasfqwrqwrqwr";
        String hash = bCrypt.hash("12asdfasfqwrqwrqwr");
        assertTrue(bCrypt.compare(hash, example));
        assertFalse(bCrypt.compare(hash, "123123123123"));
    }

}