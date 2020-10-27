package org.dav95s.openNTRIP.Clients.Authentication;

import org.dav95s.openNTRIP.Clients.Client;

public interface Authenticator {
    boolean authentication(Client client);
    String toString();
}
