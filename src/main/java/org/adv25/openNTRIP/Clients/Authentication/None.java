package org.adv25.openNTRIP.Clients.Authentication;

import org.adv25.openNTRIP.Clients.Client;

public class None implements Authenticator {
    @Override
    public boolean authentication(Client client) {
        return true;
    }

    @Override
    public String toString() {
        return "None";
    }
}
