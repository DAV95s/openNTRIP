import org.dav95s.openNTRIP.Databases.Models.NtripCasterModel;
import org.dav95s.openNTRIP.Databases.Models.ReferenceStationModel;
import org.dav95s.openNTRIP.Databases.Models.UserModel;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

public class TestDB {
    @Test
    public void casters() throws SQLException {
        NtripCasterModel model = new NtripCasterModel();
        model.setAddress("localhost");
        model.setGroup_id(1);
        model.setPort(2101);
        model.setStatus(false);
        model.create();

        model.setPort(44444);
        Assert.assertTrue(model.read());
        Assert.assertEquals(2101, model.getPort());
        Assert.assertEquals(1, model.getGroup_id());
        Assert.assertFalse(model.isStatus());

        model.setPort(123);
        Assert.assertTrue(model.update());

        Assert.assertTrue(model.read());
        Assert.assertEquals(123, model.getPort());

        Assert.assertTrue(model.delete());
        Assert.assertFalse(model.read());
    }

    @Test
    public void refstations() throws SQLException {
        ReferenceStationModel model = new ReferenceStationModel();
        model.setName("dadaf");
        model.setPassword("123");
        model.setFormat("123");
        model.create();
        model.setFormat("321");

        Assert.assertTrue(model.read());
        Assert.assertEquals("123", model.getFormat());

        model.setFormat("321");
        Assert.assertTrue(model.update());
        model.setFormat("4444");
        Assert.assertTrue(model.read());
        Assert.assertEquals("321", model.getFormat());

        Assert.assertTrue(model.delete());
        Assert.assertFalse(model.read());
    }

    @Test
    public void mountpoints() throws SQLException {

    }

    @Test
    public void users() throws SQLException {
        UserModel model = new UserModel();
        //create
        model.setUsername("qwerty123");
        model.setPassword("123123");
        if (model.read())
            model.delete();

        model.create();

        //read
        model.setPassword("123123123");
        Assert.assertTrue(model.read());
        Assert.assertEquals("123123", model.getPassword());

        //update
        model.setPassword("123123123d");
        Assert.assertTrue(model.update());
        model.setPassword("321");
        Assert.assertTrue(model.read());
        Assert.assertEquals("123123123d", model.getPassword());

        //delete
        Assert.assertTrue(model.delete());
        Assert.assertFalse(model.read());
    }

    @Test
    public void asd() throws IOException {

    }
}
