package org.dav95s.openNTRIP.Servers;

import org.dav95s.openNTRIP.CRSUtils.GridShift.ResidualsGrid;
import org.dav95s.openNTRIP.Clients.User;
import org.dav95s.openNTRIP.Databases.Models.CrsModel;
import org.dav95s.openNTRIP.Databases.Models.MountPointModel;
import org.dav95s.openNTRIP.Tools.Geometry.Point;
import org.dav95s.openNTRIP.Tools.Geometry.Polygon;
import org.dav95s.openNTRIP.Tools.RTCM.Assets.*;
import org.dav95s.openNTRIP.Tools.RTCM.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Crs {
    static final private Logger logger = LoggerFactory.getLogger(Crs.class.getName());
    static final private ExecutorService crsDispenser = Executors.newSingleThreadExecutor();

    private final ConcurrentHashMap<User, CrsSender> usersSchedule = new ConcurrentHashMap<>();
    private final MountPointModel mountPointModel;
    private final CrsModel model;
    private JSONObject json;

    private ProjectionType projectionType;
    private TransformationType transformationType;
    private JSONObject jsonTargetCrs;
    private JSONObject jsonTargetProj;
    private JSONObject jsonSourceCrs;
    private JSONObject areaOfValidity;
    private boolean isSet = false;
    private ResidualsGrid residualsGrid;

    private CRS1 crs1;
    private CRS2 crs2;
    private CRS3 crs3;

    public Crs(MountPointModel mountPointModel) {
        this.mountPointModel = mountPointModel;
        this.model = new CrsModel(mountPointModel.getId());

        if (!this.model.read()) {
            logger.error("Mountpoint: " + mountPointModel.getName() + " doesn't have Coordinate Reference System");
            return;
        }

        isSet = true;
        logger.info("Mountpoint: " + mountPointModel.getName() + " start initializes CRS");

        json = new JSONObject(model.getCrs());
        jsonTargetCrs = json.getJSONObject("Target");
        jsonTargetProj = jsonTargetCrs.getJSONObject("Projection");
        jsonSourceCrs = json.getJSONObject("Source");
        areaOfValidity = json.getJSONObject("AreaOfValidity");
        // 1025/1026/1027
        projectionType = ProjectionType.valueOf(jsonTargetProj.getString("ProjectionType"));
        switch (projectionType) {
            case TM:
            case TMS:
            case LCC1SP:
            case LCCW:
            case CS:
            case OS:
            case MC:
            case PS:
            case DS:
                msgBuilder1025();
                break;
            case LCC2SP:
                msgBuilder1026();
                break;
            case OM:
                msgBuilder1027();
                break;
            case NONE:
                break;
        }
        //Generate 1022/1023
        residualsGrid = new ResidualsGrid(model.getId(), json, model);

        //Generate 1021/1022
        transformationType = TransformationType.valueOf(json.getString("TransformationType"));
        switch (transformationType) {
            case HelmertLinearExpression:
            case HelmertStrict:
            case MolodenskiBadekas:
                msgBuilder1021();
                break;
            case MolodenskiAbridged:
                msgBuilder1022();
                break;
        }
    }

    public boolean isSet() {
        return isSet;
    }

    private int getUtilizedMessages() {
        int utilizedMessages = 0;

        if (crs2 != null) {
            switch (crs2.getMessageNumber()) {
                case 1023:
                    utilizedMessages |= 1;
                    break;
                case 1024:
                    utilizedMessages |= 2;
                    break;
                default:
                    break;
            }
        }

        if (crs3 != null) {
            switch (crs3.getMessageNumber()) {
                case 1025:
                    utilizedMessages |= 4;
                    break;
                case 1026:
                    utilizedMessages |= 8;
                    break;
                case 1027:
                    utilizedMessages |= 16;
                    break;
            }
        }
        return utilizedMessages;
    }

    private int getPlate(double pLat, double pLon) {
        String path = "src/main/resources/plates.geojson";
        try (FileInputStream in = new FileInputStream(path)) {
            String geoJson = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            JSONArray features = new JSONObject(geoJson).getJSONArray("features");

            for (int f = 0; f < features.length(); f++) {
                JSONObject geometry = features.getJSONObject(f).getJSONObject("geometry");
                String plateCode = features.getJSONObject(f).getJSONObject("properties").getString("Code");
                String type = geometry.getString("type");
                Point[] points = new Point[0];

                if ("Polygon".equals(type)) {
                    JSONArray coordinates = geometry.getJSONArray("coordinates").getJSONArray(0);
                    points = new Point[coordinates.length()];
                    for (int c = 0; c < points.length; c++) {
                        JSONArray point = coordinates.getJSONArray(c);
                        points[c] = new Point(point.getDouble(0), point.getDouble(1));
                    }
                } else if ("MultiPolygon".equals(type)) {
                    JSONArray coordinates = geometry.getJSONArray("coordinates");
                    for (int c = 0; c < coordinates.length(); c++) {
                        JSONArray polygon = coordinates.getJSONArray(c).getJSONArray(0);
                        points = new Point[polygon.length()];
                        for (int z = 0; z < points.length; z++) {
                            JSONArray point = polygon.getJSONArray(z);
                            points[z] = new Point(point.getDouble(0), point.getDouble(1));
                        }
                    }
                }
                if (new Polygon(points).contains(new Point(pLat, pLon))) {
                    return Plates.valueOf(plateCode).getPlateNmb();
                }

            }
        } catch (IOException e) {
            logger.error("error", e);
        }

        return 0;
    }

    private void msgBuilder1021() {
        MSG1021 msg1021 = new MSG1021();

        msg1021.setSourceName(jsonSourceCrs.getString("Name"));
        msg1021.setTargetName(jsonTargetCrs.getString("Name"));

        msg1021.setSystemIdentificationNumber(model.getId()); //todo error if crs id > 255

        msg1021.setUtilizedTransformationMessageIndicator(getUtilizedMessages());
        msg1021.setPlateNumber(getPlate(areaOfValidity.getDouble("LatCenter"), areaOfValidity.getDouble("LonCenter")));
        msg1021.setComputationIndicator(transformationType.getIndex());
        msg1021.setHeightIndicator(json.getInt("HeightIndicator"));

        msg1021.setLatValid(areaOfValidity.getDouble("LatCenter"));
        msg1021.setLonValid(areaOfValidity.getDouble("LonCenter"));
        msg1021.setdLatValid(areaOfValidity.getDouble("Height"));
        msg1021.setdLonValid(areaOfValidity.getDouble("Width"));

        JSONObject datum = jsonTargetCrs.getJSONObject("Datum");
        msg1021.setdX(datum.getDouble("dX"));
        msg1021.setdY(datum.getDouble("dY"));
        msg1021.setdZ(datum.getDouble("dZ"));
        msg1021.setRx(datum.getDouble("rX"));
        msg1021.setRy(datum.getDouble("rY"));
        msg1021.setRz(datum.getDouble("rZ"));
        msg1021.setdS(datum.getDouble("dS"));

        JSONObject sourceEllipsoid = jsonSourceCrs.getJSONObject("Ellipsoid");
        msg1021.setAs(sourceEllipsoid.getDouble("a"));
        msg1021.setBs(sourceEllipsoid.getDouble("b"));

        JSONObject targetEllipsoid = jsonTargetCrs.getJSONObject("Ellipsoid");
        msg1021.setAt(targetEllipsoid.getDouble("a"));
        msg1021.setBt(targetEllipsoid.getDouble("b"));

        msg1021.setHorizontalQuality(json.getInt("HorizontalQuality"));
        msg1021.setVerticalQuality(json.getInt("VerticalQuality"));

        logger.info(mountPointModel.getName() + " has initialized " + msg1021);

        crs1 = msg1021;
    }


    private void msgBuilder1022() {
        MSG1022 msg1022 = new MSG1022();

        msg1022.setSourceName(jsonSourceCrs.getString("Name"));
        msg1022.setTargetName(jsonTargetCrs.getString("Name"));

        msg1022.setSystemIdentificationNumber(model.getId()); //todo error if crs id > 255

        msg1022.setUtilizedTransformationMessageIndicator(getUtilizedMessages());
        msg1022.setPlateNumber(0);//todo fix
        msg1022.setComputationIndicator(transformationType.getIndex());
        msg1022.setHeightIndicator(json.getInt("HeightIndicator"));

        JSONObject areaOfValidity = json.getJSONObject("AreaOfValidity");
        msg1022.setLatValid(areaOfValidity.getDouble("LatCenter"));
        msg1022.setLonValid(areaOfValidity.getDouble("LonCenter"));
        msg1022.setdLatValid(areaOfValidity.getDouble("Height"));
        msg1022.setdLonValid(areaOfValidity.getDouble("Width"));

        JSONObject datum = jsonTargetCrs.getJSONObject("Datum");
        msg1022.setdX(datum.getDouble("dX"));
        msg1022.setdY(datum.getDouble("dY"));
        msg1022.setdZ(datum.getDouble("dZ"));
        msg1022.setRx(datum.getDouble("rX"));
        msg1022.setRy(datum.getDouble("rY"));
        msg1022.setRz(datum.getDouble("rZ"));
        msg1022.setdS(datum.getDouble("dS"));
        msg1022.setXp(datum.getDouble("Xp"));
        msg1022.setYp(datum.getDouble("Yp"));
        msg1022.setZp(datum.getDouble("Zp"));

        JSONObject sourceEllipsoid = jsonSourceCrs.getJSONObject("Ellipsoid");
        msg1022.setAs(sourceEllipsoid.getDouble("a"));
        msg1022.setBs(sourceEllipsoid.getDouble("b"));

        JSONObject targetEllipsoid = jsonTargetCrs.getJSONObject("Ellipsoid");
        msg1022.setAt(targetEllipsoid.getDouble("a"));
        msg1022.setBt(targetEllipsoid.getDouble("b"));

        msg1022.setHorizontalQuality(json.getInt("HorizontalQuality"));
        msg1022.setVerticalQuality(json.getInt("VerticalQuality"));

        logger.info(mountPointModel.getName() + " has initialized " + msg1022);

        crs1 = msg1022;
    }

    /*
     private int messageNumber = 1023;
    private int SystemIdentificationNumber;
    private boolean HorizontalShiftIndicator;
    private boolean VerticalShiftIndicator;
    private double Lat0;
    private double Lon0;
    private double dLat0;
    private double dLon0;
    private double MdLat;
    private double MdLon;
    private double MdH;
    Grid[] gridMap = new Grid[16];
    private int HorizontalInterpolationMethodIndicator;
    private int VerticalInterpolationMethodIndicator;
    private int HorizontalGridQualityIndicator;
    private int VerticalGridQualityIndicator;
    private int ModifiedJulianDayNumber;
     */


    private void msgBuilder1025() {
        MSG1025 msg1025 = new MSG1025();
        msg1025.setSystemIdentificationNumber(model.getId());  //todo error if crs id > 255
        msg1025.setProjectionType(projectionType.getNmb());
        msg1025.setLaNO(jsonTargetProj.getDouble("Lan0"));
        msg1025.setLoNO(jsonTargetProj.getDouble("Lon0"));
        msg1025.setS(jsonTargetProj.getDouble("S"));
        msg1025.setFalseEasting(jsonTargetProj.getDouble("FalseEasting"));
        msg1025.setFalseNorthing(jsonTargetProj.getDouble("FalseNorthing"));
        logger.info(mountPointModel.getName() + " has initialized " + msg1025);
        this.crs3 = msg1025;
    }

    private void msgBuilder1026() {
        MSG1026 msg1026 = new MSG1026();
        msg1026.setSystemIdentificationNumber(model.getId());  //todo error if crs id > 255
        msg1026.setProjectionType(projectionType.getNmb());
        msg1026.setLaFO(jsonTargetProj.getDouble("LaFO"));
        msg1026.setLoFO(jsonTargetProj.getDouble("LoFO"));
        msg1026.setLaSP1(jsonTargetProj.getDouble("LaSP1"));
        msg1026.setLaSP2(jsonTargetProj.getDouble("LaSP2"));
        msg1026.setEFO(jsonTargetProj.getDouble("EFO"));
        msg1026.setNFO(jsonTargetProj.getDouble("NFO"));
        logger.info(mountPointModel.getName() + " has initialized " + msg1026);
        this.crs3 = msg1026;
    }

    private void msgBuilder1027() {
        MSG1027 msg1027 = new MSG1027();
        msg1027.setSystemIdentificationNumber(model.getId());  //todo error if crs id > 255
        msg1027.setProjectionType(projectionType.getNmb());
        msg1027.setLaPC(jsonTargetProj.getDouble("LaPC"));
        msg1027.setLoPC(jsonTargetProj.getDouble("LoPC"));
        msg1027.setAzIL(jsonTargetProj.getDouble("AzIL"));
        msg1027.setRectifiedToSkew(jsonTargetProj.getDouble("RectifiedToSkew"));
        msg1027.setSILppm(jsonTargetProj.getDouble("SILppm"));
        msg1027.setEPC(jsonTargetProj.getDouble("EPC"));
        msg1027.setNPC(jsonTargetProj.getDouble("NPC"));
        logger.info(mountPointModel.getName() + " has initialized " + msg1027);
        this.crs3 = msg1027;
    }

    public void subscribeNewUser(User user) {
        if (isSet)
            usersSchedule.put(user, new CrsSender(user));
    }

    public void msgProcess(User user) {
        crsDispenser.submit(usersSchedule.get(user));
    }

    class CrsSender implements Runnable {
        long timeMark = System.currentTimeMillis();
        int timeStage = 0;
        int[] scheduleDelays = new int[]{5000, 5000, 5000, 60000};

        User user;

        public CrsSender(User user) {
            this.user = user;
        }

        @Override
        public void run() {
            try {
                System.out.println("scheduleDelays " + scheduleDelays[timeStage]);

                if (System.currentTimeMillis() - timeMark > scheduleDelays[timeStage]) {
                    timeMark = System.currentTimeMillis();
                    user.write(crs1.getBytes());
//                    user.write(crs2.getBytes());
                    user.write(crs3.getBytes());

                    if (timeStage < scheduleDelays.length - 1) {
                        timeStage++;
                    }
                }
            } catch (IOException e) {
                usersSchedule.remove(user);
            }
        }
    }
}
