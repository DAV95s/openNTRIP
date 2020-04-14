package org.adv25.ADVNTRIP.Tools.RTCM;

public class RTCM {

    public final static char BIT1 = '1';

    public static long toSignedInt(String bits) {
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

}

