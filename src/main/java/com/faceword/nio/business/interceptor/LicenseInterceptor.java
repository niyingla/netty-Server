package com.faceword.nio.business.interceptor;


import com.faceword.nio.business.entity.LocalBuff;
import com.faceword.nio.business.utils.NettyServerUtils;
import com.faceword.nio.config.Constans;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/11/6 15:46
 * @Version 1.0
 *  License 拦截去
 */
@Slf4j
public class LicenseInterceptor implements NioInterceptor{

    private static List<Short> list = new ArrayList<>();

    static {
        //向服务器上报设备信息 不需要  License
        list.add(Constans.EQUIPMENT_INFO);

        list.add(Constans.E1);
    }


    @Override
    public boolean before( ChannelHandlerContext ctx, ByteBuf byteBuf, LocalBuff localBuff) {
       String clientId = ctx.channel().id().asLongText();
       short mode = localBuff.getServerReceiveMessage().getMode();
        //过滤命令号
        if(list.contains(mode)){
            return true;
        }
        if(!NettyServerUtils.checkLicense(clientId)){
            log.warn( "current connect clientId = {} no license , ",clientId );
            //如果当前发送的链接没有License
            return  false;
        }
        return true;
    }


}
