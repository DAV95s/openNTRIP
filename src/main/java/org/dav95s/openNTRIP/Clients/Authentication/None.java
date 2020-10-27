package org.dav95s.openNTRIP.Clients.Authentication;

import org.dav95s.openNTRIP.Clients.Client;

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
