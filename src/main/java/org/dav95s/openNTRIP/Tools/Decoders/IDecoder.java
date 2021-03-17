package org.dav95s.openNTRIP.Tools.Decoders;

import org.dav95s.openNTRIP.Tools.RTCMStream.MessagePack;

import java.nio.ByteBuffer;

public interface IDecoder {

    MessagePack separate(ByteBuffer bb) throws IllegalArgumentException;

    DecoderType getType();

}
