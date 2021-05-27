package org.dav95s.openNTRIP.Tools.RTCM;

import org.dav95s.openNTRIP.Tools.Decoders.DecoderRTCM3;
import org.dav95s.openNTRIP.Tools.RTCMStream.MessagePack;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class MSG1006Test {
    String path = "src/test/resources/RTCM_32";

    @Test
    public void start() {

        File file = new File(path);

        try {
            InputStream input = new FileInputStream(file);
            ByteBuffer byteBuffer = ByteBuffer.wrap(input.readAllBytes());
            input.close();
            DecoderRTCM3 decoder = new DecoderRTCM3();

            MessagePack messages = decoder.separate(byteBuffer);
            byte[] bytes = messages.getMessageByNmb(1006).getBytes();


            MSG1006 msg = new MSG1006(bytes);
            byte[] bytes2 = msg.getBytes();
            MSG1006 msgnew = new MSG1006(bytes2);
            byte[] bytes3 = msgnew.getBytes();
            Assert.assertArrayEquals(bytes2, bytes3);
//            System.out.println(msg.toString());
//            System.out.println(msgnew);
//            System.out.println(new BitUtils(msg.getBytes()).toString(' '));
//            System.out.println(new BitUtils(msgnew.getBytes()).toString(' '));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}