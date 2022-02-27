package org.dav95s.openNTRIP.users.passwords;

import org.junit.Test;

import static org.junit.Assert.*;


public class NoneTest {
    @Test
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

    @Test
    public void testHash() {
        None none = new None();

        assertEquals("123123123", none.hash("123123123"));
        assertEquals("", none.hash(""));
        assertNotEquals("123", none.hash("123123123"));
    }
}