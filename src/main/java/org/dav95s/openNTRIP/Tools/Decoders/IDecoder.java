package org.dav95s.openNTRIP.Tools.Decoders;

import org.dav95s.openNTRIP.Tools.RTCMStream.MessagesPack;

import java.nio.ByteBuffer;

public interface IDecoder {

    MessagesPack separate(ByteBuffer bb) throws IllegalArgumentException;

    DecoderType getType();

}
