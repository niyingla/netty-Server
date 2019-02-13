package com.faceword.nio.controller;


import com.faceword.nio.annotations.FaceDbUniformityConfirm;
import com.faceword.nio.annotations.LogAnnotation;
import com.faceword.nio.common.CallBackMsg;
import com.faceword.nio.common.DistributionControlVo;
import com.faceword.nio.enums.CameraTypeEnum;
import com.faceword.nio.enums.LogTypeEnum;
import com.faceword.nio.service.FaceDbService;
import com.faceword.nio.service.ServiceInformationTransmissionService;
import com.faceword.nio.service.algorithm.AbstractAlgorithmService;
import com.faceword.nio.service.entity.DeleteAllFaceMessageLicense;
import com.faceword.nio.service.entity.DeleteFaceMessageLicense;
import com.faceword.nio.service.entity.FaceLibraryMessage;
import com.faceword.nio.service.entity.FaceLibraryMessageLicense;
import com.faceword.nio.service.nio.NioProcessService;
import com.faceword.nio.utils.CallBackMsgUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/11/7 11:14
 * @Version 1.0
 * 提供webServer 的http接口
 *  用来与设备端通讯
 */
@Api(description = "设备管理API列表")
@Slf4j
@RestController
public class NioServerController {

    @Autowired
    private ServiceInformationTransmissionService transmissionService;

    @Autowired
    AbstractAlgorithmService algorithmService;

    @Autowired
    FaceDbService faceDbService;

    @Value("${nio.faceDbDomain}")
    private String faceDbDomain;

    @Autowired
    private NioProcessService nioProcessService;

    /**
     * 服务器设置设备时间 3.4.设置设备时间的接口 0xa5
     * @return
     */
    @ApiOperation(value="3.4设置设备时间的接口", notes="3.4设置设备时间的接口", response=CallBackMsg.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name="license", value="license", required=true, dataType="String", paramType="query")
    })
    @FaceDbUniformityConfirm( title = "webServer请求断开设备连接" , type = LogTypeEnum.SET_EQUIPMENT_TIME )
    @LogAnnotation(title = "setEquipmentTime" ,type = LogTypeEnum.SET_EQUIPMENT_TIME )
    @PostMapping(value = "/nio/v1/setEquipmentTime")
    public CallBackMsg setEquipmentTime(String license ){
         log.info("----------setEquipmentTime 3.4/nio/v1/setEquipmentTime-----------");

         transmissionService.setEquipmentTime(license);

         return CallBackMsgUtils.noArgsSucess();
    }

    /**
     * 0xa9
     * 3.6. 服务器断开连接某个设备 A9
     * @return
     */
    @ApiOperation(value="3.6. 服务器断开连接某个设备", notes="3.6. 服务器断开连接某个设备", response=CallBackMsg.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name="license", value="license", required=true, dataType="String", paramType="query")
    })
    @LogAnnotation(title = "serverDisconnectEquipment" ,type = LogTypeEnum.SERVER_DISCONNERCT_EQUIPMENT )
    @FaceDbUniformityConfirm( title = "webServer请求断开设备连接" , type = LogTypeEnum.SERVER_DISCONNERCT_EQUIPMENT )
    @PostMapping(value = "/nio/v1/serverDisconnectEquipment")
    public  CallBackMsg serverDisconnectEquipment(String  license ) {
        log.info("----------serverDisconnectEquipment 3.6/nio/v1/serverDisconnectEquipment-----------");

        //检查当前license 有没在线

        transmissionService.disconnectEquipment(license);

        return CallBackMsgUtils.noArgsSucess();
    }

    /**
     * 3.7.向设备人脸库中添加人脸信息接口 ab
     * @return
     */
    @ApiOperation(value="3.7.向设备人脸库中添加人脸信息接口", notes="3.7.向设备人脸库中添加人脸信息接口", response=CallBackMsg.class)
    @ApiImplicitParam(value = "body" )
    @FaceDbUniformityConfirm( title = "webServer请求添加人脸到相机" , type = LogTypeEnum.FACE_ADD )
    @PostMapping(value = "/nio/v1/addFaceMessageToLibrary")
    public CallBackMsg addFaceMessageToLibrary(@RequestBody FaceLibraryMessageLicense faceLibraryMessage ){

        log.info("----------3.7/nio/v1/addFaceMessageToLibrary-----------");

        //--------------业务实现
        int cameraType =  faceLibraryMessage.getCameraType();
        log.info("---------addFaceMessageToLibrary camera type 【{}】,camera name --------" ,cameraType , CameraTypeEnum.typeOf(cameraType).getValue());

        if( cameraType == CameraTypeEnum.GRAB_CAMERA.getCode()){
            //抓拍机
           return CallBackMsgUtils.setRsSucess(algorithmService.faceAdd(faceLibraryMessage))  ;
        }else if( cameraType ==  CameraTypeEnum.INTEGRATE_CAMERA.getCode()){
            //一体机
            transmissionService.libraryAddFace( faceLibraryMessage );
        }else if( cameraType == CameraTypeEnum.ORDINARY_CAMERA.getCode() ){
            //普通摄像机
        }else{
            //暂不支持其他类型相机
            return CallBackMsgUtils.notSupportCameraType();
        }
        return CallBackMsgUtils.noArgsSucess();
    }

    /**
     * 0xad
     * 3.8.删除人脸库 人脸
     * @return
     */
    @ApiOperation(value="3.8.删除人脸库", notes="3.8.删除人脸库", response=CallBackMsg.class)
    @ApiImplicitParam(value = "body" )
    @FaceDbUniformityConfirm( title = "webServer请求删除人脸库人脸" , type = LogTypeEnum.FACE_DELETE )
    @PostMapping(value = "/nio/v1/deleteFaceLibrary")
    public CallBackMsg deleteFaceLibrary(@RequestBody DeleteFaceMessageLicense deleteFaceMessageLicense){


        //--------------业务实现
        int cameraType =  deleteFaceMessageLicense.getCameraType();
        log.info("---------deleteFaceLibrary camera type 【{}】,camera name --------" ,cameraType , CameraTypeEnum.typeOf(cameraType).getValue());

        if( cameraType == CameraTypeEnum.GRAB_CAMERA.getCode()){
            //抓拍机
           return CallBackMsgUtils.setRsSucess(algorithmService.faceDelete(deleteFaceMessageLicense));

        }else if( cameraType ==  CameraTypeEnum.INTEGRATE_CAMERA.getCode()){
            //一体机 --> 通过tcp删除人脸库人脸信息
            transmissionService.deleteLibraryFace(deleteFaceMessageLicense);
        }else if( cameraType == CameraTypeEnum.ORDINARY_CAMERA.getCode() ){
            //普通摄像机
        }else{
            //暂不支持其他类型相机
            return CallBackMsgUtils.notSupportCameraType();
        }
        return CallBackMsgUtils.noArgsSucess();
    }

    /**
     * 3.9.批量删除设备黑/白人脸库中全部的人脸接口 B1
     * @param deleteAllFaceMessageLicense
     * @return
     */
    @ApiOperation( value=" 3.9.批量删除设备黑/白人脸库中全部的人脸接口 【当前只支持一体机批量删除】", response=CallBackMsg.class )
    @ApiImplicitParam( value = "body" )
    @FaceDbUniformityConfirm( title = "webServer请求布控人脸库到相机" , type = LogTypeEnum.FACE_DELETE_ALL )
    @PostMapping( value = "/nio/v1/deleteAllFaceLibrary" )
    public CallBackMsg deleteAllFaceLibrary( @RequestBody DeleteAllFaceMessageLicense deleteAllFaceMessageLicense ){

        //--------------业务实现
        int cameraType =  deleteAllFaceMessageLicense.getCameraType();
        log.info("---------deleteAllFaceLibrary camera type 【{}】,camera name --------" ,cameraType , CameraTypeEnum.typeOf(cameraType).getValue());

        if( cameraType == CameraTypeEnum.GRAB_CAMERA.getCode()){
            log.warn( " 抓拍机不支持批量删除！" );
            // 抓拍机
            log.info("----------调用抓拍机删除所有数据-------------");
        }else if( cameraType ==  CameraTypeEnum.INTEGRATE_CAMERA.getCode()){
            // 一体机 --> 通过tcp删除人脸库人脸信息
            transmissionService.deleteAllFace(deleteAllFaceMessageLicense);
        }else if( cameraType == CameraTypeEnum.ORDINARY_CAMERA.getCode() ){
            log.warn( " 普通摄像机不支持批量删除！" );
            // 普通摄像机
        }else{
            //暂不支持其他类型相机
            return CallBackMsgUtils.notSupportCameraType();
        }
        return CallBackMsgUtils.noArgsSucess();
    }

    @ApiOperation( value=" 3.9.布控", response=CallBackMsg.class )
    @ApiImplicitParam( value = "body" )
    @FaceDbUniformityConfirm( title = "webServer添加人脸到相机" , type = LogTypeEnum.FACE_DISTRIBUTION_CONTROL )
    @PostMapping( value = "/nio/v1/distributionControl" )
    public CallBackMsg distributionControl(@RequestBody DistributionControlVo distributionControlVo){

        String license = distributionControlVo.getLicense();
        int cameraType =  distributionControlVo.getCameraType();
        log.info("---------distributionControl camera type 【{}】,camera name --------" ,cameraType , CameraTypeEnum.typeOf(cameraType).getValue());

        if(distributionControlVo.getDbList() == null || distributionControlVo.getDbList().size()==0){

            return CallBackMsgUtils.invalidParam();
        }

        List<FaceLibraryMessage> faceLibraryMessageList = faceDbService.findFaceLibraryList(distributionControlVo.getDbList());

        if( cameraType == CameraTypeEnum.GRAB_CAMERA.getCode() ){
            log.warn( " 抓拍机不支持批量删除！" );
            // 抓拍机
            log.info("----------调用抓拍机删除所有数据-------------");
        }else if( cameraType ==  CameraTypeEnum.INTEGRATE_CAMERA.getCode()){
            // 一体机
            nioProcessService.addFaceListToRedisQuery(faceLibraryMessageList,faceDbDomain,license);
            log.info(" add license:{} mapping face db to redis " , license);
            // 发送消息
            transmissionService.distributionCtrl(license);
            log.info("start to camera distribution control face!!");
        }else if( cameraType == CameraTypeEnum.ORDINARY_CAMERA.getCode() ){
            log.warn( " 普通摄像机不支持批量删除！" );
            // 普通摄像机
        }else{
            //暂不支持其他类型相机
            return CallBackMsgUtils.notSupportCameraType();
        }

        return CallBackMsgUtils.noArgsSucess();
    }
}
