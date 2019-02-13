package com.faceword.nio.service.nio;

import com.faceword.nio.annotations.FaceConsistencyCheckAnnotation;
import com.faceword.nio.annotations.FaceDbUniformityConfirm;
import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.business.entity.DeleteFaceLibraryResponse;
import com.faceword.nio.business.entity.FaceCodeGroup;
import com.faceword.nio.business.response.ResponceMessage;
import com.faceword.nio.enums.LogTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: zyong
 * @Date: 2018/12/11 11:34
 * @Version 1.0
 * 该service主要是为了触发aop
 */
@Slf4j
@Service
public class FaceDbTriggerAopServer {


    @FaceConsistencyCheckAnnotation( title="删除人脸库" ,isCheck = true ,type =  LogTypeEnum.FACE_DELETE_ALL)
    @FaceDbUniformityConfirm(title = "删除人脸库" , type = LogTypeEnum.FACE_DELETE_ALL ,responceType = ResponceMessage.class )
    public void deleteAllFaceClientResponceTrigger(ServerReceiveMessage receiveMessage ){

        log.info("----------deleteAllFaceClientResponceTrigger-------------");
    }


    @FaceConsistencyCheckAnnotation( title="删除设备人脸库人脸" ,isCheck = true ,type =  LogTypeEnum.FACE_DELETE)
    @FaceDbUniformityConfirm(title = "删除人脸库人脸" , type = LogTypeEnum.FACE_DELETE ,responceType = DeleteFaceLibraryResponse.class )
    public void deleteFaceClientResponceTrigger(ServerReceiveMessage receiveMessage){

    }

    @FaceConsistencyCheckAnnotation( title="人脸库添加人脸" ,isCheck = true ,type =  LogTypeEnum.FACE_ADD)
    @FaceDbUniformityConfirm(title = "人脸库添加人脸" , type = LogTypeEnum.FACE_ADD ,responceType = FaceCodeGroup.class )
    public void addFaceClientResponceTrigger(ServerReceiveMessage receiveMessage){

    }
}
