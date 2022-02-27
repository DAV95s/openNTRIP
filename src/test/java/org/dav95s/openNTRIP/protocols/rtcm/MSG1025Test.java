package org.dav95s.openNTRIP.protocols.rtcm;

import org.dav95s.openNTRIP.commons.Message;
import org.dav95s.openNTRIP.protocols.rtcm.messages.MSG1025;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MSG1025Test {

    @Test
    public void decodeEncode() {
        String file = "src/test/resources/1025.rtcm3";
        try (FileInputStream input = new FileInputStream(file)) {
            byte[] bytes = input.readAllBytes();
            Rtcm3Separator separator = new Rtcm3Separator();
            ArrayList<Message> separate = separator.separate(bytes);

            List<Message> collect1025 = separate.stream().filter(msg -> msg.name.equals("1025")).collect(Collectors.toList());
            Assert.assertTrue(collect1025.size() > 0);

            for (Message msg1025 : collect1025){
                MSG1025 msg1 = new MSG1025(msg1025.bytes);
                byte[] bytes1 = msg1.getBytes();
                MSG1025 msg2 = new MSG1025(bytes1);
                byte[] bytes2 = msg2.getBytes();
                Assert.assertArrayEquals(msg1025.bytes, bytes1);
                Assert.assertArrayEquals(msg1025.bytes, bytes2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}