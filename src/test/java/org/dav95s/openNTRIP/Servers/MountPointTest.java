package org.dav95s.openNTRIP.Servers;

import org.dav95s.openNTRIP.Clients.User;
import org.dav95s.openNTRIP.Databases.Models.MountPointModel;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

public class MountPointTest {
    HashMap<Integer, ReferenceStation> MockRS = new HashMap<>();
    MountPointModelTest MockMountPointModel = new MountPointModelTest(1);
    MountPoint testedMountPoint = new MountPoint(MockMountPointModel, MockRS);
    HashMap<String, UserMock> MockUsers = new HashMap<>();

    @Before
    public void init() throws IOException {
        MockUsers.put("No position", new UserMock());
        MockUsers.put("Hamburg", new UserMock(53.568045f, 9.979580f));
        MockUsers.put("Bremen", new UserMock(53.100621f, 8.765873f));
        MockUsers.put("London", new UserMock(51.507033f, -0.110511f));

//        ReferenceStationModel model1 = new ReferenceStationModel();
//        model1.getPosition().lat = 53.568045f;
//        model1.getPosition().lon = 9.979580f;
//
//        ReferenceStationModel model2 = new ReferenceStationModel();
//        model1.getPosition().lat = 53.100621f;
//        model1.getPosition().lon = 8.765873f;
//
//        ReferenceStationModel model3 = new ReferenceStationModel();
//        model1.getPosition().lat = 51.507033f;
//        model1.getPosition().lon = -0.110511f;
//
//        MockRS.put(1, new ReferenceStation(model1));
//        MockRS.put(2, new ReferenceStation(model2));
//        MockRS.put(3, new ReferenceStation(model3));
    }

    @Test
    public void emptyListOfReferenceStation() throws IllegalAccessException {
        MockMountPointModel.setNmea(false);
        MockUsers.forEach((k, v) -> {

        });

    }

    @Test
    public void firstReferenceStationInList() {

    }

}

class MountPointModelTest extends MountPointModel {
    public MountPointModelTest(int id) {
        super(id);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    public void setNmea(boolean nmea) {
        super.nmea = nmea;
    }
}

class UserMock extends User {

    public UserMock() {
        super(null, null, null);

    }

    public UserMock(float lat, float lon) {
        super(null, null, null);
        super.position.lat = lat;
        super.position.lon = lon;
    }
}