package com.faceword.nio.business.component;

import com.faceword.nio.business.entity.LocalBuff;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
/**
 * @Author: zyong
 * @Date: 2018/11/7 10:32
 * @Version 1.0
 * 向设备人脸库中添加人脸信息接口 客户端返回的信息
 */
@Slf4j
@Component(value = "172")
@NoArgsConstructor
public class ResponceExcute_AB extends AbstratExcutor{


    /**
     * 服务端向设备人脸库中添加人脸信息接口 后 客户端返回
     * @param ctx
     * @param byteBuf
     * @param localBuff
     * @param localBuffThreadLocal
     */
    @Override
    public void excute(ChannelHandlerContext ctx, ByteBuf byteBuf, LocalBuff localBuff, ThreadLocal<LocalBuff> localBuffThreadLocal) {
        log.info("-------------ResponceExcute_AB----------");

        repeatRead(ctx,byteBuf,localBuff,localBuffThreadLocal,false);

    }


}
