package org.dav95s.openNTRIP.database.modelsV2;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.dav95s.openNTRIP.commons.Message;
import org.dav95s.openNTRIP.protocols.ntrip.NetworkType;

import java.util.ArrayList;

public class NetworkModel {
    public final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public final String identifier;
    public final NetworkType type;

    public NetworkModel(String identifier, String type) {
        this.identifier = identifier;
        this.type = NetworkType.valueOf(type);
    }
    public NetworkModel(String identifier, NetworkType type) {
        this.identifier = identifier;
        this.type = type;
    }

    //todo need remove cycle and unwrap array in outbound handler
    public synchronized void write(ArrayList<Message> messages, ChannelMatcher matcher) {
        for (Message message : messages) {
            channelGroup.write(message, matcher);
        }
        channelGroup.flush();
    }
}
