package org.dav95s.openNTRIP.Tools.Decoders;

import org.dav95s.openNTRIP.Tools.RTCMStream.MessagesPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class DecoderRTCM3 implements IDecoder {
    private static final Logger logger = LoggerFactory.getLogger(DecoderRTCM3.class.getName());

    private final byte RTCM_PREAMBLE = -45;

    private ByteBuffer residue;

    DecoderType decoderType = DecoderType.RTCM3;

    @Override
    public DecoderType getType() {
        return decoderType;
    }

    public MessagesPack separate(ByteBuffer bb) throws IllegalArgumentException {
        MessagesPack messagePack = new MessagesPack();
        ByteBuffer buffer = bb;

        if (buffer.limit() == 0)
            return messagePack;

        int preamble, shift, nmb;

        if (residue != null) {
            buffer = concatByteBuffer(residue, buffer);
            residue = null;
        }

        if (buffer.get(0) != RTCM_PREAMBLE) {
            errorCounter();
            return messagePack;
        }

        preamble = 0;

        while (buffer.hasRemaining()) {
            shift = buffer.getShort(preamble + 1) + 6 & 0x3FF; // to zero first 6(reserved) bits;
            nmb = (buffer.getShort(preamble + 3) & 0xffff) >> 4;
            buffer.position(preamble);

            if (!checkExistingMessageNumber(nmb)) {
                break;
            }

            try {
                byte[] msg = new byte[shift];
                buffer.get(msg, 0, shift);
                messagePack.addMessage(nmb, msg);

                if (preamble + shift == buffer.limit())
                    break;

                if (buffer.get(preamble + shift) == RTCM_PREAMBLE) {
                    preamble = preamble + shift;
                } else {
                    break;
                }

                buffer.position(preamble);

            } catch (BufferUnderflowException e) {
                residue = ByteBuffer.allocate(buffer.capacity());
                buffer.rewind();
                residue.put(buffer);
                residue.flip();
//                residue = ByteBuffer.allocate(buffer.remaining());
//                residue.put(buffer);
//                residue.flip();
            }
        }

        return messagePack;
    }

    private boolean checkExistingMessageNumber(int nmb) {
        return (1001 <= nmb && nmb <= 1039) || (1057 <= nmb && nmb <= 1068)
                || (1071 <= nmb && nmb <= 1230) || (4001 <= nmb && nmb <= 4095);
    }

    private int errorCount = 0;

    private void errorCounter() {
        logger.error("Error counter " + errorCount);
        errorCount++;
        if (errorCount > 10)
            throw new IllegalArgumentException("NO RTCM3 DATA!");
    }

    public ByteBuffer concatByteBuffer(ByteBuffer residue, ByteBuffer buffer) {
        ByteBuffer b3 = ByteBuffer.allocate(residue.limit() + buffer.limit());
        b3.put(residue);
        b3.put(buffer);
        b3.flip();
        return b3;
    }
}
