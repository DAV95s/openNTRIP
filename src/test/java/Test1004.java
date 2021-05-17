import org.dav95s.openNTRIP.Tools.Decoders.DecoderRTCM3;
import org.dav95s.openNTRIP.Tools.RTCMStream.MessagePack;
import org.dav95s.openNTRIP.Tools.RTCM.MSG1006;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;

public class Test1004 {
    String path = "src/test/resources/RTCM_32";

    @Test
    public void start() {

        File file = new File(path);

        try {
            InputStream input = new FileInputStream(file);
            ByteBuffer byteBuffer = ByteBuffer.wrap(input.readAllBytes());
            input.close();
            DecoderRTCM3 decoder = new DecoderRTCM3();

            MessagePack messages = decoder.separate(byteBuffer);
            byte[] bytes = messages.getMessageByNmb(1006).getBytes();


            MSG1006 msg = new MSG1006(bytes);
            System.out.println(msg.toString());
            String raw = "";
            for (byte i : bytes) {
                raw += msg.toBinaryString(i) + " ";
            }
            System.out.println(raw);

            byte[] bytes2 = msg.getBytes();
            String raw2 = "";
            for (byte i : bytes2) {
                raw2 += msg.toBinaryString(i) + " ";
            }
            System.out.println(raw2);


            MSG1006 msgnew = new MSG1006(bytes2);
            System.out.println(msgnew);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
