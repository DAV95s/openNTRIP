package org.dav95s.openNTRIP.Databases.Models;

import org.dav95s.openNTRIP.CRSUtils.GridShift.GridShift;
import org.dav95s.openNTRIP.Databases.DataSource;

import org.dav95s.openNTRIP.Tools.RTCM.MSG1021;
import org.dav95s.openNTRIP.Tools.RTCM.MSG1023;
import org.dav95s.openNTRIP.Tools.RTCM.MSG1025;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class CRS {
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

    MSG1021 msg1021;
    MSG1023 msg1023;
    MSG1025 msg1025;

    public CRS(int mountpointId) throws SQLException {
        this.mountpoint_id = mountpointId;
        if (!this.read()) {
            return;
        }

        json = new JSONObject(crs);
        crsParser();
        GridShift gridShift = new GridShift(id, json.getJSONObject("AreaOfValidity"), geoidPath);
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
        msg1021 = new MSG1021();
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
        System.out.println(msg1021);
    }

    private void factory1022() {

    }

    private void factory1025() {
        msg1025 = new MSG1025();
        msg1025.setSystemIdentificationNumber(id);  //todo error if crs id > 255
        msg1025.setProjectionType(projectionType.nmb);
        msg1025.setLaNO(proj.getDouble("Lan0"));
        msg1025.setLoNO(proj.getDouble("Lon0"));
        msg1025.setS(proj.getDouble("S"));
        msg1025.setFalseEasting(proj.getDouble("FalseEasting"));
        msg1025.setFalseNorthing(proj.getDouble("FalseNorthing"));
        System.out.println(msg1025);
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

    enum TransformationType {
        HelmertLinearExpression(0),
        HelmertStrict(1),
        MolodenskiAbridged(2),
        MolodenskiBadekas(3);

        private int index;

        TransformationType(int i) {
            this.index = i;
        }
    }

    enum ProjectionType {
        NONE(0), TM(1), TMS(2), LCC1SP(3), LCC2SP(4),
        LCCW(5), CS(6), OM(7), OS(8), MC(9), PS(10), DS(11);

        int nmb;

        ProjectionType(int i) {
            nmb = i;
        }
    }
}