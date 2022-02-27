package org.dav95s.openNTRIP.utils.binaryParse;

public class Normalize {
    private static final int[] pow10 = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000};

    public static double normalize(double nmb, int scale) {
        return (double) Math.round(nmb * pow10[scale]) / pow10[scale];
    }
}
