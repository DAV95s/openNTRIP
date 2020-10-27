package org.dav95s.openNTRIP.Network;

import java.io.IOException;

public interface IWork extends Runnable {

    long createTime = System.currentTimeMillis();

    void readSelf() throws IOException;

    void close();
}
