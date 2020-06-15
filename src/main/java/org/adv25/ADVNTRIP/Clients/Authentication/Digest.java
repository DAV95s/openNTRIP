package org.adv25.ADVNTRIP.Clients.Authentication;

import org.adv25.ADVNTRIP.Clients.Client;

public class Digest implements Authentication {

    @Override
    public boolean start(Client client) {
        return false;
    }

    @Override
    public String toString() {
        return "Digest";
    }
}
