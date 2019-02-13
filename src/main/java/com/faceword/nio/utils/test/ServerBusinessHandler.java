package com.faceword.nio.utils.test;

/**
 * @Author: zyong
 * @Date: 2018/11/28 19:23
 * @Version 1.0
 */
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;



@ChannelHandler.Sharable
public class ServerBusinessHandler extends SimpleChannelInboundHandler<ByteBuf> {
    public static final ChannelHandler INSTANCE = new ServerBusinessHandler();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {


        System.out.println("data");
    }

    protected Object getResult(ByteBuf data) {
        System.out.println( data );

        return data;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // ignore
    }
}
