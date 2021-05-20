package org.dav95s.openNTRIP.Servers;


import org.dav95s.openNTRIP.Clients.Authentication.IAuthenticator;
import org.dav95s.openNTRIP.Clients.User;
import org.dav95s.openNTRIP.Databases.Models.MountPointModel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

public class MountPoint {
    final static private Logger logger = LoggerFactory.getLogger(MountPoint.class.getName());

    final protected MountPointModel model;

    public MountPoint(MountPointModel model) {
        this.model = model;
    }

    public String getName() {
        return model.getName();
    }

    public void clientAuthorization(User user) throws IOException, SQLException {

        if (logger.isDebugEnabled()) {
            JSONObject object = new JSONObject();
            object.put("input", user.toString());
            object.put("isAvailable", model.isAvailable());
            object.put("authenticator", model.getAuthenticator());
            object.put("pool", Arrays.toString(model.getStationsPool().toArray()));
            logger.debug(object.toString());
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

    public ReferenceStation getReferenceStation(User user) {
        return model.getReferenceStation(user);
    }

    @Override
    public String toString() {
        return model.toString();
    }

}


