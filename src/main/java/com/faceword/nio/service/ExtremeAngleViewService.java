package com.faceword.nio.service;

import com.faceword.nio.ai.arcsoft.ArcsoftOpenApi;
import com.faceword.nio.ai.arcsoft.entity.ArcsoftFaceToken;
import com.faceword.nio.ai.arcsoft.entity.FaceDetect;
import com.faceword.nio.business.entity.EquipmentReportInformation;
import com.faceword.nio.service.algorithm.AbstractAlgorithmService;
import com.faceword.nio.service.algorithm.entity.AgeGender;
import com.faceword.nio.service.entity.DeleteFaceMessage;
import com.faceword.nio.service.entity.DeleteFaceMessageLicense;
import com.faceword.nio.service.entity.FaceLibraryMessageLicense;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/11/13 14:10
 * @Version 1.0
 * 对于极视角算法的具体实现
 */
@Slf4j
@Service
@Transactional(readOnly = true )
public class ExtremeAngleViewService extends AbstractAlgorithmService {

    /**
     * 对极视角算法调用的封装
     */
    @Autowired
    ArcsoftOpenApi arcsoftOpenApi;




    /**
     * 抽象人脸识别接口 - > 人脸识别
     * @param recode 为抓拍机的基本信息
     */
    @Override
    public AgeGender faceDetect(EquipmentReportInformation recode) {
        byte [] bytes =  Base64Utils.decodeFromString( recode.getPicData() );
        FaceDetect faceDetect = arcsoftOpenApi.faceDetect(bytes);
        //暂时先不考虑场景图
         if(faceDetect.getResult() != null && faceDetect.getResult().size()>0){
             FaceDetect.Result result=  faceDetect.getResult().get(0);
             AgeGender ageGender = new AgeGender();
             ageGender.setAge(result.getAge());
             ageGender.setGender(result.getGender());
             return ageGender;
         }
         return  null;
    }


    /**
     * 搜索类型
     * @param groupId 对应人脸库的id
     * @param bytes 对应图片的二进制数组
     * @return
     */
    @Override
    public Object faceSearch(String groupId, byte[] bytes) {
        return arcsoftOpenApi.faceSearch(bytes,groupId);
    }


    /**
     * 调用极视角算法去识别
     * @param recode
     * @return
     */
    @Override
    public Object faceAdd(FaceLibraryMessageLicense recode) {

        return   arcsoftOpenApi.faceAdd(recode.getFaceData(),recode.getFaceID().toString(),recode.getGroupID().toString());
    }

    /**
     * 人脸删除
     * @param recode
     * @return
     */
    @Override
    public Object faceDelete( DeleteFaceMessageLicense recode) {
        List<DeleteFaceMessage.FaceID> faceDetailList = recode.getDelFaces();
        List<Object> rsList = null;
        if( faceDetailList != null ){
            rsList = new ArrayList<>();
            for (DeleteFaceMessage.FaceID faceDetail :faceDetailList ) {
                ArcsoftFaceToken faceToken =  arcsoftOpenApi.faceDelete( faceDetail.getFaceID().toString() ,recode.getGroupID().toString());
                rsList.add(faceToken);
            }
        }
        return rsList;
    }
}
