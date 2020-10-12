package org.adv25.openNTRIP.Tools.Decoders;

import org.adv25.openNTRIP.Tools.MessagePack;

import java.io.IOException;
import java.nio.ByteBuffer;

public class RAW implements IDecoder {

    @Override
    public MessagePack separate(ByteBuffer bb)  {
        MessagePack messagePack = new MessagePack();
        byte[] bytes = new byte[bb.remaining()];
        bb.get(bytes);
        messagePack.addMessage(0, bytes);
        return messagePack;
    }

    @Override
    public DecoderType getType() {
        return DecoderType.RAW;
    }
}
