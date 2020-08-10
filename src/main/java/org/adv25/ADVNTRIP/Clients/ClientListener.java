package org.adv25.ADVNTRIP.Clients;

import org.adv25.ADVNTRIP.Servers.ReferenceStation;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface ClientListener {
    void send(ByteBuffer bytes, ReferenceStation referenceStation) throws IOException;

    void safeClose() throws IOException;
}
