package org.adv25.openNTRIP.Clients.Authentication;

import org.adv25.openNTRIP.Clients.Client;

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
