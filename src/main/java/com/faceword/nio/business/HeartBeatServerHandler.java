package com.faceword.nio.business;


import com.faceword.nio.business.utils.NettyServerUtils;
import com.faceword.nio.business.utils.ServerWriteUtils;
import com.faceword.nio.config.Constans;
import com.faceword.nio.utils.ByteUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: zyong
 * @Date: 2018/10/31 10:11
 * @Version 1.0
 */
@Slf4j
@ChannelHandler.Sharable
public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {

    public static final  long readerIdleTime = 15 ;

    public static  final long writerIdleTime = 15;
    /**
     * 配置心跳机制，如果客户端在指定的时间没有发送数据时，会触发该事件
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state().equals( IdleState.READER_IDLE  )){

                String clientId = NettyServerUtils.getClientId(ctx);
                if( NettyChannelMap.isNormalLink(clientId)  ){
                    log.info("----------设备端心跳【断开】链接--------------");
                    NettyServerProcess.closeCtx(ctx);
                    log.info(" #################释放设备对应资源################### ");
                }
            }else if(event.state().equals(IdleState.WRITER_IDLE)){
                //服务端往客户端写空闲 每60秒发送一个心跳包给客户端
                ServerWriteUtils.writeHead(ByteUtils.headConver(Constans.MARK,Constans.VER,
                        Constans.DEV_TYPE,Constans.MODE,Constans.serial,Constans.length ) ,ctx);
            }else if(event.state().equals(IdleState.ALL_IDLE)){
                //读写同时出现空闲
               // log.info("读写同时出现空闲");
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        log.info("------heart 接收信息 -----------");

    }
}