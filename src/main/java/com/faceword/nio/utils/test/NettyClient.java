package com.faceword.nio.utils.test;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import io.netty.channel.socket.SocketChannel;

import java.nio.charset.Charset;

/**
 * @Author: zyong
 * @Date: 2018/10/31 16:20
 * @Version 1.0
 */
@Slf4j
public class NettyClient {

    private int port;
    private String host;
    public SocketChannel socketChannel;
    private static final EventExecutorGroup group = new DefaultEventExecutorGroup(20);
    public NettyClient(int port, String host) {
        this.port = port;
        this.host = host;
        start();
    }
    private void start(){
        ChannelFuture future = null;
        try {
            EventLoopGroup eventLoopGroup=new NioEventLoopGroup();
            Bootstrap bootstrap=new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
            bootstrap.group(eventLoopGroup);
            bootstrap.remoteAddress(host,port);
            Bootstrap handler = bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new IdleStateHandler(20, 10, 0));
//                    socketChannel.pipeline().addLast(new StringEncoder(Charset.forName("UTF-8")));
//                    socketChannel.pipeline().addLast(new StringDecoder(Charset.forName("UTF-8")));
                    socketChannel.pipeline().addLast(new NettyClientHandler());
                }
            });

            future = bootstrap.connect(host,port).sync();
            //future.addListener(ChannelFutureListener.CLOSE);
            if (future.isSuccess()) {
                socketChannel = (SocketChannel)future.channel();



                socketChannel.writeAndFlush("ok");

                System.out.println("connect server  成功---------");


            }else{
                System.out.println("连接失败！");
                System.out.println("准备重连！");
                start();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }finally{
//          if(null != future){
//              if(null != future.channel() && future.channel().isOpen()){
//                  future.channel().close();
//              }
//          }
//          System.out.println("准备重连！");
//          start();
        }
    }
    public static void main(String[]args) throws InterruptedException {
        NettyClient bootstrap = new NettyClient(12345,"192.168.0.122");



    }
}  

