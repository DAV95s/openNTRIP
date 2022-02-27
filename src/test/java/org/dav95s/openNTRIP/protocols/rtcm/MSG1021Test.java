package org.dav95s.openNTRIP.protocols.rtcm;

import org.dav95s.openNTRIP.commons.Message;
import org.dav95s.openNTRIP.protocols.rtcm.messages.MSG1021;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class MSG1021Test {
    @Test
    public void msg1021MaxValues() {
        MSG1021 msg1021Max = new MSG1021();
        msg1021Max.setMessageNumber(1021);
        msg1021Max.setSourceName("0123456789012345678901234567891");
        msg1021Max.setTargetName("0123456789012345678901234567891");
        msg1021Max.setSystemIdentificationNumber(255);
        msg1021Max.setUtilizedTransformationMessageIndicator(10);
        msg1021Max.setPlateNumber(31);
        msg1021Max.setComputationIndicator(15);
        msg1021Max.setHeightIndicator(3);
        msg1021Max.setLatValid(90);
        msg1021Max.setLonValid(180);
        msg1021Max.setdLatValid(9.1);
        msg1021Max.setdLonValid(9.1);
        msg1021Max.setdX(4194.303);
        msg1021Max.setdY(4194.303);
        msg1021Max.setdZ(4194.303);
        msg1021Max.setRx(42949.67291);
        msg1021Max.setRy(42949.67291);
        msg1021Max.setRz(42949.67291);
        msg1021Max.setdS(167.77215);
        msg1021Max.setAs(6386777.215);
        msg1021Max.setBs(6383554.431);
        msg1021Max.setAt(6386777.215);
        msg1021Max.setBt(6383554.431);
        msg1021Max.setHorizontalQuality(7);
        msg1021Max.setVerticalQuality(7);

        byte[] check1 = msg1021Max.getBytes();
        MSG1021 msg10212 = new MSG1021(check1);
        byte[] check2 = msg10212.getBytes();

        Assert.assertArrayEquals(check1, check2);
    }

    @Test
    public void decoderEncode() {
        String file = "src/test/resources/1021.rtcm3";
        try (FileInputStream input = new FileInputStream(file)) {
            byte[] bytes = input.readAllBytes();
            Rtcm3Separator separator = new Rtcm3Separator();
            ArrayList<Message> separate = separator.separate(bytes);

            List<Message> collect1021 = separate.stream().filter(msg -> msg.name.equals("1021")).collect(Collectors.toList());
            Assert.assertTrue(collect1021.size() > 0);

            for (Message msg1021 : collect1021){
                MSG1021 msg1 = new MSG1021(msg1021.bytes);
                byte[] bytes1 = msg1.getBytes();
                MSG1021 msg2 = new MSG1021(bytes1);
                byte[] bytes2 = msg2.getBytes();
                Assert.assertArrayEquals(msg1021.bytes, bytes1);
                Assert.assertArrayEquals(msg1021.bytes, bytes2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}