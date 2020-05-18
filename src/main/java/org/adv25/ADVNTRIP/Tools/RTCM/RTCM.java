package org.adv25.ADVNTRIP.Tools.RTCM;

public abstract class RTCM {

    protected byte[] rawMsg;

    protected  String binaryBuffer = "";

    protected final static char BIT1 = '1';

    protected long toSignedInt(String bits) {
        int position = 0;
        long answer = 0;

        for (int i = bits.length() - 1; i > 0; i--) {
            if (bits.charAt(i) == BIT1)
                answer += (long) Math.pow(2, position);
            position++;
        }
        if (bits.charAt(0) == BIT1)
            answer += (long) Math.pow(2, position) * -1;
        return answer;
    }

    protected static String toBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }
}

