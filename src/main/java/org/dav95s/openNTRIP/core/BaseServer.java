package org.dav95s.openNTRIP.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import org.dav95s.openNTRIP.TransportTypeHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseServer implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(BaseServer.class.getName());
    protected final int port;
    private final TransportTypeHolder transportTypeHolder;

    private Channel channel;

    protected BaseServer(int port, TransportTypeHolder transportTypeHolder) {
        this.port = port;
        this.transportTypeHolder = transportTypeHolder;
    }

    @Override
    public void run() {
        if (transportTypeHolder.epollEnabled) {
            logger.warn("Native epoll transport enabled.");
        }
        buildServerAndRun(
                transportTypeHolder.bossGroup,
                transportTypeHolder.workerGroup,
                transportTypeHolder.channelClass
        );
    }

    private void buildServerAndRun(EventLoopGroup bossGroup, EventLoopGroup workerGroup,
                                   Class<? extends ServerChannel> channelClass) {

        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(bossGroup, workerGroup)
                    .channel(channelClass)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(getChannelInitializer());

            ChannelFuture channelFuture = b.bind(port).sync();

            this.channel = channelFuture.channel();
            this.channel.closeFuture().sync();
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    protected abstract ChannelInitializer<SocketChannel> getChannelInitializer();

    public void stop() {
        if (channel != null) {
            channel.close().awaitUninterruptibly();
        }
    }
}
