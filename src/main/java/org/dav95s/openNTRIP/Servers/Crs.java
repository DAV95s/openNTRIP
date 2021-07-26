package org.dav95s.openNTRIP.Servers;

import org.dav95s.openNTRIP.CRSUtils.GridShift.ResidualsGrid;
import org.dav95s.openNTRIP.Clients.User;
import org.dav95s.openNTRIP.Databases.Models.CrsModel;
import org.dav95s.openNTRIP.Tools.NMEA;
import org.dav95s.openNTRIP.Tools.RTCM.Assets.CRS1;
import org.dav95s.openNTRIP.Tools.RTCM.Assets.CRS2;
import org.dav95s.openNTRIP.Tools.RTCM.Assets.CRS3;
import org.dav95s.openNTRIP.Tools.RTCM.Assets.ProjectionType;
import org.dav95s.openNTRIP.Tools.RTCM.MSG1021;
import org.dav95s.openNTRIP.Tools.RTCM.MSG1023;
import org.dav95s.openNTRIP.Tools.RTCM.MSG1025;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Crs {
    static final private Logger logger = LoggerFactory.getLogger(Crs.class.getName());
    static final private ExecutorService crsDispenser = Executors.newSingleThreadExecutor();

    private final ConcurrentHashMap<User, CrsSender> usersSchedule = new ConcurrentHashMap<>();
    private final MountPoint mountPoint;
    private final CrsModel model;
    private JSONObject json;

    private ProjectionType projectionType;
    private JSONObject target;
    private JSONObject proj;
    private JSONObject source;

    private CRS1 crs1;
    private CRS2 crs2;
    private CRS3 crs3;

    private boolean isSet = false;

    ResidualsGrid residualsGrid;

    public Crs(int mountpointId, MountPoint mountPoint) {
        this.model = new CrsModel(mountpointId);
        this.mountPoint = mountPoint;

        if (!this.model.read()) {
            logger.info("Mountpoint: " + mountPoint.getName() + " doesn't have Coordinate Reference System");
            return;
        } else {
            isSet = true;
        }

        try {
            logger.info("Mountpoint: " + mountPoint.getName() + " start initializes CRS");

            json = new JSONObject(model.getCrs());

            target = json.getJSONObject("Target");
            source = json.getJSONObject("Source");
            proj = target.getJSONObject("Projection");

            // 1025/1026/1027
            projectionType = ProjectionType.valueOf(proj.getString("ProjectionType"));
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
                    factory1025();
                    break;
                case LCC2SP:
                    factory1026();
                    break;
                case OM:
                    factory1027();
                    break;
                case NONE:
                    break;
            }
            //Generate 1022/1023
            residualsGrid = new ResidualsGrid(model.getId(), json.getJSONObject("AreaOfValidity"), model.getGeoidPath());

            //Generate 1021/1022
            factory1021();

        } catch (Exception e) {
            logger.error("CRS initialize error", e);
        }
    }


    private void factory1021() {
        MSG1021 msg1021 = new MSG1021();

        msg1021.setSourceName(source.getString("Name"));
        msg1021.setTargetName(target.getString("Name"));

        msg1021.setSystemIdentificationNumber(model.getId()); //todo error if crs id > 255

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

        msg1021.setUtilizedTransformationMessageIndicator(utilizedMessages);
        msg1021.setPlateNumber(0);//todo fix
        msg1021.setComputationIndicator(json.getInt("ComputationIndicator"));
        msg1021.setHeightIndicator(json.getInt("HeightIndicator"));

        JSONObject areaOfValidity = json.getJSONObject("AreaOfValidity");
        msg1021.setLatValid(areaOfValidity.getDouble("LatCenter"));
        msg1021.setLonValid(areaOfValidity.getDouble("LonCenter"));
        msg1021.setdLatValid(areaOfValidity.getDouble("Height"));
        msg1021.setdLonValid(areaOfValidity.getDouble("Width"));

        JSONObject datum = target.getJSONObject("Datum");
        msg1021.setdX(datum.getDouble("dX"));
        msg1021.setdY(datum.getDouble("dY"));
        msg1021.setdZ(datum.getDouble("dZ"));
        msg1021.setRx(datum.getDouble("rX"));
        msg1021.setRy(datum.getDouble("rY"));
        msg1021.setRz(datum.getDouble("rZ"));
        msg1021.setdS(datum.getDouble("dS"));

        JSONObject sourceEllipsoid = source.getJSONObject("Ellipsoid");
        msg1021.setAs(sourceEllipsoid.getDouble("a"));
        msg1021.setBs(sourceEllipsoid.getDouble("b"));

        JSONObject targetEllipsoid = target.getJSONObject("Ellipsoid");
        msg1021.setAt(targetEllipsoid.getDouble("a"));
        msg1021.setBt(targetEllipsoid.getDouble("b"));

        msg1021.setHorizontalQuality(json.getInt("HorizontalQuality"));
        msg1021.setVerticalQuality(json.getInt("VerticalQuality"));

        logger.info(mountPoint.getName() + " has initialized " + msg1021);

        crs1 = msg1021;
    }

    private void factory1022() {

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

    private void factory1023(NMEA.GPSPosition gpsPosition) {
        MSG1023 msg1023 = new MSG1023();
        msg1023.setSystemIdentificationNumber(model.getId());
        msg1023.setHorizontalShiftIndicator(true);
        msg1023.setVerticalShiftIndicator(true);
        if (gpsPosition != null) {

            msg1023.setGridMap(residualsGrid.get16PointsAroundUser(gpsPosition));
        }
    }

    private void factory1025() {
        MSG1025 msg1025 = new MSG1025();
        msg1025.setSystemIdentificationNumber(model.getId());  //todo error if crs id > 255
        msg1025.setProjectionType(projectionType.getNmb());
        msg1025.setLaNO(proj.getDouble("Lan0"));
        msg1025.setLoNO(proj.getDouble("Lon0"));
        msg1025.setS(proj.getDouble("S"));
        msg1025.setFalseEasting(proj.getDouble("FalseEasting"));
        msg1025.setFalseNorthing(proj.getDouble("FalseNorthing"));
        logger.info(mountPoint.getName() + " has initialized " + msg1025);
        this.crs3 = msg1025;
    }

    private void factory1026() {

    }

    private void factory1027() {

    }

    public void newUser(User user) {
        usersSchedule.put(user, new CrsSender(user));
    }

    public void run(User user) throws NullPointerException {
        crsDispenser.submit(usersSchedule.get(user));
    }

    public boolean isSet() {
        return isSet;
    }

    private byte[] generateResidualMessage(User user) {
        return null;
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
                System.out.println("CRS runnable");
                if (System.currentTimeMillis() - timeMark > scheduleDelays[timeStage]) {
                    user.write(crs1.getBytes());
                    user.write(crs3.write());

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
