package com.faceword.nio.business.component;

import com.faceword.nio.business.NettyChannelMap;
import com.faceword.nio.business.NettyServerProcess;
import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.business.entity.License;
import com.faceword.nio.business.entity.LocalBuff;
import com.faceword.nio.business.utils.ResponceMessageUtils;
import com.faceword.nio.business.utils.ServerWriteUtils;
import com.faceword.nio.enums.LicenseSignEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/11/29 10:17
 * @Version 1.0
 * 5.1向服务器上报设备信息
 */
@Slf4j
@Component(value = "225")
@NoArgsConstructor
public class BrokenNetworkE1 extends AbstratExcutor{

    /**
     * #####################################################需要重复读取数据文件
     * 断网续传设备保存的信息
     * @param ctx
     * @param byteBuf
     * @param localBuff
     * @param localBuffThreadLocal
     */
    @Override
    public void excute(ChannelHandlerContext ctx, ByteBuf byteBuf, LocalBuff localBuff, ThreadLocal<LocalBuff> localBuffThreadLocal) {
        log.info("---------BrokenNetworkE1------------");
        ServerReceiveMessage serverReceiveMessage = localBuff.getServerReceiveMessage();
        String clientId = getClientId(ctx);
        //截取约定的license 用作业务查询
        License license = NettyServerProcess.reportInformation( byteBuf ,localBuff.getLen());
        NettyChannelMap.add( clientId , (SocketChannel)ctx.channel() ,license.getLicense() , LicenseSignEnum.BROKEN_NET_LINK.getCode(),license.getDevType());
        ServerWriteUtils.writeHeadAndBody( serverReceiveMessage , ResponceMessageUtils.getSuccessStateBody() ,ctx);

    }
}
