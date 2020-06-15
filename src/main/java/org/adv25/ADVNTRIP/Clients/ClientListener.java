package org.adv25.ADVNTRIP.Clients;

import org.adv25.ADVNTRIP.Servers.BaseStation;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface ClientListener {
    void send(ByteBuffer bytes, BaseStation baseStation) throws IOException;

    void safeClose() throws IOException;
}
