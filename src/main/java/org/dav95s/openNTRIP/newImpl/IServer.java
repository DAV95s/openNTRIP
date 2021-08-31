package org.dav95s.openNTRIP.newImpl;

import org.dav95s.openNTRIP.Network.Socket;

public interface IServer {
    public void start(int port);

    public void newConnection(Socket socket);
}
