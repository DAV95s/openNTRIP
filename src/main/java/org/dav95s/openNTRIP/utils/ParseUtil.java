package org.dav95s.openNTRIP.utils;

public class ParseUtil {
    public static int parseInt(String intProperty) {
        try {
            return Integer.parseInt(intProperty);
        } catch (NumberFormatException nfe) {
            throw new RuntimeException(intProperty + " not a number. " + nfe.getMessage());
        }
    }

    public static long parseLong(String longProperty) {
        try {
            return Long.parseLong(longProperty);
        } catch (NumberFormatException nfe) {
            throw new RuntimeException(longProperty + " not a number. " + nfe.getMessage());
        }
    }
}
