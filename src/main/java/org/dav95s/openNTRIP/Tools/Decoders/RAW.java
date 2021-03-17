package org.dav95s.openNTRIP.Tools.Decoders;

import org.dav95s.openNTRIP.Tools.RTCMStream.MessagePack;

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
