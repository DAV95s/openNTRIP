package org.dav95s.openNTRIP.Tools.Decoders;

import org.dav95s.openNTRIP.Tools.RTCMStream.MessagesPack;

import java.nio.ByteBuffer;

public class RAW implements IDecoder {

    @Override
    public MessagesPack separate(ByteBuffer bb)  {
        MessagesPack messagePack = new MessagesPack();
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
