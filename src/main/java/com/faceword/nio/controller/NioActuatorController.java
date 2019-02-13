package com.faceword.nio.controller;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.business.NettyChannelMap;
import com.faceword.nio.business.NettyServerProcess;
import com.faceword.nio.business.entity.SocketChannelSign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zyong
 * @Date: 2018/11/8 9:20
 * @Version 1.0
 *  暴露自定义的NIO 链接信息
 *  用于调试用
 */
@Slf4j
@RestController
@RequestMapping(value = "/nio/actuator")
@ApiIgnore
public class NioActuatorController {

    /**
     * 通过License 去查某个对象
     * @return
     */
    @GetMapping(value = "/license")
    public Object getMessageByLicense(String license){
        log.info("---------getMessageByLicense-----------");

        Map<String,SocketChannelSign> socketChannelLicense = NettyChannelMap.getChannelByLicenseCheck(license);
        if(socketChannelLicense!=null){
            return JSON.toJSONString(NettyChannelMap.getChannelByLicenseCheck(license));
        }else{
            return "没有license对应的链接";
        }
    }

    /**
     * 查询出所有的连接信息
     * @return
     */
    @GetMapping(value = "/all")
    public Object getAllMessageByLicense(){
        log.info("---------getAllMessageByLicense-----------");
        return JSON.toJSONString(NettyChannelMap.getAllChannel());
    }

    /**
     * http://localhost:8080/nio/actuator
     * 刷新人脸库的凭证 ： ASW@!_w2018sFaceworld
     * 刷新人脸库
     * @return
     */
    @GetMapping(value = "/refreshingFaceDatabase")
    public Object refreshingFaceDatabase(String license , String appKey){
        log.info("license={},appKey={}" ,license, appKey);
        Map<String,String> rsMap = new HashMap<>();
        if(StringUtils.isBlank(license)){
            rsMap.put("message","license invalid");
            return rsMap;

        }
        if(StringUtils.isBlank(appKey)){
            rsMap.put("message","appKey invalid");
            return rsMap;
        }

        if("ASW@!_w2018sFaceworld".equals(appKey)){

            NettyServerProcess.synchronousRequest(license);
            rsMap.put("message","request success");
            return rsMap;
        }
        rsMap.put("message" ,"request param invalid");
        return rsMap;
    }
}
