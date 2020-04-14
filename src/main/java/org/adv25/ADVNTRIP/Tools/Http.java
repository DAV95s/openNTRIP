package org.adv25.ADVNTRIP.Tools;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;


public class Http {

    public static final byte[] HTTP401 = "HTTP/1.0 401 Unauthorized".getBytes();
    public static final byte[] OK_MESSAGE = "ICY 200 OK\r\n".getBytes();
    public static final byte[] BAD_MESSAGE = "ERROR - Bad Password\r\n".getBytes();


    public static void parse(String RawMessage, HashMap<String, String> httpHeader) throws IndexOutOfBoundsException {
        String[] lineArr = RawMessage.split("\r\n");

        String[] splitedLine; //for parse

        //parse http header
        for (String line : lineArr) {
            splitedLine = line.split(" ");

            if (line.matches("GET [\\S]+ HTTP[\\S]+")) {
                httpHeader.put("GET", splitedLine[1].replaceAll("/", ""));
            }

            if (line.matches("Authorization: Basic [\\S]+")) {
                httpHeader.put("Authorization", splitedLine[2]);
            }

            if (line.matches("SOURCE [\\S]+ [\\S]+")) {
                httpHeader.put("SOURCE", splitedLine[2]);
                httpHeader.put("PASSWORD", splitedLine[1]);
            }
        }
    }

    public static byte[] getOkMessage() {
        return "ICY 200 OK\r\n".getBytes();
    }

    public static void sendMessage(SocketChannel ch, ByteBuffer bb, byte[] msg) {
        bb.clear();
        bb.put(msg);
        bb.flip();
        try {
            ch.write(bb);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessageAndClose(SocketChannel ch, ByteBuffer bb, byte[] msg) {
        bb.clear();
        bb.put(msg);
        bb.flip();
        try {
            ch.write(bb);
            ch.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String osmApi(double lat, double lon) {
        try {
            String url = "https://nominatim.openstreetmap.org/reverse.php?lat=" + lat + "&lon=" + lon + "&format=json&accept-language=en&zoom=14";

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");

            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
