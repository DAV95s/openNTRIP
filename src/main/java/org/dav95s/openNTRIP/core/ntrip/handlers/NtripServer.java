package org.dav95s.openNTRIP.core.ntrip.handlers;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.dav95s.openNTRIP.TransportTypeHolder;
import org.dav95s.openNTRIP.commons.StationRegistry;
import org.dav95s.openNTRIP.core.BaseServer;
import org.dav95s.openNTRIP.utils.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NtripServer extends BaseServer {
    private final static Logger logger = LoggerFactory.getLogger(NtripServer.class.getName());


    private final ChannelInitializer<SocketChannel> channelInitializer;

    public NtripServer(ServerProperties props, StationRegistry stationRegistry, TransportTypeHolder transportType) {
        super(props.getIntProperty("ntrip.default.port"), transportType);

        int stationTimeoutSecs = props.getIntProperty("station.socket.idle.timeout", 0);

        channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                //non-sharable handlers
                if (stationTimeoutSecs > 0) {
                    pipeline.addLast(new ReadTimeoutHandler(stationTimeoutSecs));
                }
                pipeline.addLast("HttpInboundHandler", new HttpRequestDecoder());
                pipeline.addLast("NtripHandler", new NtripAuthHandler(stationRegistry));
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
