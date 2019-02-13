package com.faceword.nio.handle;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.annotations.FaceConsistencyCheckAnnotation;
import com.faceword.nio.annotations.FaceDbUniformityConfirm;
import com.faceword.nio.business.NettyChannelMap;
import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.business.response.ResponceMessage;
import com.faceword.nio.enums.LogTypeEnum;
import com.faceword.nio.enums.ResonceCodeEnum;
import com.faceword.nio.enums.SyncStatusEnum;
import com.faceword.nio.service.MansFaceLogService;
import com.faceword.nio.service.ServiceInformationTransmissionService;
import com.faceword.nio.service.nio.FaceDbTriggerAopServer;
import com.faceworld.base.config.ApplicationContextProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/12/17 9:16
 * @Version 1.0
 */
@Slf4j
@Component(value = "handle178")
public class B1ProcessHandle  extends AbstractNioHandel {


   // private FaceDbTriggerAopServer faceDbTriggerAopServer =  ApplicationContextProvider.getBean(FaceDbTriggerAopServer.class);

    private MansFaceLogService mansFaceLogService =
            ApplicationContextProvider.getBean( MansFaceLogService.class );

    private ServiceInformationTransmissionService transmissionService  =
            ApplicationContextProvider.getBean( ServiceInformationTransmissionService.class );
    /**
     * 3.9.批量删除设备黑/白人脸库中全部的人脸接口（删除人脸库）
     * @param receiveMessage
     */
    @Override
    @FaceConsistencyCheckAnnotation( title="删除人脸库" ,isCheck = true ,type =  LogTypeEnum.FACE_DELETE_ALL)
    @FaceDbUniformityConfirm(title = "删除人脸库" , type = LogTypeEnum.FACE_DELETE_ALL ,responceType = ResponceMessage.class )
    public void excute(ServerReceiveMessage receiveMessage) {
        log.info("---------deleteAllFaceClientResponce  178 方法调用！！=========");
        //faceDbTriggerAopServer.deleteAllFaceClientResponceTrigger(receiveMessage);
        try {
            ResponceMessage responceMessage  =  JSON.parseObject(receiveMessage.getBody() , ResponceMessage.class);
            Long sing = responceMessage.getSign();
            if( responceMessage.getErrcode() == ResonceCodeEnum.SUCCESS.getCode()){
                log.info(" sign == {} deleteAllFaceClientResponce synchronized data success ! " ,sing );
                mansFaceLogService.update( sing , responceMessage , SyncStatusEnum.SYNCHRONIZTION_SUCCESS );
            }else{
                log.warn(" sign == {} deleteAllFaceClientResponce  synchronized data fail ! " ,sing );
                mansFaceLogService.update( sing , responceMessage , SyncStatusEnum.SYNCHRONIZTION_FAILURE );
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {

            if(NettyChannelMap.isLicenseOnline( receiveMessage.getLicense() )){
                //继续执行删除人脸库操作
                transmissionService.continueDeleteAllFace( receiveMessage.getLicense());
                log.info("continue to camera delete  face db!!");
            }

        }
    }
}
