package com.faceword.nio.business.utils;


import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.config.Constans;
import com.faceword.nio.utils.ByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.nio.charset.Charset;

/**
 * @Author: zyong
 * @Date: 2018/11/6 10:54
 * @Version 1.0
 * 服务端往客户端写回数据工具类
 */
@Slf4j
public class ServerWriteUtils {

    public static void writeHeadAndBody(byte [] obytes , String body , ChannelHandlerContext ctx) {
        ByteBuf buffer = ctx.alloc().buffer();
        if(body == null){
            log.warn("body is null !!!!!!!!!!!!");
            return ;
        }
        if(obytes == null){

            log.warn("obytes is null !!!!!!!!!!!");
            return ;
        }
        byte[] bytes = ByteUtils.byteMerger(obytes ,body.getBytes(Charset.forName("utf-8"))) ;
        // 3. 填充数据到 ByteBuf
        buffer.writeBytes(bytes);
        ctx.channel().writeAndFlush(buffer);
    }

    /**
     * 只发送包头 ， 用于服务端给客户端发送心跳包
     * @param obytes
     * @param ctx
     */
    public static void writeHead(byte [] obytes ,ChannelHandlerContext ctx) {
        ByteBuf buffer = ctx.alloc().buffer();
        if(obytes == null){
            log.warn("obytes is null !!!!!!!!!!!!");
            return ;
        }
        // 3. 填充数据到 ByteBuf
        buffer.writeBytes(obytes);
        ChannelFuture future = ctx.channel().writeAndFlush(buffer);

        log.info("send heartbeat message ok !! isDone:{}" , future.isDone() );
    }

    /**
     * 发送包头 + 字符包体 ， 用于服务端给客户端发送心跳包
     * @param mode
     * @param ctx
     */
    public static void writeHeadAndBody(short mode,String body, ChannelHandlerContext ctx) {
        log.info("---writeHeadAndBody---");
        ServerWriteUtils.writeHeadAndBody(ByteUtils.headConver(Constans.MARK,Constans.VER,
                Constans.DEV_TYPE, mode ,Constans.serial,body.length()),body,ctx);
    }


    public static void writeHeadAndBody(short mode,String body, SocketChannel socketChannel) {
        log.info("---writeHeadAndBody---");
        if(socketChannel == null){
            log.info("socketChannel is null!!");
            return ;
        }
        byte [] headBytes = ByteUtils.headConver(Constans.MARK,Constans.VER,
                Constans.DEV_TYPE, mode ,Constans.serial,body.length());
        ByteBuf byteBuf = Unpooled.buffer(body.length());
        byte[] nBytes = ByteUtils.byteMerger(headBytes ,body.getBytes(Charset.forName("utf-8"))) ;
        byteBuf.writeBytes(nBytes);
        socketChannel.writeAndFlush(byteBuf);
    }

    /**
     * 服务端主动往客户端写入信息
     * @param socketChannel
     * @param body
     */
    public static void serverWriteToClient(SocketChannel socketChannel , String body ){
        if(socketChannel == null){
            log.info("socketChannel is null!!!!!!!");
            return ;
        }
        if(body == null){
            log.info("body is null!!!!!!!");
            return ;
        }
        ByteBuf byteBuf = Unpooled.buffer(body.length());
        byteBuf.writeBytes(body.getBytes());
        socketChannel.writeAndFlush(byteBuf);
    }

    public static void writeHeadAndBody( ServerReceiveMessage serverReceiveMessage ,
                                         ChannelHandlerContext ctx){
        if(serverReceiveMessage == null){
            log.info("serverReceiveMessage is null!!!");
            return;
        }
        String body = ResponceMessageUtils.getSuccessBody();
        ServerWriteUtils.writeHeadAndBody(
                ByteUtils.headConver(serverReceiveMessage.getMark(),serverReceiveMessage.getVer(),
                        serverReceiveMessage.getDevType(),
                        (short) (serverReceiveMessage.getMode()+1),
                        serverReceiveMessage.getSerial(),
                        body.length()) ,
                body ,ctx);
    }

    public static void writeHeadAndBody( ServerReceiveMessage serverReceiveMessage ,String body ,
                                         ChannelHandlerContext ctx){
        if(serverReceiveMessage == null){
            log.info("serverReceiveMessage is null!!!");
            return;
        }
        if(StringUtils.isBlank(body)){
            log.info("body is null!!!");
            return;
        }
        ServerWriteUtils.writeHeadAndBody(
                ByteUtils.headConver(serverReceiveMessage.getMark(),serverReceiveMessage.getVer(),
                        serverReceiveMessage.getDevType(),
                        (short) (serverReceiveMessage.getMode()+1),
                        serverReceiveMessage.getSerial(),
                        body.length()) ,
                body ,ctx);
    }

    public static void writeHead( ServerReceiveMessage serverReceiveMessage ,
                                         ChannelHandlerContext ctx) {
        if (serverReceiveMessage == null) {
            log.info("serverReceiveMessage is null!!!");
            return;
        }

        ServerWriteUtils.writeHead(
                ByteUtils.headConver(serverReceiveMessage.getMark(), serverReceiveMessage.getVer(),
                        serverReceiveMessage.getDevType(),
                        (short) (serverReceiveMessage.getMode() + 1),
                        serverReceiveMessage.getSerial(),
                        0)
                , ctx);

    }

     public static void writeHead(short mode , SocketChannel socketChannel){
          if(socketChannel ==null){
              log.info("socketChannel is not null !!!!!");
              return ;
          }
          byte [] bytes = ByteUtils.headConver(Constans.MARK,Constans.VER,
                            Constans.DEV_TYPE,
                            mode,
                            Constans.serial,
                            0);
         ByteBuf byteBuf = Unpooled.buffer(bytes.length);
         byteBuf.writeBytes( bytes );
         socketChannel.writeAndFlush(byteBuf);

    }
}
