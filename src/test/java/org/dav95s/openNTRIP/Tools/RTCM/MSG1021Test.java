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
        msg1021Max.setB_valid(90);
        msg1021Max.setL_valid(180);
        msg1021Max.setdB_valid(9.1);
        msg1021Max.setdL_valid(9.1);
        msg1021Max.setdX(new BigDecimal("4194.303"));
        msg1021Max.setdY(new BigDecimal("4194.303"));
        msg1021Max.setdZ(new BigDecimal("4194.303"));
        msg1021Max.setRx(new BigDecimal("42949.67294"));
        msg1021Max.setRy(new BigDecimal("42949.67294"));
        msg1021Max.setRz(new BigDecimal("42949.67294"));
        msg1021Max.setdS(new BigDecimal("167.77215"));
        msg1021Max.setAs(new BigDecimal("16777.215"));
        msg1021Max.setBs(new BigDecimal("33554.431"));
        msg1021Max.setAt(new BigDecimal("16777.215"));
        msg1021Max.setBt(new BigDecimal("33554.431"));
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