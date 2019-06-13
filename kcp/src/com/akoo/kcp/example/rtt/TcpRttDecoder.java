package com.akoo.kcp.example.rtt;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author <a href="mailto:szhnet@gmail.com">szh</a>
 */
public class TcpRttDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 8) {
            return;
        }

        short dataLen = in.getShort(in.readerIndex() + 6);
        if (in.readableBytes() < dataLen) {
            return;
        }

        ByteBuf msg = in.readRetainedSlice(8 + dataLen);
        out.add(msg);
    }

}
