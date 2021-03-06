package com.faceword.nio.handle;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.business.entity.FaceInformationReport;
import com.faceword.nio.enums.ListTypeEnum;
import com.faceword.nio.service.EquipmentSynchronizationService;
import com.faceword.nio.service.nio.NioProcessService;
import com.faceworld.base.config.ApplicationContextProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/12/17 9:06
 * @Version 1.0
 */
@Slf4j
@Component(value = "handle179")
public class B3ProcessHandle extends AbstractNioHandel{



    @Autowired
    private EquipmentSynchronizationService equipmentSynchronizationService;
    /**
     * 3.10.人脸信息及识别结果上报接口 B3
     *
     */
    @Override
    public void excute(ServerReceiveMessage receiveMessage) {
        log.info("---------reportFaceInformation  179 方法调用！！=========");
        this.reportProcess(receiveMessage);
    }

    private void reportProcess(ServerReceiveMessage receiveMessage){

        FaceInformationReport faceInformationReport = JSON.parseObject(receiveMessage.getBody() , FaceInformationReport.class);
        faceInformationReport.setLicense( receiveMessage.getLicense());
        Integer listType = faceInformationReport.getListType();
        if(ListTypeEnum.BLACK_LIST.getCode() == listType){
            //黑名单  null参数又AOP动态传入 --
            equipmentSynchronizationService.reportAlarmBlackListFaceInformation(faceInformationReport ,null);
        }else if(ListTypeEnum.WHITE_LIST.getCode() == listType){
            //白名单  null参数又AOP动态传入
            equipmentSynchronizationService.reportAlarmWhiteListFaceInformation(faceInformationReport,null);
        }else{
            //陌生人 null参数又AOP动态传入
            equipmentSynchronizationService.reportAlarmStrangeListFaceInformation(faceInformationReport,null );
        }
    }
}
