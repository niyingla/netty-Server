package com.faceword.nio.business.component;


import com.faceword.nio.business.entity.LocalBuff;
import com.faceword.nio.service.MansFaceLogService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/11/7 15:52
 * @Version 1.0
 * 3.9.批量删除设备黑/白人脸库中全部的人脸接口
 */
@Slf4j
@Component(value = "178")
@NoArgsConstructor
public class ResponceExcute_B1 extends AbstratExcutor{


    @Autowired
    private MansFaceLogService mansFaceLogService;

    /**
     * 批量删除设备黑/白人脸库中全部的人脸  设备端返回 ， 此处执行对应的业务
     * @param ctx
     * @param byteBuf
     * @param localBuff
     * @param localBuffThreadLocal
     */
    @Override
    public void excute(ChannelHandlerContext ctx, ByteBuf byteBuf, LocalBuff localBuff, ThreadLocal<LocalBuff> localBuffThreadLocal) {

        log.info("--------------------ResponceExcute_B1---------------");
         repeatRead(ctx,byteBuf,localBuff,localBuffThreadLocal,false);

    }
}
