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
 * @Date: 2018/11/10 14:31
 * @Version 1.0
 * 3.4.设置设备时间的接口 设备接收后返回
 *
 *
 * 一般做日志， 记录操作是否成功
 */
@Slf4j
@NoArgsConstructor
@Component(value = "166")
public class ResponceExcute_A5 extends AbstratExcutor{


    /**
     * 服务向设备发送设置时间， 设备返回接收到信息
     * @param ctx
     * @param byteBuf
     * @param localBuff
     * @param localBuffThreadLocal
     */
    @Override
    public void excute(ChannelHandlerContext ctx, ByteBuf byteBuf, LocalBuff localBuff, ThreadLocal<LocalBuff> localBuffThreadLocal) {

        log.info("--------------ResponceExcute_A5----------------" );

        ResponceMessage responceMessage = repeatRead(ctx,byteBuf,localBuff,localBuffThreadLocal, ResponceMessage.class);

    }
}
