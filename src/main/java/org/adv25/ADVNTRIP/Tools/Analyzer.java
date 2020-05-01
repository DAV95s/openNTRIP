package org.adv25.ADVNTRIP.Tools;

import org.adv25.ADVNTRIP.Clients.IClient;
import org.adv25.ADVNTRIP.Databases.Models.StationModel;
import org.adv25.ADVNTRIP.Servers.GnssStation;
import org.adv25.ADVNTRIP.Tools.RTCM.MSG1006;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Analyzer implements IClient, Runnable {
    ConcurrentHashMap<Integer, byte[]> rawData = new ConcurrentHashMap<>();
    ConcurrentHashMap<Integer, Integer> details = new ConcurrentHashMap<>(); // its time mark for "Format-Details"

    GnssStation server; //base station

    StationModel model;

    Thread thread;

    public Analyzer(GnssStation server) {
        this.server = server;
        model = server.getModel();

        server.addClient(this);
        thread = new Thread(this);
    }

    @Override
    public void sendMessage(ByteBuffer bb) {
        if (bb.limit() == 0)
            return;

        int preambleIndex, shift, msgNmb;

        while (bb.hasRemaining()) {
            if (bb.get() != -45)
                continue;

            preambleIndex = bb.position();
            shift = bb.getShort(preambleIndex) + 5;
            msgNmb = (bb.getShort(preambleIndex + 2) & 0xffff) >> 4;

            try {
                byte[] msg = new byte[shift];
                bb.get(msg, 0, shift);
                saveRaw(msgNmb, msg);
                bb.position(preambleIndex);
                bb.position(preambleIndex + shift);
            } catch (IllegalArgumentException e) {
                break;
            }
        }

        if (!thread.isAlive())
            thread.start();
    }


    public void run() {
        System.out.println("Поток запущен");
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            if (model.getCountry() == null || model.getIdentifier() == null) {
                int msgNum = 0;
                if (rawData.contains(1005))
                    msgNum = 1005;

                if (rawData.contains(1006))
                    msgNum = 1006;

                if (msgNum != 0) {
                    MSG1006 msg = new MSG1006();
                    msg.parse(rawData.get(msgNum));
                    System.out.println(msg.getJson());
                }
            }
        }
    }

    // This method save raw data and forms the "Format-Details" field. "1004(1), 1005(5)"
    private HashMap<Integer, Long> temp = new HashMap<>();

    private void saveRaw(int numb, byte[] raw) {
        rawData.put(numb, raw);
        long curTime = System.currentTimeMillis();

        if (temp.containsKey(numb)) {
            float deltaSec = (curTime - temp.get(numb)) / 1000.0f;

            if (deltaSec < 1) {
                deltaSec = 1;
            }
            details.put(numb, Math.round(deltaSec));
        }
        temp.put(numb, curTime);
    }

    // Determine the RTCM version from packages name
    private String determineRtcmFormat() {
        int format30 = 0;
        int format31 = 0;
        int format32 = 0;
        int format33 = 0;

        for (Integer k : rawData.keySet()) {
            int msg = k.intValue();

            if (1001 <= msg && msg <= 1013) {
                format30++;
                continue;
            }
            if ((1001 <= msg && msg <= 1039) || (1057 <= msg && msg <= 1068) || (4001 <= msg && msg <= 4095)) {
                format31++;
                continue;
            }
            if (1044 <= msg && msg <= 1045 || 1071 <= msg && msg <= 1230) {
                format32++;
                continue;
            }
            if (1 <= msg && msg <= 100 || msg == 1042 || msg == 1046) {
                format33++;
                continue;
            }
        }

        if (format33 != 0)
            return "RTCM 3.3";
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
        String[] matches = {"suburb", "village", "city", "county", "state"};
        String countryCode = "country_code";
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(raw);
            json = (JSONObject) json.get("address");
            for (String match : matches) {
                String response = (String) json.get(match);
                if (response != null) {
                    return response;
                }
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return "none";
    }

    @Override
    public void safeClose() throws IOException {

    }

}
