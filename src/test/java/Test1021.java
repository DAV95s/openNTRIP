import org.dav95s.openNTRIP.Tools.RTCMStream.BitUtils;
import org.dav95s.openNTRIP.Tools.Decoders.RTCM_3X;
import org.dav95s.openNTRIP.Tools.RTCMStream.Message;
import org.dav95s.openNTRIP.Tools.RTCMStream.MessagePack;
import org.dav95s.openNTRIP.Tools.RTCM.MSG1021;
import org.dav95s.openNTRIP.Tools.RTCM.MSG1023;
import org.dav95s.openNTRIP.Tools.RTCM.MSG1025;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.parser.Proj4Parser;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Test1021 {


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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String proj = "+proj=tmerc +lat_0=0 +lon_0=30 +k=1 +x_0=95900 +y_0=-6552800 +ellps=WGS84 +towgs84=5.476,2.074,9.338,3.38086,5.93454,-0.49579,-1.676094 +units=m +no_defs";
    CRSFactory crsFactory = new CRSFactory();

    @Test
    public void mm() {
        Proj4Parser parser = new Proj4Parser(crsFactory.getRegistry());
        CoordinateReferenceSystem coor = parser.parse("MSK64", proj.split(" "));
        System.out.println(coor.getParameterString());

        coor.getProjection();
    }
}
