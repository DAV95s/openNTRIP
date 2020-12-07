package org.dav95s.openNTRIP.Servers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dav95s.openNTRIP.Clients.Authentication.IAuthenticator;
import org.dav95s.openNTRIP.Clients.User;
import org.dav95s.openNTRIP.Databases.Models.MountPointModel;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

public class MountPoint {
    final static private Logger logger = LogManager.getLogger(MountPoint.class.getName());

    final private MountPointModel model;

    public MountPoint(MountPointModel model) {
        this.model = model;
    }

    public String getName() {
        return model.getName();
    }

    public void clientAuthorization(User user) throws IOException, SQLException {
        if (user.isAuthenticated()){
            user.subscribe(model.getReferenceStation(user));
        }

        if (logger.isDebugEnabled()) {
            JSONObject object = new JSONObject();
            object.put("from", MountPoint.class.getName());
            object.put("input", user.toString());
            object.put("isAvailable", model.isAvailable());
            object.put("authenticator", model.getAuthenticator());
            object.put("pool", Arrays.toString(model.getStationsPool().toArray()));
            logger.debug(object);
        }

        //available
        if (!model.isAvailable()) {
            user.close();
            return;
        }

        //authentication
        IAuthenticator authenticator = model.getAuthenticator();
        if (!authenticator.authentication(user)) {
            user.sendBadMessageAndClose();
            return;
        }

        user.sendOkMessage();
        user.subscribe(model.getReferenceStation(user));
    }

    @Override
    public String toString() {
        return model.toString();
    }
}
