package com.faceword.nio.utils.test;


import com.faceword.nio.business.HeartBeatServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @Author: zyong
 * @Date: 2018/11/1 10:44
 * @Version 1.0
 */
public class NettyServer {

    //发送数据包长度 = 长度域的值 + lengthFieldOffset + lengthFieldLength + lengthAdjustment

    /**
     * 发送的数据帧最大长度
     */
    private static final int MAX_FRAME_LENGTH = 1024 * 1024;
    /**
     * 定义长度域位于发送的字节数组中的下标。换句话说：发送的字节数组中下标为${lengthFieldOffset}的地方是长度域的开始地方
     */
    private static final int LENGTH_FIELD_LENGTH = 50;
    /**
     * 发送字节数组bytes时, 字节数组bytes[lengthFieldOffset, lengthFieldOffset+lengthFieldLength]域对应于的定义长度域部分
     */
    private static final int LENGTH_FIELD_OFFSET = 50;
    /**
     *  满足公式: 发送的字节数组bytes.length - lengthFieldLength = bytes[lengthFieldOffset, lengthFieldOffset+lengthFieldLength] + lengthFieldOffset + lengthAdjustment 
     */
    private static final int LENGTH_ADJUSTMENT = 50;
    /**
     * 接收到的发送数据包，去除前initialBytesToStrip位
     */
    private static final int INITIAL_BYTES_TO_STRIP = 50;

    private int port;
    public SocketChannel socketChannel;

    public NettyServer(int port) throws InterruptedException {
        this.port = port;
        bind();
    }

    private void bind() throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker);
        bootstrap.channel(NioServerSocketChannel.class);
        //队列的大小
        bootstrap.option(ChannelOption.SO_BACKLOG, 1000);
        //通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        //保持长连接状态
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();
                //字符串类解析
//                p.addLast(new StringEncoder(Charset.forName("UTF-8")));
//                p.addLast(new StringDecoder(Charset.forName("UTF-8")));
               // p.addLast(new ServerDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_LENGTH,LENGTH_FIELD_OFFSET,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP,true));
                //p.addLast(new NettyServerHandler());
                //p.addLast(new FixedLengthFrameDecoder( MAX_FRAME_LENGTH ));
                p.addLast( new IdleStateHandler(5, 5, 60, TimeUnit.SECONDS) );

                p.addLast( ServerBusinessHandler.INSTANCE);
                //自定义心跳时间的逻辑
                p.addLast( new HeartBeatServerHandler() );
            }
        });
        ChannelFuture f = bootstrap.bind(port).sync();
        if (f.isSuccess()) {
            System.out.println("server start---------------");
        }
    }

    public static void main(String[] args) throws InterruptedException {

        new NettyServer(12345);
    }
}