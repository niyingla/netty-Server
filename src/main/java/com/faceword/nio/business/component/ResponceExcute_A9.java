package com.faceword.nio.business.component;

import com.faceword.nio.business.entity.LocalBuff;
import com.faceword.nio.business.response.ResponceMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/11/7 14:37
 * @Version 1.0
 * 服务器断开连接某个相机 客户端返回
 */
@Slf4j
@NoArgsConstructor
@Component(value = "170")
public class ResponceExcute_A9 extends AbstratExcutor{


    /**
     * 3.6. 服务器断开连接某个设备 客户端返回
     * @param ctx
     * @param byteBuf
     * @param localBuff
     * @param localBuffThreadLocal
     */
    @Override
    public void excute(ChannelHandlerContext ctx, ByteBuf byteBuf, LocalBuff localBuff, ThreadLocal<LocalBuff> localBuffThreadLocal) {
        log.info("---------------ResponceExcute_A9-----------------");
        ResponceMessage responceMessage = repeatRead(ctx,byteBuf,localBuff,localBuffThreadLocal, ResponceMessage.class);

    }
}
