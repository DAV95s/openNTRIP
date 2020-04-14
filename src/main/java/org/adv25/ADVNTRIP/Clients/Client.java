package org.adv25.ADVNTRIP.Clients;

import org.adv25.ADVNTRIP.Servers.GnssStation;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client implements IClient {
    GnssStation server;
    SocketChannel channel;

    public Client(GnssStation server, SocketChannel channel) {
        this.server = server;
        this.channel = channel;
    }
    
    public void safeClose() throws IOException {
    	channel.close();
    }

    public void sendMessage(ByteBuffer bb) throws IOException {
        channel.write(bb);
    }
}
