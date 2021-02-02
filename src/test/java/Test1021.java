import org.dav95s.openNTRIP.Tools.BitUtils;
import org.dav95s.openNTRIP.Tools.Decoders.RTCM_3X;
import org.dav95s.openNTRIP.Tools.Message;
import org.dav95s.openNTRIP.Tools.MessagePack;
import org.dav95s.openNTRIP.Tools.RTCM.MSG1021;
import org.dav95s.openNTRIP.Tools.RTCM.MSG1023;
import org.dav95s.openNTRIP.Tools.RTCM.MSG1025;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Test1021 {
    private int toSignedInt(String bits) {
        int position = 0;
        int answer = 0;

        for (int i = bits.length() - 1; i > 0; i--) {
            if (bits.charAt(i) == '1')
                answer += Math.pow(2, position);
            position++;
        }
        if (bits.charAt(0) == '1')
            answer += Math.pow(2, position) * -1;
        return answer;
    }

    @Test
    public void msg1021MaxValues() {
        MSG1021 msg1021Max = new MSG1021();
        msg1021Max.setMessageNumber(4095);
        msg1021Max.setSourceName("0123456789012345678901234567891");
        msg1021Max.setTargetName("0123456789012345678901234567891");
        msg1021Max.setSystemIdentificationNumber(255);
        msg1021Max.setUtilizedTransformationMessageIndicator(10);
        msg1021Max.setPlateNumber(31);
        msg1021Max.setComputationIndicator(31);
        msg1021Max.setHeightIndicator(3);
        msg1021Max.setB_valid(-324000);
        msg1021Max.setL_valid(648000);
        msg1021Max.setdB_valid(32766);
        msg1021Max.setdL_valid(32766);
        msg1021Max.setdX(new BigDecimal("4194.303"));
        msg1021Max.setdY(new BigDecimal("4194.303"));
        msg1021Max.setdZ(new BigDecimal("4194.303"));
        msg1021Max.setRx(new BigDecimal("-42949.67294"));
        msg1021Max.setRy(new BigDecimal("42949.67294"));
        msg1021Max.setRz(new BigDecimal("-42949.67294"));
        msg1021Max.setdS(new BigDecimal("-167.77215"));
        msg1021Max.setAdd_as(new BigDecimal("16777.215"));
        msg1021Max.setAdd_bs(new BigDecimal("33554.431"));
        msg1021Max.setAdd_at(new BigDecimal("16777.215"));
        msg1021Max.setAdd_bt(new BigDecimal("33554.431"));
        msg1021Max.setHrInd(7);
        msg1021Max.setVrInd(7);

        byte[] check1 = msg1021Max.write();
        MSG1021 msg10212 = new MSG1021(check1);
        byte[] check2 = msg10212.write();

        Assert.assertArrayEquals(check1,check2);

    }

    @Test
    public void decoder() {
        try {
            String path = "src/test/resources/1021.rtcm3";
            InputStream input = new FileInputStream(path);
            ByteBuffer buffer = ByteBuffer.wrap(input.readAllBytes());
            input.close();
            RTCM_3X decoder = new RTCM_3X();
            MessagePack pack = decoder.separate(buffer);
            Message msg = pack.getMessageByNmb(1021);
            long mark1 = System.currentTimeMillis();
            MSG1021 msg1021 = new MSG1021(msg.getBytes());
            System.out.println(System.currentTimeMillis() - mark1);
            mark1 = System.currentTimeMillis();
            byte[] bytes = msg1021.write();
            System.out.println(System.currentTimeMillis() - mark1);
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

    @Test
    public void decoder2() {
        try {
            String path = "src/test/resources/1023.rtcm3";
            InputStream input = new FileInputStream(path);
            ByteBuffer buffer = ByteBuffer.wrap(input.readAllBytes());

            RTCM_3X decoder = new RTCM_3X();
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

    @Test
    public void decoder3() {
        try {

            String path = "src/test/resources/1025.rtcm3";
            InputStream input = new FileInputStream(path);
            ByteBuffer buffer = ByteBuffer.wrap(input.readAllBytes());

            RTCM_3X decoder = new RTCM_3X();
            MessagePack pack = decoder.separate(buffer);
            Message msg = pack.getMessageByNmb(1025);

            MSG1025 msg1025 = new MSG1025(msg.getBytes());
            System.out.println(msg1025);
            byte[] bytes = msg1025.write();
            MSG1025 msg10235 = new MSG1025(bytes);
            System.out.println(msg10235);

            System.out.println(new BitUtils(msg.getBytes()).toString(' '));
            System.out.println(new BitUtils(bytes).toString(' '));
            Assert.assertArrayEquals(msg.getBytes(), bytes);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void extractor() {
        String path = "C:\\Users\\1663646\\Desktop\\123";

        try {
            InputStream input = new FileInputStream(path);
            ByteBuffer buffer = ByteBuffer.wrap(input.readAllBytes());

            RTCM_3X decoder = new RTCM_3X();
            MessagePack pack = decoder.separate(buffer);

            String new_path = "C:\\Users\\1663646\\Desktop\\1025.rtcm";
            OutputStream out = new FileOutputStream(new_path);
            ArrayList<Message> arr = pack.getArray();
            for (Message msg : arr) {
                if (msg.getNmb() == 1025)
                    out.write(msg.getBytes());
            }
            out.flush();
            out.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
