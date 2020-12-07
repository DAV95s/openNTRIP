package org.dav95s.openNTRIP.Clients.Authentication;

import org.dav95s.openNTRIP.Clients.User;

import java.sql.SQLException;

public interface IAuthenticator {
    boolean authentication(User user) throws SQLException;
    String toString();
}
