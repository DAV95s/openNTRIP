package org.adv25.ADVNTRIP.Clients.Authentication;

import org.adv25.ADVNTRIP.Clients.Client;

public class None implements Authentication {
    @Override
    public boolean start(Client client) {
        return true;
    }

    @Override
    public String toString() {
        return "None";
    }
}
