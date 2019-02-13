package com.faceword.nio.handle;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.annotations.FaceConsistencyCheckAnnotation;
import com.faceword.nio.annotations.FaceDbUniformityConfirm;
import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.business.entity.DeleteFaceLibraryResponse;
import com.faceword.nio.business.response.ResponceMessage;
import com.faceword.nio.enums.LogTypeEnum;
import com.faceword.nio.enums.ResonceCodeEnum;
import com.faceword.nio.enums.SyncStatusEnum;
import com.faceword.nio.service.MansFaceLogService;
import com.faceword.nio.service.nio.FaceDbTriggerAopServer;
import com.faceworld.base.config.ApplicationContextProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/12/17 9:14
 * @Version 1.0
 */
@Slf4j
@Component(value = "handle174")
public class ADProcessHandle extends AbstractNioHandel{


   // private FaceDbTriggerAopServer faceDbTriggerAopServer =  ApplicationContextProvider.getBean(FaceDbTriggerAopServer.class);

    private MansFaceLogService mansFaceLogService =
            ApplicationContextProvider.getBean( MansFaceLogService.class );

    /**
     * 3.8.删除人脸库 人脸
     * @param receiveMessage
     */
    @Override
    @FaceConsistencyCheckAnnotation( title="删除设备人脸库人脸" ,isCheck = true ,type =  LogTypeEnum.FACE_DELETE)
    @FaceDbUniformityConfirm(title = "删除人脸库人脸" , type = LogTypeEnum.FACE_DELETE ,responceType = DeleteFaceLibraryResponse.class )
    public void excute(ServerReceiveMessage receiveMessage) {
        log.info("---------deleteFaceClientResponce  174 方法调用！！=========");
        //faceDbTriggerAopServer.deleteFaceClientResponceTrigger(receiveMessage);
        DeleteFaceLibraryResponse deleteFaceLibraryResponse  =  JSON.parseObject(receiveMessage.getBody() , DeleteFaceLibraryResponse.class);
        Long sing = deleteFaceLibraryResponse.getSign();
        if( deleteFaceLibraryResponse.getErrcode() == ResonceCodeEnum.SUCCESS.getCode()){
            log.info(" sign == {} deleteFaceClientResponce synchronized data success !! " ,sing );
            mansFaceLogService.update( sing , deleteFaceLibraryResponse , SyncStatusEnum.SYNCHRONIZTION_SUCCESS );
        }else{
            log.warn(" sign == {} deleteFaceClientResponce  synchronized data fail !! " ,sing );
            mansFaceLogService.update( sing , deleteFaceLibraryResponse , SyncStatusEnum.SYNCHRONIZTION_FAILURE );
        }
    }
}
