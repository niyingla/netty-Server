package com.faceword.nio.business.component;


import com.faceword.nio.business.entity.LocalBuff;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/12/7 11:25
 * @Version 1.0
 * 6.3人脸库版本一致性对比--人脸库人脸详情对比（设备发起人脸对比请求）
 */
@Slf4j
@NoArgsConstructor
@Component(value = "213")
public class FaceConsistencyCheckD5 extends AbstratExcutor{


    /**
     * #####################################################需要重复读取数据文件
     * 人脸信息及识别结果上报处理逻辑
     * @param ctx
     * @param byteBuf
     * @param localBuff
     * @param localBuffThreadLocal
     */
    @Override
    public void excute(ChannelHandlerContext ctx, ByteBuf byteBuf, LocalBuff localBuff, ThreadLocal<LocalBuff> localBuffThreadLocal) {
       // log.info("---------FaceInformationReportB3------------");

        super.repeatRead( ctx,byteBuf,localBuff,localBuffThreadLocal,true );

    }
}
