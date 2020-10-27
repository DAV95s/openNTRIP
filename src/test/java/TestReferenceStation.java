import org.dav95s.openNTRIP.Databases.Models.MountPointModel;
import org.dav95s.openNTRIP.Databases.Models.NtripCasterModel;
import org.dav95s.openNTRIP.Databases.Models.ReferenceStationModel;
import org.dav95s.openNTRIP.Servers.NtripCaster;
import org.dav95s.openNTRIP.Servers.ReferenceStation;
import org.dav95s.openNTRIP.Spatial.PointLla;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class TestReferenceStation extends NtripCaster {
    public TestReferenceStation(NtripCasterModel model) throws IOException {
        super(model);
    }

    @Test
    public void getStationFromPool() throws IOException {
        ReferenceStationModel WarszawaStationModel = new ReferenceStationModel();
        WarszawaStationModel.setLla(new PointLla("POINT(52.238056 21.011723)"));
        WarszawaStationModel.setOnline(true);
        ReferenceStation Warszawa = new ReferenceStation(WarszawaStationModel);

        ReferenceStationModel LisboaStationModel = new ReferenceStationModel();
        LisboaStationModel.setLla(new PointLla("POINT(38.736964 -9.140344)"));
        LisboaStationModel.setOnline(true);
        ReferenceStation Lisboa = new ReferenceStation(LisboaStationModel);

        MountPointModel mountPointModel = new MountPointModel();
        ArrayList<ReferenceStation> referenceStations = new ArrayList<>();
        referenceStations.add(Warszawa);
        referenceStations.add(Lisboa);

        //this.mountPoints.add(mountPointModel);

    }
}
