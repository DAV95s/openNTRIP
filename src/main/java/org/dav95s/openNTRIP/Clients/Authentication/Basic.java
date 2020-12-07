package org.dav95s.openNTRIP.Clients.Authentication;

import org.dav95s.openNTRIP.Clients.Passwords.PasswordHandler;
import org.dav95s.openNTRIP.Clients.User;
import org.dav95s.openNTRIP.Databases.Models.UserModel;
import org.dav95s.openNTRIP.Tools.Config;

import java.sql.SQLException;
import java.util.Base64;

public class Basic implements IAuthenticator {

    @Override
    public boolean authentication(User user) throws SQLException {
        String basicAuth = user.getHttpHeader("Authorization");
        if (basicAuth == null)
            return false;

        if (!basicAuth.matches(" Basic [\\S]+"))
            return false;

        basicAuth = basicAuth.trim();

        String[] accountAndPassword = basicAuthorizationDecode(basicAuth.split(" ")[1]);

        PasswordHandler passwordHandler = Config.getInstance().getPasswordHandler();

        UserModel model = new UserModel();
        model.setUsername(accountAndPassword[0]);

        if (!model.read())
            return false;

        user.setModel(model);

        return passwordHandler.compare(user.getPassword(), accountAndPassword[1]);
    }

    @Override
    public String toString() {
        return "Basic";
    }

    String[] basicAuthorizationDecode(String src) {
        String temp = src.replaceAll("Basic", "").trim();

        byte[] decode = Base64.getDecoder().decode(temp);

        String result = new String(decode);

        String[] response = result.split(":");

        if (response.length != 2)
            throw new SecurityException("Illegal authorization format");

        return response;
    }
}
