package org.dav95s.openNTRIP.Clients.Authentication;

import org.dav95s.openNTRIP.Clients.User;

public class Digest implements IAuthenticator {

    @Override
    public boolean authentication(User user) {
        return false;
    }

    @Override
    public String toString() {
        return "Digest";
    }
}
