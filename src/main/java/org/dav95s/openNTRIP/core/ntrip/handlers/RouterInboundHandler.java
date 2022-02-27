package org.dav95s.openNTRIP.core.ntrip.handlers;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.string.StringDecoder;
import org.dav95s.openNTRIP.database.models.MountPointModel;
import org.dav95s.openNTRIP.database.models.SourceTableModel;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class RouterInboundHandler extends SimpleChannelInboundHandler<HttpRequest> {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) {

        if (msg.decoderResult().isFailure() || msg.method() != HttpMethod.GET) {
            ctx.close();
            return;
        }

        String uri = msg.uri().substring(1);

        MountPointModel model = new MountPointModel(uri);
        boolean isExists = model.read();

        if (uri.isEmpty() || !isExists) { // mountpoint not exists, send sourcetable
            SourceTableModel sourcetable = new SourceTableModel();
            String response = sourcetable.getSourcetable();
            ctx.writeAndFlush(response);
            ctx.close();
            return;
        }


//        if (model.getAuthenticator() instanceof None) {
//
//        } else if (model.getAuthenticator() instanceof Basic) {
//            if (msg.headers().contains("Authorization")) {
//                UserModel user = new UserModel();
//
//                String basicAuthHeader = msg.headers().get("Authorization");
//                if (basicAuthHeader.contains("Basic ")) {
//                    String base64 = basicAuthHeader.replace("Basic ", "");
//                    Base64.getDecoder().decode(base64.getBytes(StandardCharsets.ISO_8859_1));
//                }
//                String[] basicAndHash = basicAuthHeader.split(" ");
//
////                String basicAuthHeader =
////                        "Basic " + Base64.getEncoder()
////                                .encodeToString((model.getName() + ":" + model.)
////                                        .getBytes(StandardCharsets.ISO_8859_1));
//            } else {
//                ctx.writeAndFlush("ERROR - Bad Password\r\n");
//            }
//        } else if (model.getAuthenticator() instanceof Digest) { //not supported
//            ctx.close();
//            return;
//        }


        ctx.channel().writeAndFlush(ctx.alloc().buffer(14).writeBytes("ICY 200 OK\r\n".getBytes(StandardCharsets.US_ASCII)));

        ctx.pipeline().remove("HttpDecoder");
        ctx.pipeline().remove("Router");
        ctx.pipeline().addFirst(new StringDecoder());

    }

    private void sourceAuthentication(HttpRequest request) {
        System.out.println(request.decoderResult().toString());
    }

    private void writeResponse(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE,
                Unpooled.EMPTY_BUFFER);
        ctx.write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


}
