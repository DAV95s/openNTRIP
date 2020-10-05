package org.adv25.openNTRIP.Clients.Authentication;

import org.adv25.openNTRIP.Clients.Client;
import org.adv25.openNTRIP.Clients.Passwords.PasswordHandler;
import org.adv25.openNTRIP.Databases.DAO.ClientDAO;
import org.adv25.openNTRIP.Tools.Config;

import java.util.Base64;
import java.util.logging.Logger;

public class Basic implements Authenticator {
    public static final Logger log = Logger.getLogger(Basic.class.getName());

    @Override
    public boolean authentication(Client client) {
        String basicAuth = client.getHttpHeader("Authorization");
        if (basicAuth == null)
            return false;

        if (!basicAuth.matches(" Basic [\\S]+"))
            return false;

        basicAuth = basicAuth.trim();

        String[] accPass = basicAuthorizationDecode(basicAuth.split(" ")[1]);

        PasswordHandler passwordHandler = Config.getInstance().getPasswordHandler();

        ClientDAO dao = new ClientDAO();

        client.setModel(dao.read(accPass[0]));

        return passwordHandler.Compare(client.getPassword(), accPass[1]);


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
