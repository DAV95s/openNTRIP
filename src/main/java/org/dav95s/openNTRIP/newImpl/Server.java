package org.dav95s.openNTRIP.newImpl;

import org.dav95s.openNTRIP.Network.Socket;

import java.util.HashMap;
import java.util.Map;

public class Server implements IServer {
    int port = 0;

    Map<ICaster, Map<String, IMountPoint>> sourceTable = new HashMap<>();

    public void start(int port) {

    }

    public void newConnection(Socket socket) {

    }

}
