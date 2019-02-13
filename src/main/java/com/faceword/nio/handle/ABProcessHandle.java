package com.faceword.nio.handle;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.annotations.FaceConsistencyCheckAnnotation;
import com.faceword.nio.annotations.FaceDbUniformityConfirm;
import com.faceword.nio.business.NettyChannelMap;
import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.business.entity.FaceCodeGroup;
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
 * @Date: 2018/12/17 9:10
 * @Version 1.0
 */
@Slf4j
@Component(value = "handle172")
public class ABProcessHandle extends AbstractNioHandel{

   // private FaceDbTriggerAopServer faceDbTriggerAopServer =  ApplicationContextProvider.getBean(FaceDbTriggerAopServer.class);;

    private MansFaceLogService mansFaceLogService =
            ApplicationContextProvider.getBean( MansFaceLogService.class );

    private ServiceInformationTransmissionService transmissionService  =
            ApplicationContextProvider.getBean( ServiceInformationTransmissionService.class );
    /**
     * 3.7.向设备人脸库中添加人脸信息接口
     * @param receiveMessage
     */
    @Override
    @FaceConsistencyCheckAnnotation( title="人脸库添加人脸" ,isCheck = true ,type =  LogTypeEnum.FACE_ADD)
    @FaceDbUniformityConfirm(title = "人脸库添加人脸" , type = LogTypeEnum.FACE_ADD ,responceType = FaceCodeGroup.class )
    public void excute(ServerReceiveMessage receiveMessage) {
        log.info("---------addFaceClientResponce  172 方法调用！！=========");
       // faceDbTriggerAopServer.addFaceClientResponceTrigger(receiveMessage);
        FaceCodeGroup faceCodeGroup  =  JSON.parseObject(receiveMessage.getBody() , FaceCodeGroup.class);
        Long sing = faceCodeGroup.getSign();
        try {
            if( faceCodeGroup.getErrcode() == ResonceCodeEnum.SUCCESS.getCode()){
                log.info(" sign == {} synchronized data success ! " ,sing );
                mansFaceLogService.update( sing , faceCodeGroup , SyncStatusEnum.SYNCHRONIZTION_SUCCESS );
            }else{
                log.warn(" sign == {} synchronized data fail ! " ,sing );
                mansFaceLogService.update( sing , faceCodeGroup , SyncStatusEnum.SYNCHRONIZTION_FAILURE );
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            if(NettyChannelMap.isLicenseOnline(receiveMessage.getLicense() )){
                //继续向人脸库同步人脸
                transmissionService.distributionCtrl(receiveMessage.getLicense());
                log.info("continue to camera distribution control face!!");
            }
        }
    }
}
