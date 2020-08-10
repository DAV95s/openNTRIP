package org.adv25.ADVNTRIP.Network;

import org.adv25.ADVNTRIP.Servers.Caster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

public class NetworkProcessor {
    final static private Logger logger = LogManager.getLogger(NetworkProcessor.class.getName());

    ServerSocketChannel serverSocketChannel = null;

    public NetworkProcessor() {
        try {
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.configureBlocking(false);
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
