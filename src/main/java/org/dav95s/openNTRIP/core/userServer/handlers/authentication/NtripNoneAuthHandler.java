package org.dav95s.openNTRIP.core.userServer.handlers.authentication;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.dav95s.openNTRIP.commons.Registry;
import org.dav95s.openNTRIP.core.ChannelState;
import org.dav95s.openNTRIP.database.modelsV2.NetworkModel;
import org.dav95s.openNTRIP.protocols.ntrip.NtripResponse;

public class NtripNoneAuthHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Registry registry;

    public NtripNoneAuthHandler(Registry registry) {
        this.registry = registry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        ctx.writeAndFlush(NtripResponse.OK_MESSAGE);

        String networkName = ctx.channel().attr(ChannelState.NETWORK).get();
        NetworkModel networkModel = registry.networks.get(networkName);
        networkModel.channelGroup.add(ctx.channel());

        ctx.channel().attr(ChannelState.AUTHENTICATION).set(true);
    }
}
