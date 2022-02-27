package org.dav95s.openNTRIP.core.referenceStation;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.dav95s.openNTRIP.commons.StationRegistry;
import org.dav95s.openNTRIP.database.models.ReferenceStationModel;

public class ReferenceStation {

    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public final ReferenceStationModel model;

    private StationRegistry registry;

    public ReferenceStation(ReferenceStationModel model, StationRegistry registry) {
        this.model = model;

        this.registry = registry;
    }

    public void write(byte[] bytes) {
        channelGroup.write(bytes);
    }

    public void subscribe(Channel channel) {
        channelGroup.add(channel);
    }

    public void unsubscribe(Channel channel) {
        channelGroup.remove(channel);
    }

    public void close(){
        registry.removeReferenceStation(model.name);
    }

    public float distance(double lat, double lon) {
        if (this.model != null) {
            throw new IllegalStateException("Reference station does not have coordinates " + this.toString());
        }

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat - model.lat);
        double dLng = Math.toRadians(lon - model.lon);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(model.lat)) * Math.cos(Math.toRadians(lat)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (float) (earthRadius * c);
    }
}
