package com.faceword.nio.business.component;


import com.faceword.nio.business.entity.LocalBuff;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
/**
 * @Author: zyong
 * @Date: 2018/11/6 19:24
 * @Version 1.0
 * 3.5. 设备上报抓拍人脸信息接口
 */
@Slf4j
@Component(value = "167")
@NoArgsConstructor
public class ReportSnappedFaceExcutorA7 extends AbstratExcutor{

    /**
     * #######################需要重复读取数据文件
     * 设备上报抓拍人脸信息
     * @param ctx
     * @param byteBuf
     * @param localBuff
     * @param localBuffThreadLocal
     */
    @Override
    public void excute(ChannelHandlerContext ctx,
                       ByteBuf byteBuf,
                       LocalBuff localBuff,
                       ThreadLocal<LocalBuff> localBuffThreadLocal) {
        log.info("--------------ReportSnappedFaceExcutorA7----------------" );
        super.repeatRead(ctx,byteBuf,localBuff,localBuffThreadLocal, true);
    }
}
