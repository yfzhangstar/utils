package com.akoo.kcp.example.echo;

import com.akoo.common.buffer.Helper;
import com.akoo.kcp.netty.Kcp;
import com.akoo.kcp.netty.UkcpChannel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handler implementation for the echo client.
 *
 * @author <a href="mailto:szhnet@gmail.com">szh</a>
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * Creates a client-side handler.
     */
    public EchoClientHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException {
        UkcpChannel kcpCh = (UkcpChannel) ctx.channel();
        kcpCh.conv(EchoClient.CONV); // set conv
        Map req = new HashMap<>();
        req.put("client", EchoClient.CLIENT_TYPE);
        req.put("key", 0);
        for (int i = 0; i < EchoClient.STR_LEN; i++) {
            req.put(i, UUID.randomUUID().toString());
        }
        byte[] data = Helper.mapToBytes(req);
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(data.length);
        System.out.println("req len : " + data.length);
        buf.writeBytes(data);
        buf.writeInt(0);
        ctx.writeAndFlush(buf);
    }

    private static final InternalLogger log = InternalLoggerFactory.getInstance(EchoClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf)msg;
        int len = buf.readInt();
        System.out.println("len : " + len);
        byte[] data = new byte[len];
        buf.readBytes(data);
        try {
            Map result = Helper.mapFromBytes(data);
            int client = (int)result.get("client");
            int key = (int)result.get("key");
            log.info(">>>>>>>> client : " + client + ", key : " + key);
            buf.clear();
//            result.put("client", client);
//            result.put("key", key + 1);
//            byte[] reqData = Helper.mapToBytes(result);
//            buf.writeInt(reqData.length);
//            System.out.println("req len : " + data.length);
//            buf.writeBytes(reqData);
//            ctx.writeAndFlush(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

}
