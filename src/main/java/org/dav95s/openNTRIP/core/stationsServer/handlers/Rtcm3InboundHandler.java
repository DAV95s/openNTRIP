package org.dav95s.openNTRIP.core.stationsServer.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.dav95s.openNTRIP.commons.Message;
import org.dav95s.openNTRIP.commons.Registry;
import org.dav95s.openNTRIP.core.ChannelState;
import org.dav95s.openNTRIP.core.stationsServer.ReferenceStationChannelMatcher;
import org.dav95s.openNTRIP.database.modelsV2.NetworkModel;
import org.dav95s.openNTRIP.database.modelsV2.ReferenceStationModel;
import org.dav95s.openNTRIP.protocols.rtcm.Rtcm3Separator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Rtcm3InboundHandler extends SimpleChannelInboundHandler<byte[]> {
    private final static Logger logger = LoggerFactory.getLogger(Rtcm3InboundHandler.class.getName());

    private final Registry registry;
    private final ReferenceStationModel model;
    private final ReferenceStationChannelMatcher channelMatcher;
    private final Rtcm3Separator separator = new Rtcm3Separator();

    public Rtcm3InboundHandler(Registry registry, ReferenceStationModel model) {
        this.model = model;
        this.registry = registry;
        this.channelMatcher = new ReferenceStationChannelMatcher(model.name);
    }

    int totalLengthBytes = 0;
    int totalLengthMessages = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] bytes) throws Exception {
        ReferenceStationModel referenceStationModel = ctx.channel().attr(ChannelState.REFERENCE_STATION_MODEL).get();

        ArrayList<Message> messages = separator.separate(bytes);

        totalLengthBytes += bytes.length;
        for (Message msg : messages) {
            totalLengthMessages += msg.bytes.length;
        }

        logger.debug("Station: " + model.name + " accept " + bytes.length + " bytes, " + " messages " + messages.size());
        logger.debug("Station: " + model.name + " total bytes accepted " + totalLengthBytes + " total parser to " + totalLengthMessages);

        for (String network : referenceStationModel.networks) {
            NetworkModel networkModel = registry.networks.get(network);
            if (networkModel == null) {
                logger.warn("Reference station " + model.name + " has undefined network");
                continue;
            }
            networkModel.write(messages, channelMatcher);
        }
        registry.networks.get(referenceStationModel.name).write(messages, channelMatcher);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //registry.removeReferenceStation(ctx.channel().attr(ChannelState.REFERENCE_STATION).get());
        super.channelInactive(ctx);
    }
}
