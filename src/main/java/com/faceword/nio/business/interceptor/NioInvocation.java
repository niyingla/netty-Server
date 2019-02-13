package com.faceword.nio.business.interceptor;

import com.faceword.nio.business.entity.LocalBuff;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: zyong
 * @Date: 2018/11/7 9:26
 * @Version 1.0
 */
@Slf4j
public abstract class NioInvocation {

    public abstract boolean invoke(ChannelHandlerContext ctx,
                  ByteBuf byteBuf ,
                  LocalBuff localBuff ,
                  ThreadLocal<LocalBuff> localBuffThreadLocal);

    
}
