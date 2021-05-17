package org.dav95s.openNTRIP.Tools.RTCM;

import org.dav95s.openNTRIP.Tools.Decoders.DecoderRTCM3;
import org.dav95s.openNTRIP.Tools.RTCMStream.BitUtils;
import org.dav95s.openNTRIP.Tools.RTCMStream.Message;
import org.dav95s.openNTRIP.Tools.RTCMStream.MessagePack;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MSG1023Test {
    @Test
    public void decodeEncode() {
        try {
            String path = "src/test/resources/1023.rtcm3";
            InputStream input = new FileInputStream(path);
            ByteBuffer buffer = ByteBuffer.wrap(input.readAllBytes());

            DecoderRTCM3 decoder = new DecoderRTCM3();
            MessagePack pack = decoder.separate(buffer);
            Message msg = pack.getMessageByNmb(1023);

            MSG1023 msg1023 = new MSG1023(msg.getBytes());
            System.out.println(msg1023);
            byte[] bytes = msg1023.write();
            MSG1023 msg10233 = new MSG1023(bytes);
            System.out.println(msg10233);

            System.out.println(new BitUtils(msg.getBytes()).toString(' '));
            System.out.println(new BitUtils(bytes).toString(' '));
            Assert.assertArrayEquals(msg.getBytes(), bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}