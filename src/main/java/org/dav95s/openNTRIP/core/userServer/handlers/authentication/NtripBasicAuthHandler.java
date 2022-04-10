package org.dav95s.openNTRIP.core.userServer.handlers.authentication;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.dav95s.openNTRIP.commons.Registry;
import org.dav95s.openNTRIP.core.ChannelState;
import org.dav95s.openNTRIP.database.modelsV2.NetworkModel;
import org.dav95s.openNTRIP.database.modelsV2.UserModel;
import org.dav95s.openNTRIP.database.repository.UserRepository;
import org.dav95s.openNTRIP.exception.UserAuthorizationException;
import org.dav95s.openNTRIP.protocols.ntrip.NtripResponse;
import org.dav95s.openNTRIP.users.passwords.BCrypt;
import org.dav95s.openNTRIP.utils.BasicAuthParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NtripBasicAuthHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final static Logger logger = LoggerFactory.getLogger(NtripBasicAuthHandler.class.getName());

    private final Registry registry;
    private final UserRepository userRepository;

    public NtripBasicAuthHandler(Registry registry, UserRepository userRepository) {
        this.registry = registry;
        this.userRepository = userRepository;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        String authorizationString = msg.headers().get("Authorization");

        BasicAuthParser parser = new BasicAuthParser(authorizationString);

        UserModel userModel = userRepository.getUserByUsername(parser.account);

        if (userModel == null) {
            throw new UserAuthorizationException("Bad password!");
        }

        BCrypt bCrypt = new BCrypt();

        if (bCrypt.compare(userModel.password, parser.password)) {
            ctx.writeAndFlush(NtripResponse.OK_MESSAGE);

            String networkName = ctx.channel().attr(ChannelState.NETWORK).get();
            NetworkModel networkModel = registry.networks.get(networkName);
            networkModel.channelGroup.add(ctx.channel());

            ctx.channel().attr(ChannelState.AUTHENTICATION).set(true);
        }else {
            ctx.channel().attr(ChannelState.AUTHENTICATION).set(false);
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof UserAuthorizationException) {
            logger.info("Channel ID:" + ctx.channel().id() + " " + cause.getMessage(), cause);
            ctx.writeAndFlush(NtripResponse.BAD_PASSWORD);
            ctx.close();
        }
    }
}
