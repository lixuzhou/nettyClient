package com.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class NettyClient
{

    private static Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

    public static int msgCount = 0;
    private static int clientCount = 1;

    @SuppressWarnings("all")
    public static void main(String[] args) throws Exception {
        LOGGER.info("********** start...");

        if (null == args || args.length < 2) {
            return;
        }
        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
        if(args.length > 2) {clientCount = Integer.parseInt(args[2]);}
        if(args.length > 3) {msgCount = Integer.parseInt(args[3]);}

        for (int i = 0; i < clientCount; i++) {
            final int clientIndex = i;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    EventLoopGroup workerGroup = new NioEventLoopGroup();
                    try {
                        Bootstrap b = new Bootstrap(); // (1)
                        b.group(workerGroup); // (2)
                        b.channel(NioSocketChannel.class); // (3)
                        b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
                        b.handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new ClientHandler());
                            }
                        });

                        // Start the client.
                        ChannelFuture f = b.connect(host, port).sync(); // (5)
                        LOGGER.info("********** fire connect - " + clientIndex);
                        // Wait until the connection is closed.
                        f.channel().closeFuture().sync();
                        LOGGER.info("********** closeFuture - " + clientIndex);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        workerGroup.shutdownGracefully();
                        LOGGER.info("********** shutdownGracefully - " + clientIndex);
                    }
                }
            });
            t.start();
        }

    }
}
