import java.io.*;

public class TestConnect {
    class Base {
        byte[] data;

        public Base() {
            String path = "src/test/resources/testRtcm.rtcm3";
            File file = new File(path);
            try {
                InputStream in = new FileInputStream(file);
                data = in.readAllBytes();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

    class Client {

    }
}
