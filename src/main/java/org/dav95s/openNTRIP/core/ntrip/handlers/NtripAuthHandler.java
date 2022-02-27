package org.dav95s.openNTRIP.core.ntrip.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import org.dav95s.openNTRIP.commons.StationRegistry;
import org.dav95s.openNTRIP.database.models.MountPointModel;
import org.dav95s.openNTRIP.database.models.SourceTableModel;
import org.dav95s.openNTRIP.database.models.UserModel;
import org.dav95s.openNTRIP.database.models.assets.Authenticator;
import org.dav95s.openNTRIP.utils.BasicAuthParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NtripAuthHandler extends SimpleChannelInboundHandler<HttpRequest> {
    final static private Logger logger = LoggerFactory.getLogger(NtripAuthHandler.class.getName());
    private final StationRegistry stationRegistry;

    private String request;
    private boolean isLogged = false;
    private UserModel model;
    private MountPointModel mountPointModel;

    public NtripAuthHandler(StationRegistry stationRegistry) {
        this.stationRegistry = stationRegistry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
        if (msg.method() != HttpMethod.GET) {
            ctx.close();
            return;
        }

        if (request.isEmpty()) {
            sendSourceTable(ctx);
            return;
        }

        this.request = msg.uri().substring(1);
        mountPointModel = new MountPointModel(request);

        if (mountPointModel.read()) {
            Authenticator authenticator = mountPointModel.getAuthenticator();
            if (authenticator == Authenticator.None) {
                this.isLogged = true;
            } else if (authenticator == Authenticator.Basic) {
                BasicAuthParser parser = new BasicAuthParser(msg.headers().get("Authorization"));
                this.model = new UserModel(parser.account);
                if (model.read()) {
                    this.isLogged = true;
                }
            } else if (authenticator == Authenticator.Digest) {
                logger.warn("Digest authentication not supported!");
            }

            if (isLogged) {
                if (mountPointModel.isNmea()) {

                } else {
                    
                }
            }

        } else {
            sendSourceTable(ctx);
            return;
        }


    }

    private void sendSourceTable(ChannelHandlerContext ctx) {
        SourceTableModel sourceTableModel = new SourceTableModel();
        ctx.write(sourceTableModel.getSourcetable());
    }

}
