package org.dav95s.openNTRIP.Tools.RTCM;

import org.dav95s.openNTRIP.Tools.Decoders.DecoderRTCM3;
import org.dav95s.openNTRIP.Tools.RTCMStream.BitUtils;
import org.dav95s.openNTRIP.Tools.RTCMStream.Message;
import org.dav95s.openNTRIP.Tools.RTCMStream.MessagesPack;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MSG1025Test {

    @Test
    public void decodeEncode() {
        try {
            String path = "src/test/resources/1025.rtcm3";
            InputStream input = new FileInputStream(path);
            ByteBuffer buffer = ByteBuffer.wrap(input.readAllBytes());

            DecoderRTCM3 decoder = new DecoderRTCM3();
            MessagesPack pack = decoder.separate(buffer);
            Message msg = pack.getMessageByNmb(1025);

            MSG1025 msg1025 = new MSG1025(msg.getBytes());
            System.out.println(msg1025);
            byte[] bytes = msg1025.getBytes();
            MSG1025 msg10235 = new MSG1025(bytes);
            System.out.println(msg10235);

            System.out.println(new BitUtils(msg.getBytes()).toString(' '));
            System.out.println(new BitUtils(bytes).toString(' '));
            Assert.assertArrayEquals(msg.getBytes(), bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}