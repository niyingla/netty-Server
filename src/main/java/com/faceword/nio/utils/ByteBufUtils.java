package com.faceword.nio.utils;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: zyong
 * @Date: 2018/11/2 11:13
 * @Version 1.0
 * 给予ByteBuf操作的工具类
 */
@Slf4j
public class ByteBufUtils {

    public static String readChannelMessage( ByteBuf byteBuf) {
        byte[] req = new byte[byteBuf.readableBytes()];
        //将buf中的数据读取到req中
        byteBuf.readBytes( req );
        try {
            String body = new String(req, "UTF-8").substring(0, req.length );
            //log.warn("解析的字符length =  {}" ,  req.length);
            //log.warn("解析的字符为 【{}】" , body);
            return body;
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            byteBuf.clear();
        }
       return "";
    }

    public static String readChannelMessage( ByteBuf byteBuf ,int length) {
        byte[] req = new byte[length];
        //将buf中的数据读取到req中
        byteBuf.readBytes(req);
        try {
            String body = new String(req, "UTF-8").substring(0, length );
            //log.warn("解析的字符length =  {}" ,  req.length);
            //log.warn("解析的字符为 【{}】" , body);
            return body;
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            byteBuf.clear();
        }
        return "";
    }

    public static String readChannelMessage(ByteBuf byteBuf , Integer readLen){
        ByteBuf buf = byteBuf.readBytes(readLen);
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        try {
          String body =   new String( req, "UTF-8");
            //log.warn("解析的字符length =  {}" ,  req.length);
           // log.warn("解析的字符为 【{}】" , body);
           return body;
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            byteBuf.clear();
        }
        return "";
    }
}
