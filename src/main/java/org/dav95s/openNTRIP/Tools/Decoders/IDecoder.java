package org.dav95s.openNTRIP.Tools.Decoders;

import org.dav95s.openNTRIP.Tools.MessagePack;

import java.nio.ByteBuffer;

public interface IDecoder {

    MessagePack separate(ByteBuffer bb) throws IllegalArgumentException;

    DecoderType getType();

}
