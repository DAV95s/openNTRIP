import org.dav95s.openNTRIP.Clients.Passwords.BCrypt;
import org.dav95s.openNTRIP.Clients.Passwords.None;
import org.dav95s.openNTRIP.Clients.Passwords.SHA256;
import org.junit.Assert;
import org.junit.Test;

public class TestPasswordAlgorithm {

    @Test
    public void none() {
        None none = new None();

        Assert.assertTrue(none.Compare("hibernate.cfg.xml", "hibernate.cfg.xml"));
        
        Assert.assertFalse(none.Compare("", ""));
        Assert.assertFalse(none.Compare("", "!2@@#31"));
        Assert.assertFalse(none.Compare(null, "11313"));
        Assert.assertFalse(none.Compare(null, null));
        Assert.assertFalse(none.Compare("123131", null));

    }

    @Test
    public void sha256() {
        SHA256 sha256 = new SHA256();
        Assert.assertTrue(sha256.Compare("22B72A2B6A95086A9AF6EF8B052CCB342C733B350382EE19B07BE76C1DF9EF1D", "tttwwq2"));
        Assert.assertTrue(sha256.Compare("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", "hello"));
        Assert.assertTrue(sha256.Compare("032f61c6fa0d7cabfe2cf4c498619934d3fa0ccc8b9d3b589e1a4a4f7cebd177", "totototototoq"));

        Assert.assertFalse(sha256.Compare("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", "hallo"));
        Assert.assertFalse(sha256.Compare("", "!2@@#31"));
        Assert.assertFalse(sha256.Compare(null, "11313"));
        Assert.assertFalse(sha256.Compare(null, null));
        Assert.assertFalse(sha256.Compare("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", null));
    }

    @Test
    public void BCrypt() {
        BCrypt bCrypt = new BCrypt();

        Assert.assertTrue(bCrypt.Compare("$2y$12$P07ACb3XmtIvHJXrzhTULeOUAF1Q0zh3STfPpjZTu9z8JVD4580Uu", "password"));
        Assert.assertTrue(bCrypt.Compare("$2a$10$eGYNvSMbYMw2ks818r6HdOlqiPRR1ETqTATsdxWzoO8aug7sxx.mC", "ffaqq"));
        Assert.assertTrue(bCrypt.Compare("$2a$04$YFzshOrWKAZ8iu4gW0/NhOo9B.A1Lnn8k6uzXivraiAKv66ByW1q6", "rqrqrqr1144"));
        Assert.assertTrue(bCrypt.Compare("$2b$10$uayG5HrmJSRK7.gDW9QX7.e9RYM0lwlUbzieDbVCcVrKJ14XFHwx6", "ttt4444ccz"));
        Assert.assertTrue(bCrypt.Compare("$2a$04$AQgn0ch7ZWrrfP8Opq/k8.xazPZcqyvbz/8xf52W69utG9onGhi16", "yyytttqqee4444"));

        Assert.assertFalse(bCrypt.Compare("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", "hallo"));
        Assert.assertFalse(bCrypt.Compare("", "!2@@#31"));
        Assert.assertFalse(bCrypt.Compare(null, "11313"));
        Assert.assertFalse(bCrypt.Compare(null, null));
        Assert.assertFalse(bCrypt.Compare("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", null));
    }
}
