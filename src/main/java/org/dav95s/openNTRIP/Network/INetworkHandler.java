package org.dav95s.openNTRIP.Network;

import java.io.IOException;

public interface INetworkHandler extends Runnable {

    void readChannel() throws IOException;

    void close();
}
