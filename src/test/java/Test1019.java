
import org.adv25.ADVNTRIP.Tools.Decoders.RTCM_3X;
import org.adv25.ADVNTRIP.Tools.Message;
import org.adv25.ADVNTRIP.Tools.RTCM.MSG1019;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Test1019 {
    @Test
    public void start() {
        String path = "src/test/resources/1019.rtcm3";

        File file = new File(path);

        try {
            InputStream input = new FileInputStream(file);
            ByteBuffer byteBuffer = ByteBuffer.wrap(input.readAllBytes());
            input.close();
            RTCM_3X decoder = new RTCM_3X();

            //ArrayList<Message> messages = decoder.separate(byteBuffer);
            //MSG1019 msg1019 = new MSG1019(messages.get(0).getBytes());

            //System.out.println(msg1019.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
