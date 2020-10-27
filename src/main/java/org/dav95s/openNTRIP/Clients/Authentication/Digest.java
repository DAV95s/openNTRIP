package org.dav95s.openNTRIP.Clients.Authentication;

import org.dav95s.openNTRIP.Clients.Client;

public class Digest implements Authenticator {

    @Override
    public boolean authentication(Client client) {
        return false;
    }

    @Override
    public String toString() {
        return "Digest";
    }
}
