package com.faceword.nio.business.component;


import com.faceword.nio.business.NettyChannelMap;
import com.faceword.nio.business.NettyServerProcess;
import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.business.entity.License;
import com.faceword.nio.business.entity.LocalBuff;
import com.faceword.nio.business.service.NettyService;
import com.faceword.nio.business.utils.ResponceMessageUtils;
import com.faceword.nio.business.utils.ServerWriteUtils;
import com.faceword.nio.enums.LicenseSignEnum;
import com.faceword.nio.service.CameraService;
import com.faceword.nio.service.ServiceInformationTransmissionService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/11/6 19:18
 * @Version 1.0
 * 3.3.向服务器上报设备信息
 */
@Slf4j
@Component(value = "163")
@NoArgsConstructor
public class EquipmentInfoExcutorA3 extends  AbstratExcutor{

    @Autowired
    ServiceInformationTransmissionService transmissionService;

    @Autowired
    CameraService cameraService;

    @Autowired
    NettyService nettyService;

    /**
     *设备向服务器上报信息组件 业务逻辑
     * @param ctx
     * @param byteBuf
     */
    @Override
    public void excute(ChannelHandlerContext ctx,
                       ByteBuf byteBuf,
                       LocalBuff localBuff,
                       ThreadLocal<LocalBuff> localBuffThreadLocal) {
        log.info("---------EquipmentInfoExcutorA3------------");
        ServerReceiveMessage serverReceiveMessage = localBuff.getServerReceiveMessage();

        String clientId = getClientId(ctx);
        //截取约定的license 用作业务查询
        //此处需要解密
        License license = NettyServerProcess.reportInformation( byteBuf ,localBuff.getLen());
        NettyChannelMap.add( clientId , (SocketChannel)ctx.channel() ,license.getLicense() , LicenseSignEnum.NORMAL_LINK.getCode() , license.getDevType() );

        ServerWriteUtils.writeHeadAndBody( serverReceiveMessage , ResponceMessageUtils.getSuccessStateBody() ,ctx);
        //将相机的状态修改为在线
        cameraService.updateCameraOnLine( NettyChannelMap.getLicense( clientId )  );

        nettyService.process(clientId);
    }




}
