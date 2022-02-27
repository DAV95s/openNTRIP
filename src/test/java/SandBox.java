import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

public class SandBox {

    @Test
    public void start() {
        String json = "{\"type\":\"FeatureCollection\",\"totalFeatures\":1,\"features\":[{\"type\":\"Feature\",\"id\":\"ZKP_LAND_06.239216801\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[108820.5,84474.09],[108707.71,84426.3],[108734.23,84359.9],[108763.53,84346.78],[108775.15,84343.76],[108847.13,84381.85],[108843.98,84386.98],[108835.68,84400.32],[108836.44,84401.7],[108856.74,84413.87],[108820.5,84474.09]],[[108803.27,84378.69],[108772.61,84382.61],[108774.15,84394.79],[108805.11,84390.61],[108803.27,84378.69]]]},\"geometry_name\":\"GEOLOC\",\"properties\":{\"KN\":\"78:15:0843201:1008\",\"STATUS\":\"Неизвестно\",\"ADDRESS\":\"г.Санкт-Петербург, проспект Стачек, участок 238, (г.Санкт-Петербург, проспект Стачек, участок 238, (у дома 208))\",\"PERMITTED_USE\":\" Для размещения религиозных объектов\",\"FACT_AREA\":null,\"UPDATED_AREA\":null,\"DECLARED_AREA\":11480,\"RIGHT_KIND\":null,\"P_DATE\":\"22.10.2002\",\"CAD_COST\":12819205.14,\"PREV_KNS\":\"78:8432А:1008\",\"BEFORE_KN\":null,\"HAS_CAD_SURVEY\":\"Да\",\"WAS_RENTED\":\"В аренде\"}}],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"urn:ogc:def:crs:EPSG::99781\"}}}";
        JSONObject jsonObject = new JSONObject(json);

        JSONArray jsonArray = jsonObject.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray polygon = jsonArray.getJSONArray(i);
            System.out.println(polygon.getDouble(0) + " " + polygon.getDouble(1));
        }

    }

}

