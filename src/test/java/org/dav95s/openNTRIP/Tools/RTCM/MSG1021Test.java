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
import java.math.BigDecimal;
import java.nio.ByteBuffer;


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

        byte[] check1 = msg1021Max.write();
        MSG1021 msg10212 = new MSG1021(check1);
        byte[] check2 = msg10212.write();

        Assert.assertArrayEquals(check1, check2);
    }

    @Test
    public void decoderEncode() {
        try {
            String path = "src/test/resources/1021.rtcm3";
            InputStream input = new FileInputStream(path);
            ByteBuffer buffer = ByteBuffer.wrap(input.readAllBytes());
            input.close();
            DecoderRTCM3 decoder = new DecoderRTCM3();
            MessagePack pack = decoder.separate(buffer);
            Message msg = pack.getMessageByNmb(1021);
            MSG1021 msg1021 = new MSG1021(msg.getBytes());
            byte[] bytes = msg1021.write();

            System.out.println(msg1021);
            System.out.println(new MSG1021(bytes));
            System.out.println(new BitUtils(msg.getBytes()).toString(' '));
            System.out.println(new BitUtils(bytes).toString(' '));
            Assert.assertArrayEquals(msg.getBytes(), bytes);

            Assert.assertArrayEquals(msg.getBytes(), bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}