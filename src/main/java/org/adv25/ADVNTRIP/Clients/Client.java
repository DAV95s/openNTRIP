package org.adv25.ADVNTRIP.Clients;

import org.adv25.ADVNTRIP.Databases.Models.ClientModel;
import org.adv25.ADVNTRIP.Servers.GnssStation;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client implements IClient {
    GnssStation server;
    SocketChannel channel;
    ClientModel properties;

    public Client(GnssStation server, SocketChannel channel, ClientModel model) {
        this.server = server;
        this.channel = channel;
        this.properties = model;
    }
    
    public void safeClose() throws IOException {
    	channel.close();
    }

    public void sendMessage(ByteBuffer bb) throws IOException {
        channel.write(bb);
    }
}
