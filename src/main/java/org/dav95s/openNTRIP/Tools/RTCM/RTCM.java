package org.dav95s.openNTRIP.Tools.RTCM;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;

public abstract class RTCM {

    protected byte[] rawMsg;

    protected String binaryBuffer = "";

    protected final static char BIT1 = '1';

    protected ByteBuffer buffer = ByteBuffer.allocate(1024);


    protected void write(int numb, int bitCount) {
        if (bitCount == 32) {
            buffer.putInt(numb);
            return;
        }
    }

    @Deprecated
    protected long toSignedLong(String bits) {
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

    @Deprecated
    protected int toSignedInt(String bits) {
        int position = 0;
        int answer = 0;

        for (int i = bits.length() - 1; i > 0; i--) {
            if (bits.charAt(i) == BIT1)
                answer += Math.pow(2, position);
            position++;
        }
        if (bits.charAt(0) == BIT1)
            answer += Math.pow(2, position) * -1;
        return answer;
    }

    @Deprecated
    protected int toUnsignedInt(String bits) {
        int n = bits.length();
        int decimal = 0;
        for (int d = 0; d < n; d++) {

            decimal = decimal << 1;

            if (bits.charAt(d) == '1') {
                decimal = decimal | 1;
            }
        }
        return decimal;
    }

    @Deprecated
    protected long toUnsignedLong(String bits) {
        long n = bits.length();
        long decimal = 0;
        for (int d = 0; d < n; d++) {

            decimal = decimal << 1;

            if (bits.charAt(d) == '1') {
                decimal = decimal | 1;
            }
        }
        return decimal;
    }

    @Deprecated
    protected int toIntS(String bits) {
        int response = toUnsignedInt(bits.substring(1));
        if (bits.charAt(0) == BIT1)
            response *= -1;

        return response;
    }

    @Deprecated
    protected String customFormat(String pattern, double value) {
        return new DecimalFormat(pattern).format(value);
    }

    @Deprecated
    public String toBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }

    public String toBinaryString(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(toBinaryString(b));
            sb.append(" ");
        }
        return sb.toString();
    }

    protected void setToBinaryBuffer(byte[] bytes) {
        for (int i = 1; i < bytes.length; i++) {
            binaryBuffer += toBinaryString(bytes[i]);
        }
    }

    protected String getBits(int position, int length) {
        String response = "";
        for (int i = position; i < position + length; i++) {
            response += binaryBuffer.charAt(i);
        }
        return response;
    }

    @Deprecated
    public static short getShortFromArray(byte[] array, int index) {
        return (short) (((array[index] & 0xFF) << 8) | (array[++index] & 0xFF));
    }


}

