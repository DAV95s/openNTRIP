import org.dav95s.openNTRIP.Clients.User;
import org.dav95s.openNTRIP.Databases.Models.MountPointModel;
import org.dav95s.openNTRIP.Databases.Models.ReferenceStationModel;
import org.dav95s.openNTRIP.Databases.Models.UserModel;
import org.dav95s.openNTRIP.Servers.ReferenceStation;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class TestReferenceStation {


    @Test
    public void getStationFromPool() throws IOException {
        ReferenceStationModel WarszawaStationModel = new ReferenceStationModel();
        WarszawaStationModel.getPosition().lat = (float) 52.238056;
        WarszawaStationModel.getPosition().lon = (float) 21.011723;
        WarszawaStationModel.setOnline(true);
        ReferenceStation Warszawa = new ReferenceStation(WarszawaStationModel);

        ReferenceStationModel LisboaStationModel = new ReferenceStationModel();
        LisboaStationModel.getPosition().lat = (float) 38.736964;
        LisboaStationModel.getPosition().lon = (float) -9.140344;
        LisboaStationModel.setOnline(true);
        ReferenceStation Lisboa = new ReferenceStation(LisboaStationModel);

        MountPointModel mountPointModel = new MountPointModel();
        mountPointModel.setNmea(true);
    }
}
