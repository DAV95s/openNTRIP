import org.dav95s.openNTRIP.Clients.Passwords.BCrypt;
import org.dav95s.openNTRIP.Clients.Passwords.None;
import org.dav95s.openNTRIP.Clients.Passwords.SHA256;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestPasswordAlgorithm {

    @Test
    public void none() {
        None none = new None();

        //compare
        assertTrue(none.compare("123123123", "123123123"));
        assertTrue(none.compare("", ""));
        assertFalse(none.compare("", "!2@@#31"));
        assertFalse(none.compare(null, "11313"));
        assertFalse(none.compare(null, null));
        assertFalse(none.compare("123131", null));

        //hash o_O
        assertEquals("123123123", none.hash("123123123"));
        assertEquals("", none.hash(""));
        assertFalse("123".equals(none.hash("123123123")));
    }

    @Test
    public void sha256() {
        //compare
        SHA256 sha256 = new SHA256();
        assertTrue(sha256.compare("22B72A2B6A95086A9AF6EF8B052CCB342C733B350382EE19B07BE76C1DF9EF1D", "tttwwq2"));
        assertTrue(sha256.compare("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", "hello"));
        assertTrue(sha256.compare("032f61c6fa0d7cabfe2cf4c498619934d3fa0ccc8b9d3b589e1a4a4f7cebd177", "totototototoq"));

        assertFalse(sha256.compare("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", "hallo"));
        assertFalse(sha256.compare("", "!2@@#31"));
        assertFalse(sha256.compare(null, "11313"));
        assertFalse(sha256.compare(null, null));
        assertFalse(sha256.compare("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", null));

        //hash
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", sha256.hash("hello"));

    }

    @Test(expected = IllegalArgumentException.class)
    public void sha256exception() {
        SHA256 sha256 = new SHA256();
        sha256.hash("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void sha256null() {
        SHA256 sha256 = new SHA256();
        sha256.hash(null);
    }

    @Test
    public void BCrypt() {
        BCrypt bCrypt = new BCrypt();
        //compare
        assertTrue(bCrypt.compare("$2y$12$P07ACb3XmtIvHJXrzhTULeOUAF1Q0zh3STfPpjZTu9z8JVD4580Uu", "password"));
        assertTrue(bCrypt.compare("$2a$10$eGYNvSMbYMw2ks818r6HdOlqiPRR1ETqTATsdxWzoO8aug7sxx.mC", "ffaqq"));
        assertTrue(bCrypt.compare("$2a$04$YFzshOrWKAZ8iu4gW0/NhOo9B.A1Lnn8k6uzXivraiAKv66ByW1q6", "rqrqrqr1144"));
        assertTrue(bCrypt.compare("$2b$10$uayG5HrmJSRK7.gDW9QX7.e9RYM0lwlUbzieDbVCcVrKJ14XFHwx6", "ttt4444ccz"));
        assertTrue(bCrypt.compare("$2a$04$AQgn0ch7ZWrrfP8Opq/k8.xazPZcqyvbz/8xf52W69utG9onGhi16", "yyytttqqee4444"));

        assertFalse(bCrypt.compare("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", "hallo"));
        assertFalse(bCrypt.compare("", "!2@@#31"));
        assertFalse(bCrypt.compare(null, "11313"));
        assertFalse(bCrypt.compare(null, null));
        assertFalse(bCrypt.compare("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", null));

        //hash
        String example = "12asdfasfqwrqwrqwr";
        String hash = bCrypt.hash("12asdfasfqwrqwrqwr");
        assertTrue(bCrypt.compare(hash, example));
        assertFalse(bCrypt.compare(hash, "123123123123"));
    }
}
