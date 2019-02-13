package com.faceword.nio.utils.test;



import com.faceword.nio.business.NettyChannelMap;
import com.faceword.nio.business.NettyServerProcess;
import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.business.entity.LocalBuff;
import com.faceword.nio.business.utils.NettyServerUtils;
import com.faceword.nio.business.utils.ResponceMessageUtils;
import com.faceword.nio.business.utils.ServerWriteUtils;
import com.faceword.nio.config.Constans;
import com.faceword.nio.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: zyong
 * @Date: 2018/11/1 10:42
 * @Version 1.0
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    public static ChannelHandlerContext context = null;

    //利用写空闲发送心跳检测消息
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case WRITER_IDLE:
                    Message pingMsg=new Message(MsgType.PING);
                    ctx.writeAndFlush(JSON.toJSON(pingMsg));
                    //System.out.println("send ping to server----------");
                    break;
                default:
                    break;
            }
        }
    }
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelRegistered");
        ctx.writeAndFlush("ok");
    }


    //定义local线程对象 保存多次的包信息
    private ThreadLocal<LocalBuff> localBuffThreadLocal = new ThreadLocal<LocalBuff>();



    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {

        String clientId = ctx.channel().id().asLongText();
        LocalBuff localBuff = localBuffThreadLocal.get();
        ServerReceiveMessage serverReceiveMessage = localBuff == null ? null : localBuff.getServerReceiveMessage();
        //初始化读取包头
        if( localBuff ==null || localBuff.getLen() == 0  ){
            log.info("------read package  info-----");
            localBuff = NettyServerProcess.readInit( byteBuf );

            localBuffThreadLocal.set(localBuff);
        }
        //每次读取buff里面所有的内容
        localBuff.getBuff().append(ByteBufUtils.readChannelMessage( byteBuf  ));
        if( localBuff.isEquals()){

            System.out.println(localBuff.getBuff().toString());
        }

    }

  //  private static ExecutorService threadPool = Executors.newFixedThreadPool(2);
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("channelActive");

        for (;;){
//            threadPool.execute(new Runnable() {
//                @Override
//                public void run() {
//                    ByteBuf byteBuf = ctx.alloc().ioBuffer();
//                    byteBuf.writeLong(System.currentTimeMillis());
//                    ctx.channel().writeAndFlush(byteBuf);
//
//                }
//
//            });
            try {
                Thread.sleep(2000);
            }catch (Exception e){
                e.printStackTrace();
            }

        }


    }


}