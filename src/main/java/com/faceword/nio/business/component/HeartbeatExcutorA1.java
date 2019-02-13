package com.faceword.nio.business.component;


import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.business.entity.LocalBuff;
import com.faceword.nio.business.utils.ServerWriteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/11/6 19:14
 * @Version 1.0
 * 3.2.设备与服务器的心跳接口
 */
@Slf4j
@Component(value = "161")
@NoArgsConstructor
public class HeartbeatExcutorA1 extends AbstratExcutor {


    /**
     * 心跳包业务的处理
     * @param ctx
     * @param byteBuf
     */
    @Override
    public void excute(ChannelHandlerContext ctx,
                       ByteBuf byteBuf ,
                       LocalBuff localBuff,
                       ThreadLocal<LocalBuff> localBuffThreadLocal) {
        log.info("--------------HeartbeatExcutorA1 get heartbeat message ----------------" );
        ServerReceiveMessage serverReceiveMessage = localBuff.getServerReceiveMessage();

        //给客户端回一个数据包
        ServerWriteUtils.writeHead( serverReceiveMessage ,ctx );
    }
}
