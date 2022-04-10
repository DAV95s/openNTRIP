package org.dav95s.openNTRIP.core.userServer.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.dav95s.openNTRIP.commons.Registry;
import org.dav95s.openNTRIP.protocols.nmea.NMEA;

public class NmeaPositionHander extends SimpleChannelInboundHandler<String> {
    Registry registry;

    public NmeaPositionHander(Registry registry) {
        this.registry = registry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        NMEA nmea = new NMEA();
        NMEA.GPSPosition gpsPosition = nmea.parse(msg);
        if (gpsPosition.isSet()) {
            System.out.println("No implement!");

        } else {
            System.out.println("No nmea coordinate!");
        }
    }
}
