package org.adv25.openNTRIP.Clients.Authentication;

import org.adv25.openNTRIP.Clients.Client;

public interface Authenticator {
    boolean authentication(Client client);
    String toString();
}
