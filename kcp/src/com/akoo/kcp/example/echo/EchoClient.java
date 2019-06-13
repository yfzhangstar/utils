package com.akoo.kcp.example.echo;

import com.akoo.kcp.netty.UkcpChannel;
import com.akoo.kcp.netty.UkcpClientChannel;
import com.akoo.kcp.netty.ChannelOptionHelper;
import com.akoo.kcp.netty.UkcpChannelOption;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Sends one message when a connection is open and echoes back any received
 * data to the server.
 *
 * @author <a href="mailto:szhnet@gmail.com">szh</a>
 */
public final class EchoClient {

    static final int CONV = Integer.parseInt(System.getProperty("conv", "10"));
    static final String HOST = System.getProperty("host", "192.168.2.88");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "10000"));
    static final int CLIENT_TYPE = 100;


    static final int STR_LEN = 500;
    public static void main(String[] args) throws Exception {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(UkcpClientChannel.class)
                    .handler(new ChannelInitializer<UkcpChannel>() {
                        @Override
                        public void initChannel(UkcpChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new EchoClientHandler());
                        }
                    });
            ChannelOptionHelper.nodelay(b, true, 20, 2, true)
                    .option(UkcpChannelOption.UKCP_MTU, 512);

            // Start the client.
            ChannelFuture f = b.connect(HOST, PORT).sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }

}
