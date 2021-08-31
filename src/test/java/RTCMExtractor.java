
import org.dav95s.openNTRIP.Tools.Decoders.DecoderRTCM3;
import org.dav95s.openNTRIP.Tools.RTCMStream.BitUtils;
import org.dav95s.openNTRIP.Tools.RTCMStream.Message;
import org.dav95s.openNTRIP.Tools.RTCMStream.MessagesPack;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RTCMExtractor {

    public void extractor(String path, int msgNmb) {
        try {
            InputStream input = new FileInputStream(path);
            ByteBuffer buffer = ByteBuffer.wrap(input.readAllBytes());

            DecoderRTCM3 decoder = new DecoderRTCM3();
            MessagesPack pack = decoder.separate(buffer);

            String new_path = "C:\\Users\\1663646\\Desktop\\1025.rtcm";
            OutputStream out = new FileOutputStream(new_path);
            ArrayList<Message> arr = pack.getArray();
            for (Message msg : arr) {
                if (msg.getNmb() == msgNmb)
                    out.write(msg.getBytes());
            }
            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void generator() {
        String path = "C:\\Users\\root\\Desktop\\1222.csv";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + ".txt"));
            while (reader.ready()) {
                String base = reader.readLine();
                String[] split = base.split(";");
                String request = base + "\t";
                for (String str : split) {
                    double one = Double.parseDouble(str);
                    request += one + BitUtils.normalize(Math.random(), 6) * 0.000001 + "\t";
                }
                writer.write(request + "\r\n");
            }
            writer.flush();
            reader.close();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void te() {


        ExecutorService service = Executors.newSingleThreadExecutor();
        Run rr = null;
        service.submit(rr);
        service.submit(rr);
        service.submit(rr);
    }

    class Run implements Runnable {
        int i = 0;

        @Override
        public void run() {
            System.out.println(i++);
        }
    }

}
