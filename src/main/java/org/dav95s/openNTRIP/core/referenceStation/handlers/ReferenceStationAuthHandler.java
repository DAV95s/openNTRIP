package org.dav95s.openNTRIP.core.referenceStation.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.dav95s.openNTRIP.commons.StationRegistry;
import org.dav95s.openNTRIP.core.referenceStation.ReferenceStation;
import org.dav95s.openNTRIP.database.models.ReferenceStationModel;

import java.nio.charset.StandardCharsets;

public class ReferenceStationAuthHandler extends SimpleChannelInboundHandler<byte[]> {
    private final ByteBuf OK_MESSAGE = Unpooled.copiedBuffer("ICY 200 OK\r\n", StandardCharsets.ISO_8859_1);
    private final ByteBuf BAD_PASSWORD = Unpooled.copiedBuffer("ERROR - Bad Password\r\n", StandardCharsets.ISO_8859_1);

    private StationRegistry registry;

    public ReferenceStationAuthHandler(StationRegistry stationRegistry) {
        this.registry = stationRegistry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        String message = new String(msg);
        String[] params = message.split("\r\n");
        if (params.length < 1) {
            ctx.close();
        }

        String[] authData = params[0].split(" ", 3);
        if (authData.length != 3 || !authData[0].equals("SOURCE")) {
            ctx.close();
        }

        String pass = authData[1];
        String acc = authData[2];

        ReferenceStationModel stationModel = new ReferenceStationModel(acc);
        ReferenceStation referenceStation = new ReferenceStation(stationModel, registry);
        //if station  exists or password is correct
        if (stationModel.read() || stationModel.password.equals(pass)) {
            ctx.writeAndFlush(OK_MESSAGE);
            ctx.pipeline().remove("StationAuthHandler");
            ctx.pipeline().addLast("RtcmStreamHandler", new Rtcm3InboundHandler(referenceStation));
            registry.addReferenceStation(stationModel.name, referenceStation);
        } else {
            ctx.writeAndFlush(BAD_PASSWORD);
            ctx.close();
        }
    }

}
