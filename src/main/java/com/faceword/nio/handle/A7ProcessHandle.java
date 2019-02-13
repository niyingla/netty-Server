package com.faceword.nio.handle;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.business.entity.EquipmentReportInformation;
import com.faceword.nio.service.ExtremeAngleViewService;
import com.faceword.nio.service.algorithm.AbstractAlgorithmService;
import com.faceworld.base.config.ApplicationContextProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/12/17 9:04
 * @Version 1.0
 */
@Slf4j
@Component(value = "handle167")
public class A7ProcessHandle extends AbstractNioHandel{

    private AbstractAlgorithmService abstractAlgorithmService =
            ApplicationContextProvider.getBean( ExtremeAngleViewService.class );

    /**
     * 3.5. 设备上报抓拍人脸信息接口 A7
     * 根据注解上面的命令号去匹配方法
     */
    @Override
    public void excute(ServerReceiveMessage receiveMessage) {
        log.info("------------processDeviceReportFaceInfo 167！！===========");
        String license = receiveMessage.getLicense();

        //TODO 保存抓拍基本信息
        EquipmentReportInformation reportInformation =  JSON.parseObject(receiveMessage.getBody() , EquipmentReportInformation.class);
        reportInformation.setLicense(license);
        //基于抽象类的调用
        abstractAlgorithmService.process(reportInformation);
    }



}
