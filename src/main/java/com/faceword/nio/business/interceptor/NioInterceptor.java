package com.faceword.nio.business.interceptor;


import com.faceword.nio.business.entity.LocalBuff;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author: zyong
 * @Date: 2018/11/7 9:22
 * @Version 1.0
 * NIO 自定义拦截器
 */
public interface NioInterceptor {

    /**
     * 前置拦截
     * @param
     */
    boolean before( ChannelHandlerContext ctx,
                 ByteBuf byteBuf ,
                 LocalBuff localBuff );




}
