package org.adv25.ADVNTRIP.Tools.Decoders;

import org.adv25.ADVNTRIP.Tools.MessagePack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class RTCM_3X implements IDecoder {
    private static Logger logger = LogManager.getLogger(RTCM_3X.class.getName());

    DecoderType decoderType = DecoderType.RTCM3;

    public MessagePack separate(ByteBuffer bb) throws IOException {
        MessagePack messageList = new MessagePack();
        bb.flip();

        if (bb.limit() == 0)
            return null;

        int preamble, shift, nmb;

        while (bb.hasRemaining()) {
            if (bb.get() != -45)
                continue;

            preamble = bb.position() - 1;
            shift = bb.getShort(preamble + 1) + 6;
            nmb = (bb.getShort(preamble + 3) & 0xffff) >> 4;

            try {
                bb.position(preamble);
                byte[] msg = new byte[shift];
                bb.get(msg, 0, shift);
                messageList.addMessage(nmb, msg);

                bb.position(preamble + shift);

            } catch (IllegalArgumentException | BufferUnderflowException e) {
                throw new IOException();
            }
        }

        if (messageList.isEmpty())
            return null;

        return messageList;
    }

    @Override
    public DecoderType getType() {
        return decoderType;
    }
}
