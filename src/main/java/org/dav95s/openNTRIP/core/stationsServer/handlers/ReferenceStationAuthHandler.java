package org.dav95s.openNTRIP.core.stationsServer.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.dav95s.openNTRIP.commons.Registry;
import org.dav95s.openNTRIP.core.ChannelState;
import org.dav95s.openNTRIP.database.modelsV2.NetworkModel;
import org.dav95s.openNTRIP.database.modelsV2.ReferenceStationModel;
import org.dav95s.openNTRIP.database.repository.ReferenceStationRepository;
import org.dav95s.openNTRIP.exception.ReferenceStationAuthorizationException;
import org.dav95s.openNTRIP.protocols.ntrip.NtripResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReferenceStationAuthHandler extends SimpleChannelInboundHandler<byte[]> {
    private final static Logger logger = LoggerFactory.getLogger(ReferenceStationAuthHandler.class.getName());

    private final Registry registry;
    private final ReferenceStationRepository referenceStationRepository;

    public ReferenceStationAuthHandler(Registry registry, ReferenceStationRepository referenceStationRepository) {
        this.registry = registry;
        this.referenceStationRepository = referenceStationRepository;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("New inbound connect " + ctx.channel().remoteAddress() + " channelID:" + ctx.channel().id());
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        String message = new String(msg);
        String[] params = message.split("\r\n");

        if (params.length < 1)
            throw new IllegalArgumentException("Not valid request" + ctx.channel().remoteAddress());

        String[] authData = params[0].split(" ", 3);

        if (authData.length != 3 || !authData[0].equals("SOURCE")) {
            throw new IllegalArgumentException("Not valid request" + ctx.channel().remoteAddress());
        }

        String pass = authData[1];
        String name = authData[2];

        ReferenceStationModel model = referenceStationRepository.GetReferenceStationByName(name);

        if (model == null || !model.password.equals(pass)) {
            throw new ReferenceStationAuthorizationException("Reference station " + name + " is not exists or bad password " + ctx.channel().remoteAddress());
        }

        ctx.channel().attr(ChannelState.REFERENCE_STATION_MODEL).set(model);

        logger.info(model.name + ": Successful authorization " + ctx.channel().remoteAddress());
        ctx.writeAndFlush(NtripResponse.OK_MESSAGE);
        ctx.pipeline().remove("StationAuthHandler");
        ctx.pipeline().addLast("RtcmStreamHandler", new Rtcm3InboundHandler(registry, model));

        for (String network : model.networks) {
            NetworkModel networkModel = registry.networks.get(network);
            if (networkModel == null){
                logger.warn("Reference station " + model.name + " has undefined network");
                continue;
            }
            networkModel.channelGroup.add(ctx.channel());
        }

        registry.networks.get(model.name).channelGroup.add(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IllegalArgumentException) {
            logger.info("Channel ID:" + ctx.channel().id() + " " + cause.getMessage(), cause);
            ctx.writeAndFlush(NtripResponse.BAD_PASSWORD);
        } else if (cause instanceof ReferenceStationAuthorizationException) {
            logger.info("Reference station is not exists or bad password ");
            ctx.writeAndFlush(NtripResponse.BAD_PASSWORD);
        }

        ctx.close();
    }
}
