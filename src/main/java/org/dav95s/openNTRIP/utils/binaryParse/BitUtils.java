package org.dav95s.openNTRIP.utils.binaryParse;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class BitUtils {

    String buffer;

    int pointer = 0;

    public BitUtils(byte[] arr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : arr) {
            sb.append(toBinaryString(b));
        }
        buffer = sb.toString();
    }

    public BitUtils(String bitString) {
        buffer = bitString;
    }

    public BitUtils() {
        buffer = "";
    }

    public void setPointer(int i) {
        pointer = i;
    }

    public void clear() {
        buffer = "";
    }

    private String toBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }

    public boolean getBoolean() {
        int pointer = this.pointer;
        this.pointer += 1;

        return buffer.substring(pointer, this.pointer).equals("1");
    }

    public void setBoolean(boolean data) {
        buffer += data ? "1" : "0";
    }

    public int getSignedInt(int length) {
        int pointer = this.pointer;
        this.pointer += length;

        return toSignedInt(buffer.substring(pointer, this.pointer));
    }

    public long getSignedLong(int length) {
        int pointer = this.pointer;
        this.pointer += length;

        return toSignedLong(buffer.substring(pointer, this.pointer));
    }

    public long getUnsignedLong(int length) {
        Preconditions.checkArgument(length < 64);
        int pointer = this.pointer;
        this.pointer += length;

        return Long.parseLong(buffer.substring(pointer, this.pointer), 2);
    }

    public int getUnsignedInt(int length) {
        Preconditions.checkArgument(length < 32);
        int pointer = this.pointer;
        this.pointer += length;

        return Integer.parseInt(buffer.substring(pointer, this.pointer), 2);
    }

    public void setInt(int data, int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = length - 1; i >= 0; i--) {
            int mask = 1 << i;
            stringBuilder.append((data & mask) != 0 ? "1" : "0");
        }
        buffer += stringBuilder.toString();
    }

    public void setLong(long data, int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = length - 1; i >= 0; i--) {
            long mask = 1L << i;
            stringBuilder.append((data & mask) != 0 ? "1" : "0");
        }
        buffer += stringBuilder.toString();
    }

    public void setString(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.ISO_8859_1);
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(toBinaryString(b));
        }
        buffer += stringBuilder.toString();
    }

    public void setBitString(String str) {
        buffer += str;
    }

    public String getString(int length) {
        int pointer = this.pointer;
        this.pointer += length;
        byte[] bString = getByteByString(buffer.substring(pointer, this.pointer));
        return new String(bString, StandardCharsets.ISO_8859_1);
    }

    public byte[] getArrayFromBuffer() {
        return getByteByString(buffer);
    }

    private long toSignedLong(String bits) {
        int position = 0;
        long answer = 0;

        for (int i = bits.length() - 1; i > 0; i--) {
            if (bits.charAt(i) == '1')
                answer += (long) Math.pow(2, position);
            position++;
        }
        if (bits.charAt(0) == '1')
            answer += (long) Math.pow(2, position) * -1;
        return answer;
    }

    public int toSignedInt(String bits) {
        int position = 0;
        int answer = 0;

        for (int i = bits.length() - 1; i > 0; i--) {
            if (bits.charAt(i) == '1')
                answer += Math.pow(2, position);
            position++;
        }
        if (bits.charAt(0) == '1')
            answer += Math.pow(2, position) * -1;
        return answer;
    }

    private byte[] getByteByString(String binaryString) {
        Iterable<String> iterable = Splitter.fixedLength(8).split(binaryString);
        byte[] ret = new byte[Iterables.size(iterable)];
        Iterator<String> iterator = iterable.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Integer byteAsInt = Integer.parseInt(iterator.next(), 2);
            ret[i] = byteAsInt.byteValue();
            i++;
        }
        return ret;
    }

    public byte[] getByteArray() {
        Iterable<String> iterable = Splitter.fixedLength(8).split(buffer);
        byte[] ret = new byte[Iterables.size(iterable)];
        Iterator<String> iterator = iterable.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Integer byteAsInt = Integer.parseInt(iterator.next().toString(), 2);
            ret[i] = byteAsInt.byteValue();
            i++;
        }
        int shift = buffer.length() % 8;
        ret[i - 1] = (byte) (ret[i - 1] << (8 - shift));

        return ret;
    }

    @Override
    public String toString() {
        return buffer;
    }

    public String toString(char splitter) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < buffer.length(); i += 8) {
            builder.append(buffer, i, i + 8);
            builder.append(splitter);
        }
        return builder.toString();
    }


}
