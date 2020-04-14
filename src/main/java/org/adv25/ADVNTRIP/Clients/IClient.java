package org.adv25.ADVNTRIP.Clients;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface IClient {
    void sendMessage(ByteBuffer bb) throws IOException;
    
    void safeClose() throws IOException;
}
