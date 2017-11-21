package com.example;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private static Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf) msg; // (1)
        try {
            LOGGER.info("********** channelRead : " + m.toString());
            ctx.close();
        } finally {
            m.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.info("********** exceptionCaught");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("********** channelActive");
        super.channelActive(ctx);
        LOGGER.info("********** NettyClient.msgCount = " + NettyClient.msgCount);
        for (int i = 0; i < NettyClient.msgCount; i++) {
            ctx.writeAndFlush("");
        }
        LOGGER.info("********** writeAndFlush " + NettyClient.msgCount + " messages.");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("********** channelInactive");
        super.channelInactive(ctx);
    }
}
