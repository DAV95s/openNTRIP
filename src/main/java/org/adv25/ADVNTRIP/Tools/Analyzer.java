package org.adv25.ADVNTRIP.Tools;

import org.adv25.ADVNTRIP.Caster;
import org.adv25.ADVNTRIP.Clients.IClient;
import org.adv25.ADVNTRIP.Databases.DAO.StationDAO;
import org.adv25.ADVNTRIP.Databases.Models.StationModel;
import org.adv25.ADVNTRIP.Servers.GnssStation;
import org.adv25.ADVNTRIP.Tools.RTCM.MSG1006;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Analyzer implements IClient, Runnable {
    private static Logger log = Logger.getLogger(Analyzer.class.getName());

    protected ConcurrentHashMap<Integer, byte[]> rawData = new ConcurrentHashMap<>();
    protected Map<Integer, Integer> details = new TreeMap<>();

    //ConcurrentHashMap<Integer, Integer> details = new ConcurrentHashMap<>(); // its time mark for "Format-Details"

    GnssStation server; //base station

    StationModel model;

    Thread thread;

    long StartPackageTimeMark = 0; //Time of start stream. For bit rate
    long LastPackageTimeMark = 0; //Time of last data send. For check up alive

    StationDAO stationDAO = new StationDAO();

    public Analyzer() {

    }

    public Analyzer(GnssStation server) {
        this.server = server;
        model = server.getModel();

        server.addClient(this);
        thread = new Thread(this);
    }

    @Override
    public void sendMessage(ByteBuffer bb) {
        if (StartPackageTimeMark == 0)
            StartPackageTimeMark = System.currentTimeMillis();

        LastPackageTimeMark = System.currentTimeMillis();

        if (bb.limit() == 0)
            return;

        int preambleIndex, shift, msgNmb;

        while (bb.hasRemaining()) {
            if (bb.get() != -45)
                continue;

            preambleIndex = bb.position() - 1;
            shift = bb.getShort(preambleIndex + 1) + 6;
            msgNmb = (bb.getShort(preambleIndex + 3) & 0xffff) >> 4;

            try {
                bb.position(preambleIndex);
                byte[] msg = new byte[shift];

                bb.get(msg, 0, shift);

                rawData.put(msgNmb, msg);

                saveRaw(msgNmb);

                bitContainer += msg.length;

                bb.position(preambleIndex);
                bb.position(preambleIndex + shift);
            } catch (IllegalArgumentException | BufferUnderflowException e) {
                break;
            }
        }

        if (thread != null && !thread.isAlive()) {
            thread.start();
        }

    }

    public void run() {
        log.log(Level.FINE, model.getMountpoint() + " analyzer is running!");

        stationDAO.setOnline(model);

        boolean isApiRequested = true;

        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

//            long deltaT = System.currentTimeMillis() - LastPackageTimeMark;
//            if (deltaT > 5000) {
//                stationDAO.setOffline(model);
//            }
//            if (deltaT > 10000) {
//                server.safeClose();
//            }

            if (isApiRequested) {
                determinePosition();
                isApiRequested = false;
            }

            determineFormatDetails();
            determineRtcmVersion();
            determineNav_System();
            determineCarrier();
            stationDAO.update(model);
        }
    }

    // bitrate
    long bitContainer = 0;

    void bitrateMeter(int data) {
        bitContainer += data;
        long deltaT = System.currentTimeMillis() - StartPackageTimeMark;
        deltaT /= 1000;
        model.setBitrate((int) (bitContainer / deltaT));
    }

    // forms the "Format-Details" field. "1004(1), 1005(5)"
    HashMap<Integer, Long> temp = new HashMap<>();

    void saveRaw(int numb) {
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

    void determineFormatDetails() {
        String formatDetails = "";

        for (Map.Entry<Integer, Integer> entry : details.entrySet()) {
            ;
            formatDetails += ("," + entry.getKey() + "(" + entry.getValue() + ")");
        }
        model.setFormatDetails(formatDetails.substring(1));
    }

    // Determine the RTCM version from packages name
    void determineRtcmVersion() {
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

        if (format33 != 0) {
            model.setFormat("RTCM 3.3");
            return;
        }
        if (format32 != 0) {
            model.setFormat("RTCM 3.2");
            return;
        }
        if (format31 != 0) {
            model.setFormat("RTCM 3.1");
            return;
        }
        if (format30 != 0) {
            model.setFormat("RTCM 3.0");
        }
    }

    void determineNav_System() {
        String navSystems = "";
        int[] GPS = {1001, 1002, 1003, 1004, 1015, 1016, 1017, 1019, 1030, 1034, 1057, 1058, 1059, 1060, 1061, 1062, 1071, 1072, 1073,
                1074, 1075, 1076, 1077};

        for (int i : GPS) {
            if (rawData.containsKey(i)) {
                navSystems += "+GPS";
                break;
            }
        }

        int[] GLONASS = {1009, 1010, 1011, 1012, 1020, 1031, 1035, 1037, 1038, 1039, 1063, 1064, 1065, 1066, 1067, 1068, 1081, 1082,
                1083, 1084, 1085, 1086, 1087, 1230};

        for (int i : GLONASS) {
            if (rawData.containsKey(i)) {
                navSystems += "+GLO";
                break;
            }
        }

        int[] GAL = {1091, 1092, 1093, 1094, 1095, 1096, 1097};

        for (int i : GAL) {
            if (rawData.containsKey(i)) {
                navSystems += "+GAL";
                break;
            }
        }

        if (navSystems.length() > 0) {
            model.setNavSystem(navSystems.substring(1));
        }
    }

    void determineCarrier() {
        int carrier = 0;
        int[] L1 = {1001, 1002, 1009, 1010, 1071, 1081, 1091};
        for (int i : L1) {
            if (rawData.containsKey(i)) {
                carrier = 1;
                break;
            }
        }
        int[] L2 = {1003, 1004, 1011, 1012, 1072, 1073, 1074, 1076, 1077, 1082, 1083, 1084, 1086, 1087,
                1092, 1093, 1094, 1096, 1097, 1230};
        for (int i : L2) {
            if (rawData.containsKey(i)) {
                carrier = 2;
                break;
            }
        }
        model.setCarrier(carrier);
    }

    void determinePosition() {
        MSG1006 msg;

        if (rawData.containsKey(1005)) {
            msg = new MSG1006(rawData.get(1005));
        } else if (rawData.containsKey(1006)) {
            msg = new MSG1006(rawData.get(1006));
        } else {
            return;
        }

        double[] lla = ecef2lla(msg.getECEFX(), msg.getECEFY(), msg.getECEFZ());

        model = stationDAO.read(model.getMountpoint());

        model.setLatitude(lla[0]);
        model.setLongitude(lla[1]);

        osmParser(HttpProtocol.osmApi(lla[0], lla[1]));
        stationDAO.update(model);
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

        double[] response = {lat * 180 / Math.PI, lon * 180 / Math.PI, alt};

        return response;
    }

    // Country code
    private String iso2CountryCodeToIso3CountryCode(String iso2CountryCode) {
        Locale locale = new Locale("", iso2CountryCode);
        return locale.getISO3Country();
    }
    // Country code


    public void osmParser(String raw) {
        String[] identifier = {"suburb", "village", "city", "county", "state"};

        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(raw);
            json = (JSONObject) json.get("address");

            for (String match : identifier) {
                if (json.containsKey(match)) {
                    model.setIdentifier((String) json.get(match));
                    break;
                }
            }

            if (json.containsKey("country_code")) {
                String iso = iso2CountryCodeToIso3CountryCode((String) json.get("country_code"));
                model.setCountry(iso);
            }

        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void safeClose() throws IOException {

    }

}
