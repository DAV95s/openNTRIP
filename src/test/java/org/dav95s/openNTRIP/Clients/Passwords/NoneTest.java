package org.dav95s.openNTRIP.Clients.Passwords;

import junit.framework.TestCase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NoneTest extends TestCase {

    public void testCompare() {
        None none = new None();

        //compare
        assertTrue(none.compare("123123123", "123123123"));
        assertTrue(none.compare("", ""));
        assertFalse(none.compare("", "!2@@#31"));
        assertFalse(none.compare(null, "11313"));
        assertFalse(none.compare(null, null));
        assertFalse(none.compare("123131", null));
    }

    public void testHash() {
        None none = new None();
        //hash o_O
        assertEquals("123123123", none.hash("123123123"));
        assertEquals("", none.hash(""));
        assertFalse("123".equals(none.hash("123123123")));
    }
}