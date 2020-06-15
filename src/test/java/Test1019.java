import org.adv25.ADVNTRIP.Tools.Analyzer;
import org.adv25.ADVNTRIP.Tools.RTCM.MSG1019;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Test1019 extends Analyzer {
    @Test
    public void start() {
        String path = "src/test/resources/1019.rtcm3";

        File file = new File(path);

        try {
            InputStream input = new FileInputStream(file);
            ByteBuffer byteBuffer = ByteBuffer.wrap(input.readAllBytes());
            input.close();

            send(byteBuffer);

            MSG1019 msg1019 = new MSG1019(rawData.get(1019));

           System.out.println(msg1019.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
