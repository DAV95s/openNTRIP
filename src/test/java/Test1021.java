import org.dav95s.openNTRIP.Tools.Decoders.DecoderRTCM3;
import org.dav95s.openNTRIP.Tools.RTCMStream.Message;
import org.dav95s.openNTRIP.Tools.RTCMStream.MessagePack;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Test1021 {






    public void extractor() {
        String path = "C:\\Users\\1663646\\Desktop\\123";

        try {
            InputStream input = new FileInputStream(path);
            ByteBuffer buffer = ByteBuffer.wrap(input.readAllBytes());

            DecoderRTCM3 decoder = new DecoderRTCM3();
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


}
