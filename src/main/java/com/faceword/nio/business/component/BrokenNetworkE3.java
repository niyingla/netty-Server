package com.faceword.nio.business.component;

import com.faceword.nio.business.entity.LocalBuff;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/11/29 10:23
 * @Version 1.0
 * 5.2断网重新连接后人脸及识别结果上报（断网过程中缓存的人脸）
 */
@Slf4j
@Component(value = "227")
@NoArgsConstructor
public class BrokenNetworkE3  extends AbstratExcutor {


    /**
     * 断网重新连接后人脸及识别结果上报
     * @param ctx
     * @param byteBuf
     * @param localBuff
     * @param localBuffThreadLocal
     */
    @Override
    public void excute(ChannelHandlerContext ctx, ByteBuf byteBuf, LocalBuff localBuff, ThreadLocal<LocalBuff> localBuffThreadLocal) {
        //直接调用重复读的方法
        super.repeatRead(ctx,byteBuf,localBuff,localBuffThreadLocal,true);
    }
}
