package org.dav95s.openNTRIP.Servers;

import org.dav95s.openNTRIP.CRSUtils.GridShift.ResidualsGrid;
import org.dav95s.openNTRIP.Databases.DataSource;
import org.dav95s.openNTRIP.Tools.RTCM.Assets.*;
import org.dav95s.openNTRIP.Tools.RTCM.MSG1021;
import org.dav95s.openNTRIP.Tools.RTCM.MSG1025;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class Crs {
    private static final Timer crsDispenser = new Timer("crsDispenser");

    int id;
    int mountpoint_id;
    String crs;
    String geoidPath;
    JSONObject json;

    ProjectionType projectionType;
    TransformationType transformationType;
    JSONObject target;
    JSONObject proj;
    JSONObject source;

    CRS1 crs1;
    CRS2 crs2;
    CRS3 crs3;

    /**
     * Message 1021 or 1022
     */
    private TimerTask crsTask1 = new TimerTask() {
        int stage = 0;
        int[] delays = new int[]{3000, 8000, 13000, 60000};

        @Override
        public void run() {

            crsDispenser.schedule(this, delays[stage]);

            if (stage < 3) {
                stage++;
            } else {
                crsDispenser.schedule(this, 60000, 60000);
            }

        }
    };

    /**
     * Message 1023 or 1024
     */
    private TimerTask crsTask2 = new TimerTask() {
        int stage = 0;
        int[] delays = new int[]{4, 9, 14, 60};

        @Override
        public void run() {

        }
    };

    /**
     * Message 1025 or 1026 or 1027
     */
    private TimerTask crsTask3 = new TimerTask() {
        int stage = 0;
        int[] delays = new int[]{5, 10, 15, 60};

        @Override
        public void run() {

        }
    };

    public Crs(int mountpointId) throws SQLException {
        this.mountpoint_id = mountpointId;
        if (!this.read()) {
            return;
        }


        json = new JSONObject(crs);
        crsParser();
        ResidualsGrid gridShift = new ResidualsGrid(id, json.getJSONObject("AreaOfValidity"), geoidPath);
    }

    private void crsParser() {
        transformationType = TransformationType.valueOf(json.getString("TransformationType"));
        target = json.getJSONObject("Target");
        source = json.getJSONObject("Source");
        proj = target.getJSONObject("Projection");
        projectionType = ProjectionType.valueOf(proj.getString("ProjectionType"));

        factory1021(transformationType);

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
    }

    private void factory1021(TransformationType transformationType) {
        MSG1021 msg1021 = new MSG1021();
        JSONObject areaOfValidity = json.getJSONObject("AreaOfValidity");
        msg1021.setSourceName(source.getString("Name"));
        msg1021.setTargetName(target.getString("Name"));
        msg1021.setSystemIdentificationNumber(id); //todo error if crs id > 255
        msg1021.setComputationIndicator(0); //todo fix
        msg1021.setPlateNumber(0);//todo fix
        msg1021.setComputationIndicator(json.getInt("ComputationIndicator"));
        msg1021.setHeightIndicator(json.getInt("HeightIndicator"));
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
        crs1 = msg1021;
        System.out.println(crs1);
    }

    private void factory1022() {

    }

    private void factory1025() {
        MSG1025 msg1025 = new MSG1025();
        msg1025.setSystemIdentificationNumber(id);  //todo error if crs id > 255
        msg1025.setProjectionType(projectionType.getNmb());
        msg1025.setLaNO(proj.getDouble("Lan0"));
        msg1025.setLoNO(proj.getDouble("Lon0"));
        msg1025.setS(proj.getDouble("S"));
        msg1025.setFalseEasting(proj.getDouble("FalseEasting"));
        msg1025.setFalseNorthing(proj.getDouble("FalseNorthing"));
        System.out.println(crs3);
    }

    private void factory1026() {

    }

    private void factory1027() {

    }

    public void get1023or1024msg() {

    }

    public boolean read() throws SQLException {
        String sql = "SELECT * FROM `crs` WHERE `id` = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, mountpoint_id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("id");
                    crs = rs.getString("crs");
                    geoidPath = rs.getString("geoid_path");
                    return true;
                } else {
                    return false;
                }
            }

        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }


}

