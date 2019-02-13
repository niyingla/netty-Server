package com.faceword.nio.business.component;




import com.faceword.nio.business.entity.LocalBuff;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
/**
 * @Author: zyong
 * @Date: 2018/11/7 15:25
 * @Version 1.0
 * 3.8.删除人脸库
 * 服务端发送删除人脸库TCP后，客户端返回
 */
@Slf4j
@Component(value = "174")
@NoArgsConstructor
public class ResponceExcute_AD extends AbstratExcutor{



    /**
     * 服务端发送删除人脸库TCP后，客户端返回后业务处理
     * @param ctx
     * @param byteBuf
     * @param localBuff
     * @param localBuffThreadLocal
     */
    @Override
    public void excute(ChannelHandlerContext ctx, ByteBuf byteBuf, LocalBuff localBuff, ThreadLocal<LocalBuff> localBuffThreadLocal) {
        log.info("---------------------ResponceExcute_AD-------------------");
          repeatRead(ctx,byteBuf,localBuff,localBuffThreadLocal,false);

    }



}
