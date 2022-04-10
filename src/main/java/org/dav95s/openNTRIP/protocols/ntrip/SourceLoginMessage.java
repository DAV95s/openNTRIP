package org.dav95s.openNTRIP.protocols.ntrip;

public class SourceLoginMessage {
    public final String login;
    public final String password;

    public SourceLoginMessage(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
