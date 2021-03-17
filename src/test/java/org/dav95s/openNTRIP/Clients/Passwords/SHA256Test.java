package org.dav95s.openNTRIP.Clients.Passwords;

import junit.framework.TestCase;
import org.junit.Test;

public class SHA256Test extends TestCase {

    public void testCompare() {

        SHA256 sha256 = new SHA256();
        assertTrue(sha256.compare("22B72A2B6A95086A9AF6EF8B052CCB342C733B350382EE19B07BE76C1DF9EF1D", "tttwwq2"));
        assertTrue(sha256.compare("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", "hello"));
        assertTrue(sha256.compare("032f61c6fa0d7cabfe2cf4c498619934d3fa0ccc8b9d3b589e1a4a4f7cebd177", "totototototoq"));

        assertFalse(sha256.compare("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", "hallo"));
        assertFalse(sha256.compare("", "!2@@#31"));
        assertFalse(sha256.compare(null, "11313"));
        assertFalse(sha256.compare(null, null));
        assertFalse(sha256.compare("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", null));


    }

    public void testHash() {
        SHA256 sha256 = new SHA256();
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


}