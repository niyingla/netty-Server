package com.faceword.nio.business.utils;

import com.faceword.nio.business.NettyChannelMap;
import com.faceword.nio.business.entity.LocalBuff;
import com.faceword.nio.config.Constans;
import com.faceword.nio.utils.Base64Utils;
import com.faceword.nio.utils.RC4Utils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
/**
 * @Author: zyong
 * @Date: 2018/11/6 14:13
 * @Version 1.0
 */
@Slf4j
public class NettyServerUtils {


    /**
     *  通过ChannelHandlerContext 获取客户端的唯一id号
     * @param ctx
     * @return
     */
    public static String getClientId(ChannelHandlerContext ctx){
        return ctx.channel().id().asLongText();
    }

    /**
     *  获取license 的业务id
     * @param license
     * @return
     */
    public static String getServiceLicenseId(String license){
        if(license == null ){
            return "";
        }
        return license.split(",")[0];
    }


    /**
     *  解码License
     * @param license
     * @return
     */
    public static String decodeLicense(String license){
        if(license == null ){
            return "";
        }
        return RC4Utils.decry_RC4(Base64Utils.base64Decode(license) , Constans.RC4_KEY);
    }

    /**
     * netty 的客户端id
     * @param clientId
     * @return
     */
    public static boolean checkLicense(String clientId){
        if(clientId == null){
            log.info("clientId is null !!!!!!!!");
            return false;
        }
        //如果不存在License 就
        if(StringUtils.isBlank( NettyChannelMap.getLicense(clientId))){
            log.info("当前客户端没有 License");
            return false;
        }
        return true;

    }


    /**
     * 获取命令号
     * @return
     */
    public static short getMode( LocalBuff localBuff){
        if( localBuff == null ){
            return -1;
        }
        if(localBuff.getServerReceiveMessage() == null){
            return -1;
        }
        return localBuff.getServerReceiveMessage().getMode();
    }
}
