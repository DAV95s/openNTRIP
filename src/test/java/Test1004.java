import org.adv25.ADVNTRIP.Tools.Analyzer;
import org.adv25.ADVNTRIP.Tools.RTCM.MSG1004;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;

public class Test1004 extends Analyzer {
    String path = "src/test/resources/testRtcm.rtcm3";

    File file = new File(path);

    @Test
    public void start() {
        String path = "src/test/resources/1004.rtcm3";

        File file = new File(path);

        try {
            InputStream input = new FileInputStream(file);
            ByteBuffer byteBuffer = ByteBuffer.wrap(input.readAllBytes());
            input.close();

            send(byteBuffer);

            MSG1004 msg1004 = new MSG1004(rawData.get(1004));

            for (MSG1004.Sat1004 s : msg1004.getListSatellites()) {
                System.out.println(s.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
