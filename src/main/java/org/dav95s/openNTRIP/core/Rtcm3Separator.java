package org.dav95s.openNTRIP.core;

import org.dav95s.openNTRIP.tools.RTCMStream.Crc24q;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Rtcm3Separator {
    static final private Logger logger = LoggerFactory.getLogger(Rtcm3Separator.class.getName());

    private static final HashSet<Integer> rtcm3messages = null;
    private final byte PREAMBLE = -45;

    private byte[] tail = null; //residual of the previous message

    public ArrayList<Message2> separate(byte[] bytes) {
        ArrayList<Message2> messages = new ArrayList<>();

        if (bytes == null || bytes.length == 0) {
            return messages;
        }

        if (tail == null) {
            if (bytes[0] == PREAMBLE)
                return parse(bytes, messages);
        } else {
            //Concatenation new message with residual of the previous message
            byte[] newBytes = new byte[tail.length + bytes.length];
            System.arraycopy(tail, 0, newBytes, 0, tail.length);
            System.arraycopy(bytes, 0, newBytes, tail.length, bytes.length);
            tail = null;
            return parse(newBytes, messages);
        }

        return messages;
    }

    private ArrayList<Message2> parse(byte[] bytes, ArrayList<Message2> messages) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != PREAMBLE) {
                continue;
            }

            try {
                short length = (short) (((bytes[i + 1] & 0xFF) << 8) | (bytes[i + 2] & 0xFF));
                short msgNumb = (short) (((bytes[i + 3] & 0xFF) << 8) | (bytes[i + 4] & 0xFF));
                length = (short) (length & 0x3FF); //cut off reserved bits
                msgNumb = (short) (msgNumb >> 4);

                if (checkExistsMessageNumber(msgNumb)) {
                    byte[] msg = new byte[length + 6];
                    System.arraycopy(bytes, i, msg, 0, length + 6);
                    messages.add(new Message2(msgNumb, msg));
                    i = i + length + 5; //in the next cycle i will be incremented
                }

            } catch (IndexOutOfBoundsException e) {
                this.tail = new byte[bytes.length - i];
                System.arraycopy(bytes, i, tail, 0, tail.length);
                break;
            }
        }
        return messages;
    }

    protected boolean checkCrs(byte[] bytes) {
        byte[] crs = Crc24q.crc24q(bytes, bytes.length - 3, 0);
        return Arrays.equals(bytes, bytes.length - 3, bytes.length - 1, crs, 0, 2);
    }

    protected boolean checkExistsMessageNumber(int nmb) {
        return (1001 <= nmb && nmb <= 1039) || (1057 <= nmb && nmb <= 1068)
                || (1070 <= nmb && nmb <= 1230) || (4001 <= nmb && nmb <= 4095);
    }
}
