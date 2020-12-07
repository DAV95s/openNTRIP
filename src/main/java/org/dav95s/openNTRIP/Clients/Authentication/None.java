package org.dav95s.openNTRIP.Clients.Authentication;

import org.dav95s.openNTRIP.Clients.User;

public class None implements IAuthenticator {
    @Override
    public boolean authentication(User user) {
        return true;
    }

    @Override
    public String toString() {
        return "None";
    }
}
