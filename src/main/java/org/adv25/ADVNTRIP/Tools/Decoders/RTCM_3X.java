package org.adv25.ADVNTRIP.Tools.Decoders;

import org.adv25.ADVNTRIP.Tools.Msg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class RTCM_3X {
    private static Logger logger = LogManager.getLogger(RTCM_3X.class.getName());

    public ArrayList<Msg> separate(ByteBuffer bb) {
        ArrayList<Msg> list = new ArrayList<>();

        if (bb.limit() == 0)
            return list;

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
                list.add(new Msg(nmb, msg));

                bb.position(preamble + shift);

            } catch (IllegalArgumentException | BufferUnderflowException e) {
                return list;
            }
        }
        return list;
    }

    public ArrayList<Msg> separate(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return separate(bb);
    }
}
