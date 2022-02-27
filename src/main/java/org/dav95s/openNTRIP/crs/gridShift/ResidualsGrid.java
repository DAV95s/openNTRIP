package org.dav95s.openNTRIP.crs.gridShift;

import com.harium.storage.kdtree.KDTree;
import com.harium.storage.kdtree.KeyDuplicateException;
import com.harium.storage.kdtree.KeySizeException;
import org.dav95s.openNTRIP.crs.geoids.GGF;
import org.dav95s.openNTRIP.crs.geoids.IGeoid;
import org.dav95s.openNTRIP.database.models.CrsModel;
import org.dav95s.openNTRIP.database.models.GridModel;
import org.dav95s.openNTRIP.protocols.nmea.NMEA;
import org.dav95s.openNTRIP.utils.binaryParse.Normalize;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResidualsGrid {
    private final Logger logger = LoggerFactory.getLogger(ResidualsGrid.class.getName());
    //crs boundary box
    double area_top;
    double area_bottom;
    double area_left;
    double area_right;
    //number of points in the grid
    int colCount;
    int rowCount;
    //resolution of the grid
    double dLat0 = 0.004167;
    double dLon0 = 0.008333;

    IGeoid geoid;

    // [0],[1],[2],[3],[4],[5],[6],...[N]
    // [N+1],[N+2],[N+3],[N+4],[N+5],[N+6],[N+7],...
    GridNode[][] grid;

    public ResidualsGrid(int crs_id, JSONObject json, CrsModel model) {
        geoid = initGeoid(model.getGeoidPath());
        JSONObject validArea = json.getJSONObject("AreaOfValidity");
        double latC = validArea.getDouble("LatCenter");
        double lonC = validArea.getDouble("LonCenter");
        double height = validArea.getDouble("Height");
        double width = validArea.getDouble("Width");

        //crs boundary
        area_top = latC + height / 2;
        area_bottom = latC - height / 2;
        area_left = lonC - width / 2;
        area_right = lonC + width / 2;

        if (area_left < -180)
            area_left += 360;

        if (area_left > 180)
            area_left -= 360;

        if (area_right < -180)
            area_right += 360;

        if (area_right > 180)
            area_right -= 360;

        //resolution of crs grid
        colCount = (int) (Math.abs(area_right - area_left) / dLon0);
        rowCount = (int) (Math.abs(area_top - area_bottom) / dLat0);

        grid = new GridNode[rowCount][colCount];

        initGrid(crs_id);
        backupGrid(model);

    }

    private void initGrid(int crs_id) {
        KDTree<GeodeticPoint> kdTree = new KDTree<>(2);
        GridModel gridModel = new GridModel();

        //get from db all geodetic point
        ArrayList<GeodeticPoint> points = gridModel.getAddGeodeticPointByCrsId(crs_id);
        //create spatial index
        for (GeodeticPoint point : points) {
            try {
                kdTree.insert(new double[]{point.north, point.east}, point);
            } catch (KeySizeException | KeyDuplicateException e) {
                e.printStackTrace();
            }
        }

        //delete array from memory
        points = null;

        //generate grid
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                try {
                    double nodeLat = area_top - row * dLat0;
                    double nodeLon = area_left + col * dLon0;
                    //get nearest geodetic point for node of grid
                    List<GeodeticPoint> nearestPoints = kdTree.nearest(new double[]{nodeLat, nodeLon}, 5);
                    //init node of grid
                    grid[row][col] = IDW(nearestPoints, nodeLat, nodeLon);
                    grid[row][col].height = geoid.getValueByPoint(nodeLat, nodeLon);
                } catch (KeySizeException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private GridNode IDW(List<GeodeticPoint> gridNodes, double nodeLat, double nodeLon) {
        double sum = 0;
        for (GeodeticPoint gridNode : gridNodes) {
            sum += gridNode.distance(nodeLat, nodeLon);
        }

        sum = 1 / sum;

        for (GeodeticPoint GeodeticPoint : gridNodes) {
            GeodeticPoint.distance = 1 / GeodeticPoint.distance / sum;
        }

        GridNode response = new GridNode();
        response.north = Normalize.normalize(nodeLat, 9);
        response.east = Normalize.normalize(nodeLon, 9);
        response.dEast = 0;
        response.dNorth = 0;

        for (GeodeticPoint gridNode : gridNodes) {
            response.dNorth += gridNode.dNorth * gridNode.distance;
            response.dEast += gridNode.dEast * gridNode.distance;
        }

        response.dNorth = Normalize.normalize(response.dNorth, 9);
        response.dEast = Normalize.normalize(response.dEast, 9);

        return response;
    }

    public void backupGrid(CrsModel model) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("top", area_top);
        jsonObject.put("bottom", area_bottom);
        jsonObject.put("left", area_left);
        jsonObject.put("right", area_right);
        jsonObject.put("gridWidth", dLon0);
        jsonObject.put("gridHeight", dLat0);
        jsonObject.put("zone", new JSONArray());

        JSONArray gridJson = new JSONArray();
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                JSONArray cell = new JSONArray();
                cell.put(grid[row][col].dNorth);
                cell.put(grid[row][col].dEast);
                cell.put(grid[row][col].dH);
                gridJson.put(cell);
            }
        }
        jsonObject.put("grid", gridJson);
        model.setResidualGrid(jsonObject.toString());
        model.update();
    }

    public GridNode[] get16PointsAroundUser(NMEA.GPSPosition user) {
        ArrayList<GridNode> grid16 = new ArrayList<>(16);
        int lon = (int) Normalize.normalize((user.lon - area_left) / dLon0, 4) - 1;
        int lat = (int) Normalize.normalize((user.lat - area_top) / dLat0, 4) - 1;
        for (int x = 0; x < 4; x++) {
            grid16.addAll(Arrays.asList(grid[lat + x]).subList(lon, 4 + lon));
        }
        return grid16.toArray(new GridNode[16]);
    }

    private IGeoid initGeoid(String geoidPath) {
        return new GGF(geoidPath);
    }
}
