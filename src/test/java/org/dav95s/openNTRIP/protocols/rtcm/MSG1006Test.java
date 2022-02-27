package org.dav95s.openNTRIP.protocols.rtcm;

import org.dav95s.openNTRIP.commons.Message;
import org.dav95s.openNTRIP.protocols.rtcm.messages.MSG1006;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MSG1006Test {
    String path = "src/test/resources/RTCM_32";

    @Test
    public void start() {

        File file = new File(path);

        try (FileInputStream input = new FileInputStream(file)) {
            byte[] bytes = input.readAllBytes();
            Rtcm3Separator separator = new Rtcm3Separator();
            ArrayList<Message> separate = separator.separate(bytes);

            List<Message> collect1006 = separate.stream().filter(msg -> msg.name.equals("1006")).collect(Collectors.toList());
            Assert.assertTrue(collect1006.size() > 0);

            for (Message msg1006 : collect1006){
                MSG1006 msg = new MSG1006(bytes);
                byte[] bytes2 = msg.getBytes();
                MSG1006 msg2 = new MSG1006(bytes2);
                byte[] bytes3 = msg2.getBytes();
                Assert.assertArrayEquals(bytes2, bytes3);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}