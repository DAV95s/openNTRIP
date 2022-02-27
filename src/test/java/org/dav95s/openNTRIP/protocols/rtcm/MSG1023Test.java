package org.dav95s.openNTRIP.protocols.rtcm;

import org.dav95s.openNTRIP.commons.Message;
import org.dav95s.openNTRIP.protocols.rtcm.messages.MSG1023;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MSG1023Test {
    @Test
    public void decodeEncode() {

        String file = "src/test/resources/1023.rtcm3";
        try (FileInputStream input = new FileInputStream(file)) {
            byte[] bytes = input.readAllBytes();
            Rtcm3Separator separator = new Rtcm3Separator();
            ArrayList<Message> separate = separator.separate(bytes);

            List<Message> collect1023 = separate.stream().filter(msg -> msg.name.equals("1023")).collect(Collectors.toList());
            Assert.assertTrue(collect1023.size() > 0);

            for (Message msg1023 : collect1023){
                MSG1023 msg1 = new MSG1023(msg1023.bytes);
                byte[] bytes1 = msg1.getBytes();
                MSG1023 msg2 = new MSG1023(bytes1);
                byte[] bytes2 = msg2.getBytes();
                Assert.assertArrayEquals(msg1023.bytes, bytes1);
                Assert.assertArrayEquals(msg1023.bytes, bytes2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}