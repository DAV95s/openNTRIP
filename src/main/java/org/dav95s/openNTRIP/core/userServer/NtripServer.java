package org.dav95s.openNTRIP.core.userServer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import org.dav95s.openNTRIP.TransportTypeHolder;
import org.dav95s.openNTRIP.commons.Registry;
import org.dav95s.openNTRIP.core.BaseServer;
import org.dav95s.openNTRIP.core.userServer.handlers.UserAuthHandler;
import org.dav95s.openNTRIP.database.repository.MountpointRepository;
import org.dav95s.openNTRIP.database.repository.UserRepository;
import org.dav95s.openNTRIP.utils.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NtripServer extends BaseServer {
    private final static Logger logger = LoggerFactory.getLogger(NtripServer.class.getName());

    MountpointRepository mountpointRepository = new MountpointRepository();
    UserRepository userRepository = new UserRepository();

    private final ChannelInitializer<SocketChannel> channelInitializer;

    public NtripServer(ServerProperties serverProperties, Registry registry, TransportTypeHolder transportType) {
        super(serverProperties.getIntProperty("ntrip.default.port"), transportType);

        int stationTimeoutSecs = serverProperties.getIntProperty("station.socket.idle.timeout", 0);

        channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("ByteArrayEncoder", new ByteArrayEncoder());
                pipeline.addLast("HttpInboundHandler", new HttpRequestDecoder());
                pipeline.addLast("HttpObjectAggregator ", new HttpObjectAggregator(2048,true));
                pipeline.addLast("Authentication", new UserAuthHandler(registry, mountpointRepository, userRepository));
            }
        };

        logger.debug("hard.socket.idle.timeout = {}", stationTimeoutSecs);

        logger.info("Plain tcp/ip reference station server port {}.", port);
    }

    @Override
    protected ChannelInitializer<SocketChannel> getChannelInitializer() {
        return channelInitializer;
    }
}
