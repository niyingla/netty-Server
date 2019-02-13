package com.faceword.nio.service.algorithm;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.business.entity.EquipmentReportInformation;

import com.faceword.nio.service.EquipmentSynchronizationService;
import com.faceword.nio.service.GrabMachineService;
import com.faceword.nio.service.RedisCacheService;
import com.faceword.nio.service.algorithm.entity.AgeGender;
import com.faceword.nio.service.entity.DeleteFaceMessageLicense;
import com.faceword.nio.service.entity.FaceLibraryMessageLicense;
import com.faceworld.camera.base.entity.CameraCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zyong
 * @Date: 2018/11/13 11:12
 * @Version 1.0
 *  根据不同的算法有不同的实现， 所以这里对算法做抽象
 */
@Slf4j
public abstract  class AbstractAlgorithmService {

    /**
     * redis 缓存服务
     */
    @Autowired
    RedisCacheService redisCacheService ;

    /**
     * 抓拍机持久化服务
     */
    @Autowired
    GrabMachineService grabMachineService;

    @Autowired
    EquipmentSynchronizationService synchronizationService;

    /**
     * 抽象人脸识别接口 - > 人脸识别
     * @param recode 为抓拍机的基本信息
     */
    public abstract AgeGender faceDetect(EquipmentReportInformation recode);


    /**
     *  人脸库的添加
     */
    public abstract Object faceAdd(FaceLibraryMessageLicense recode);

    /**
     * 人脸库的删除
     */
    public abstract Object faceDelete( DeleteFaceMessageLicense recode);

    /**
     *  人脸搜索
     * @param groupId 对应人脸库的id
     * @param bytes 对应图片的二进制数组
     * @return
     */
    public abstract Object faceSearch(String groupId , byte [] bytes );

    /**
     * 封装算法的调用过程
     */
    public void process( EquipmentReportInformation recode ){

        /**
         * 接收抓拍机设备传递过来的base64编码的图片  ->recode.getPicData()
         */
        byte [] bytes =  Base64Utils.decodeFromString( recode.getPicData() );
        /**
         *   1. 识别人脸的信息
         */
        AgeGender ageGender =  faceDetect(recode);
        log.info("AgeGender->{}" , JSON.toJSONString(ageGender));
        if(ageGender == null){
            log.info("无法识别出人脸的基础信息 【age - male】 停止对这条数据的处理");
            return ;
        }
        recode.setAge(ageGender.getAge());
        recode.setGender(ageGender.getGender());
        /**
         *   2.保存当前信息到抓拍记录表
         */
        //  当前方法调用service嵌套了 grabMachineService.saveCaptureInfo( recode );
        synchronizationService.processDeviceReportFaceInfo(recode , null);


        //3.根据license去查询对应的人脸库信息
        List<HashMap> faceLibInfoMap =  redisCacheService.getFaceInfoByLicense(recode.getLicense());
        log.info("redis___map ->{}" , JSON.toJSONString(faceLibInfoMap));
        if(faceLibInfoMap == null){
            //直接保存到陌生人
            grabMachineService.saveAlarmStrange(recode,ageGender);
            return ;
        }
        // 接下来调用算法进行识别
        Map<String,Object> map = null;
        String faceDbType = null; //对应人脸库的类型 1:黑名单 0:白名单
        String faceDbId = null; //对应数据库的人脸库id ，对应算法的group id
        Object rsobj = null; //
        for (int i=0; i<faceLibInfoMap.size() ; i++){
           map = faceLibInfoMap.get(i);
           faceDbType = map.get("faceDbType").toString();
           faceDbId = map.get("faceDbId").toString();
           Object faceSearch = faceSearch(faceDbId,bytes);
           if("0".equals(faceDbType)){  //0:白名单
               rsobj =  grabMachineService.saveAlarmWhite(recode,faceSearch,ageGender);
           }else if("1".equals(faceDbType)){ // 黑名单
               rsobj = grabMachineService.saveAlarmBlack(recode,faceSearch,ageGender);
           }
           if( rsobj !=null ){
               //所有已经找到对应的算法，完成识别
               return ;
           }
        }
       //如果黑名单与白名单都没有在对应的人脸库识别到 ，则保存到陌生人中
        grabMachineService.saveAlarmStrange(recode,ageGender);
    }
}
