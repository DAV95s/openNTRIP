package org.adv25.ADVNTRIP.Clients.Authentication;

import org.adv25.ADVNTRIP.Clients.Client;

public interface Authentication {
    boolean start(Client client);
    String toString();
}
