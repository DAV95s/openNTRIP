package org.dav95s.openNTRIP.core.referenceStation.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.dav95s.openNTRIP.commons.Message;
import org.dav95s.openNTRIP.core.referenceStation.ReferenceStation;
import org.dav95s.openNTRIP.protocols.rtcm.Rtcm3Separator;

import java.util.ArrayList;

public class Rtcm3InboundHandler extends SimpleChannelInboundHandler<byte[]> {

    private final ReferenceStation referenceStation;

    public Rtcm3InboundHandler(ReferenceStation referenceStation) {
        this.referenceStation = referenceStation;
    }

    int totalLengthBytes = 0;
    int totalLengthMessages = 0;

    private final Rtcm3Separator separator = new Rtcm3Separator();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] bytes) throws Exception {
        ArrayList<Message> messages = separator.separate(bytes);

        totalLengthBytes += bytes.length;
        for (Message msg : messages) {
            totalLengthMessages += msg.bytes.length;
        }

        System.out.println("TOTAL BYTES: " + totalLengthBytes);
        System.out.println("TOTAL IN MSG " + totalLengthMessages);

        this.referenceStation.write(bytes);
    }

}
