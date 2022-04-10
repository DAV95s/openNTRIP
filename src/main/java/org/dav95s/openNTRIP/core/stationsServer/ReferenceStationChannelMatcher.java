package org.dav95s.openNTRIP.core.stationsServer;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelMatcher;
import io.netty.util.AttributeKey;
import org.dav95s.openNTRIP.core.ChannelState;

public class ReferenceStationChannelMatcher implements ChannelMatcher {
    final AttributeKey<String> key = ChannelState.REFERENCE_STATION;
    final String name;

    public ReferenceStationChannelMatcher(String name) {
        this.name = name;
    }

    @Override
    public boolean matches(Channel channel) {
        return channel.attr(key).get().equals(name) || (channel.attr(key).get() == null);
    }
}
