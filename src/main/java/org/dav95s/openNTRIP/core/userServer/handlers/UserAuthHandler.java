package org.dav95s.openNTRIP.core.userServer.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import org.dav95s.openNTRIP.commons.Registry;
import org.dav95s.openNTRIP.core.ChannelState;
import org.dav95s.openNTRIP.core.userServer.handlers.authentication.NtripBasicAuthHandler;
import org.dav95s.openNTRIP.core.userServer.handlers.authentication.NtripNoneAuthHandler;
import org.dav95s.openNTRIP.database.models.SourceTableModel;
import org.dav95s.openNTRIP.database.modelsV2.MountpointModel;
import org.dav95s.openNTRIP.database.repository.MountpointRepository;
import org.dav95s.openNTRIP.database.repository.UserRepository;
import org.dav95s.openNTRIP.exception.MountpointNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class UserAuthHandler extends ChannelInboundHandlerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(UserAuthHandler.class.getName());

    private final MountpointRepository mountpointRepository;
    private final UserRepository userRepository;
    private final Registry registry;

    public UserAuthHandler(Registry registry, MountpointRepository mountpointRepository, UserRepository userRepository) {
        this.mountpointRepository = mountpointRepository;
        this.userRepository = userRepository;
        this.registry = registry;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        assert msg instanceof FullHttpRequest;

        FullHttpRequest message = (FullHttpRequest) msg;

        if (message.method() != HttpMethod.GET) {
            throw new IllegalArgumentException("Only GET messages");
        }

        String request = message.uri().substring(1);

        MountpointModel mountpoint = mountpointRepository.getMountpoint(request);

        if (mountpoint == null) {
            throw new MountpointNotFound(request + " Requested reference station not exists!");
        }

        ctx.channel().attr(ChannelState.MOUNTPOINT).set(mountpoint);
        ctx.channel().attr(ChannelState.NETWORK).set(mountpoint.network);

        ctx.channel().pipeline().remove(this);

        switch (mountpoint.authenticator) {
            case Digest:
                logger.warn("Digest authentication not supported!");
                ctx.close();
                break;

            case Basic:
                SimpleChannelInboundHandler<FullHttpRequest> authHandler = new NtripBasicAuthHandler(registry, userRepository);
                authHandler.channelRead(ctx, msg);
                break;

            case None:
                ctx.pipeline().addLast(new NtripNoneAuthHandler(registry));
                break;
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IllegalArgumentException) {
            logger.info("Channel ID:" + ctx.channel().id() + " " + cause.getMessage(), cause);
            ctx.close();
        } else if (cause instanceof MountpointNotFound) {
            logger.info("Channel ID:" + ctx.channel().id() + " " + cause.getMessage(), cause);
            sendSourceTable(ctx);
        }

    }

    private void sendSourceTable(ChannelHandlerContext ctx) {
        SourceTableModel sourceTableModel = new SourceTableModel();
        ctx.writeAndFlush(sourceTableModel.getSourcetable().getBytes(StandardCharsets.US_ASCII));
        ctx.close();
    }
}
