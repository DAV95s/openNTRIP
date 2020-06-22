import org.adv25.ADVNTRIP.Tools.Analyzer;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import static org.adv25.ADVNTRIP.Tools.RTCM.RTCM.crc24q;

public class TestAnalyzer {
    @Test
    public void TestRtcm3Parser() {
        String[] rtcmTest = {
                "Numb: 1006 Size: 27",
                "Numb: 1008 Size: 42",
                "Numb: 1013 Size: 55",
                "Numb: 1019 Size: 67",
                "Numb: 1020 Size: 51",
                "Numb: 1033 Size: 69",
                "Numb: 1077 Size: 479",
                "Numb: 1087 Size: 378",
                "Numb: 1097 Size: 429"
        };

        String path = "src/test/resources/testRtcm.rtcm3";

        File file = new File(path);

        try {
            InputStream input = new FileInputStream(file);
            ByteBuffer byteBuffer = ByteBuffer.wrap(input.readAllBytes());
            input.close();

            //send(byteBuffer);
            ArrayList<String> list = new ArrayList<>();

            //rawData.forEach((k, v) -> list.add("Numb: " + k + " Size: " + v.length));
            String[] array = list.toArray(new String[list.size()]);

            Arrays.sort(array);
            Arrays.sort(rtcmTest);

            Assert.assertArrayEquals(array, rtcmTest);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //CRC24q algorithm test
        //for (byte[] b : rawData.values()) {
        //    byte[] result = crc24q(b, b.length - 3, 0);
        //    Assert.assertArrayEquals(result, new byte[]{b[b.length - 3], b[b.length - 2], b[b.length - 1]});
       // }
    }
}
