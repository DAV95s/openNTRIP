package org.adv25.ADVNTRIP.Tools;

import org.adv25.ADVNTRIP.Clients.IClient;
import org.adv25.ADVNTRIP.Servers.GnssStation;
import org.adv25.ADVNTRIP.Tools.RTCM.IRTCM;
import org.adv25.ADVNTRIP.Tools.RTCM.MSG1006;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;


public class Analyzer implements IClient, Runnable {
    Queue<byte[]> rawData = new LinkedList<byte[]>();

    GnssStation server; //base station

    Map<Integer, IRTCM> mapper = new HashMap<>();
    
	@Override
	public void safeClose() throws IOException {
			
	}

    @Override
    public void sendMessage(ByteBuffer bb) throws IOException {
        rawData.add(bb.array());
    }

    public Analyzer(GnssStation server, int TimeMilliseconds) {
        this.server = server;
        server.addClient(this);
        mapper.put(1005, new MSG1006());
        mapper.put(1006, new MSG1006());
        System.out.println("1231");
        new Thread(this).start();
    }

    public void run() {
        while (true) {
            if (rawData.size() == 0)
                return;

            ByteBuffer buffer = ByteBuffer.wrap(rawData.poll());

            int preambleIndex, shift, msgNmb;

            while (buffer.hasRemaining()) {
                if (buffer.get() != -45)
                    continue;

                preambleIndex = buffer.position();
                shift = buffer.getShort(preambleIndex) + 5;
                msgNmb = (buffer.getShort(preambleIndex + 2) & 0xffff) >> 4;
                System.out.println(msgNmb);
                try {
                    byte[] msg = new byte[shift];
                    buffer.position(preambleIndex);
                    switch (msgNmb) {
                        case 1005:
                        case 1006:
                            buffer.get(msg, 0, preambleIndex + shift);
                            new BasePosition(msg);
                        default:
                            break;
                    }
                    buffer.position(preambleIndex);
                    buffer.position(preambleIndex + shift);
                } catch (IllegalArgumentException e) {
                    buffer.clear();
                    break;
                }
            }
        }
    }

    class BasePosition implements Runnable {

        private byte[] msg;

        public BasePosition(byte[] msg) {
            this.msg = msg;
            System.out.println("BASE");
        }

        @Override
        public void run() {

        }
    }

    private String rtcmFormatAnalyze() {
        int format30 = 0;
        int format31 = 0;
        int format32 = 0;

        /*if (rtcmPack.size() == 0)
            return "RAW";

        for (Integer k : rtcmPack.keySet()) {
            int msg = k.intValue();

            if (1001 <= msg && msg <= 1013) {
                format30++;
                continue;
            }
            if ((1001 <= msg && msg <= 1039) || (1057 <= msg && msg <= 1068) || (4001 <= msg && msg <= 4095)) {
                format31++;
                continue;
            }
            if (1071 <= msg && msg <= 1230) {
                format32++;
                continue;
            }
        }
         */

        if (format32 != 0)
            return "RTCM 3.2";
        if (format31 != 0)
            return "RTCM 3.1";
        if (format30 != 0) {
            return "RTCM 3.0";
        }
        return "RAW";
    }


    //gist.github.com/1536056
    // WGS84 ellipsoid constants
    private final double a = 6378137; // radius
    private final double e = 8.1819190842622e-2;  // eccentricity

    private final double asq = Math.pow(a, 2);
    private final double esq = Math.pow(e, 2);

    private double[] ecef2lla(double x, double y, double z) {

        double b = Math.sqrt(asq * (1 - esq));
        double bsq = Math.pow(b, 2);
        double ep = Math.sqrt((asq - bsq) / bsq);
        double p = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        double th = Math.atan2(a * z, b * p);

        double lon = Math.atan2(y, x);
        double lat = Math.atan2((z + Math.pow(ep, 2) * b * Math.pow(Math.sin(th), 3)), (p - esq * a * Math.pow(Math.cos(th), 3)));
        double N = a / (Math.sqrt(1 - esq * Math.pow(Math.sin(lat), 2)));
        double alt = p / Math.cos(lat) - N;

        lon = lon % (2 * Math.PI);

        double[] ret = {lat * 180 / Math.PI, lon * 180 / Math.PI, alt};

        return ret;
    }

    public String osmParser(String raw) {
        String[] matches = {"suburb", "village", "city", "county", "state", "country"};
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(raw);
            json = (JSONObject) json.get("address");
            for (String match : matches) {
                String response;
                if ((response = (String) json.get(match)) != null) {
                    return response;
                }
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return "none";
    }


}
