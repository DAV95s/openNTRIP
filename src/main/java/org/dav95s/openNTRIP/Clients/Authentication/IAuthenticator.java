package org.dav95s.openNTRIP.Clients.Authentication;

import org.dav95s.openNTRIP.Clients.User;

public interface IAuthenticator {
    boolean authentication(User user);
    String toString();
}
