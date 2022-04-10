package org.dav95s.openNTRIP.core.stationsServer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.dav95s.openNTRIP.TransportTypeHolder;
import org.dav95s.openNTRIP.commons.Registry;
import org.dav95s.openNTRIP.core.BaseServer;
import org.dav95s.openNTRIP.core.stationsServer.handlers.ReferenceStationAuthHandler;
import org.dav95s.openNTRIP.database.repository.ReferenceStationRepository;
import org.dav95s.openNTRIP.utils.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReferenceStationServer extends BaseServer {
    private final static Logger logger = LoggerFactory.getLogger(ReferenceStationServer.class.getName());

    private final ChannelInitializer<SocketChannel> channelInitializer;

    public ReferenceStationServer(ServerProperties serverProperties, Registry registry,
                                  TransportTypeHolder transportType, ReferenceStationRepository referenceStationRepository) {

        super(serverProperties.getIntProperty("station.default.port"), transportType);

        int stationTimeoutSecs = serverProperties.getIntProperty("station.socket.idle.timeout", 0);

        channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                //non-sharable handlers
                if (stationTimeoutSecs > 0) {
                    pipeline.addLast(new ReadTimeoutHandler(stationTimeoutSecs));
                }

                pipeline.addLast("ByteArrayEncoder", new ByteArrayEncoder());
                pipeline.addLast("ByteArrayDecoder", new ByteArrayDecoder());
                pipeline.addLast("StationAuthHandler", new ReferenceStationAuthHandler(registry, referenceStationRepository));
            }
        };

        logger.debug("hard.socket.idle.timeout = {}", stationTimeoutSecs);

        logger.info("Plain tcp/ip reference station server port {}.", port);
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return channelInitializer;
    }

    @Override
    public void stop() {
        logger.info("Shutting down default server...");
        super.stop();
    }
}
