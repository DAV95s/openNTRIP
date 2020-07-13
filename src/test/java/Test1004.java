import org.adv25.ADVNTRIP.Tools.Decoders.RTCM_3X;
import org.adv25.ADVNTRIP.Tools.Msg;
import org.adv25.ADVNTRIP.Tools.RTCM.MSG1004;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;

public class Test1004 {
    String path = "src/test/resources/testRtcm.rtcm3";

    File file = new File(path);

    @Test
    public void start() {
        String path = "src/test/resources/1004.rtcm3";

        File file = new File(path);

        try {
            InputStream input = new FileInputStream(file);
            byte[] bytes = input.readAllBytes();
            input.close();

            RTCM_3X rtcm3X = new RTCM_3X();
            ArrayList<Msg> list = rtcm3X.separate(bytes);
            for (Msg msg : list) {
                if (msg.getNmb() == 1004) {
                    System.out.println(new MSG1004(msg.getBytes()).toString());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
