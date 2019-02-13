package com.faceword.nio.service;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.annotations.LogAnnotation;
import com.faceword.nio.business.NettyChannelMap;
import com.faceword.nio.business.utils.ServerWriteUtils;
import com.faceword.nio.config.Constans;
import com.faceword.nio.enums.LicenseSignEnum;
import com.faceword.nio.enums.LogTypeEnum;
import com.faceword.nio.redis.RedisConstans;
import com.faceword.nio.service.entity.*;
import com.faceword.nio.utils.Base64Utils;
import com.faceword.nio.utils.DateUtils;
import com.faceword.nio.utils.IOUtil;
import com.faceword.nio.utils.SnowflakeIdWorekrUtils;
import com.faceworld.base.mybatis.model.MansFaceLogWithBLOBs;
import com.faceworld.base.redis.RedisClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: zyong
 * @Date: 2018/11/6 16:13
 * @Version 1.0
 * 服务向设备发送信息的service
 */
@Slf4j
@Service
public class ServiceInformationTransmissionService {


    @Autowired
    private MansFaceLogService mansFaceLogService;



    /**
     * 3.4.设置设备时间的接口 A5
     * @return
     */
    public void setEquipmentTime(String license){
        log.info("------------setEquipmentTime------------------");
        TimeZone timeZone = new TimeZone();
        timeZone.setTime( DateUtils.getDateTime() );
        ServerWriteUtils.writeHeadAndBody( Constans.A5, JSON.toJSONString(timeZone) ,
                NettyChannelMap.getSocketChannel(license , LicenseSignEnum.NORMAL_LINK.getCode()) );

    }

    /**
     * 3.6. 服务器断开连接某个设备
     */
    public void disconnectEquipment(String  license){
        log.warn("--------disconnectEquipment-----------");
        ServerWriteUtils.writeHead( Constans.A9 , NettyChannelMap.getSocketChannel( license ,LicenseSignEnum.NORMAL_LINK.getCode()) );
    }

    /**
     * 3.7 向设备人脸库中添加人脸信息
     */
    @LogAnnotation( title = "addFaceMessageToLibrary" ,type = LogTypeEnum.FACE_ADD  ,isSign = true)
    public void libraryAddFace(FaceLibraryMessageLicense faceLibraryMessageLicense){
        log.info("------------libraryAddFace------------------");
        //NettyChannelMap.print();
        FaceLibraryMessage faceLibraryMessage = new FaceLibraryMessage();
        //通过http 加载图片
        byte [] bytes = IOUtil.fileUrlToByte(faceLibraryMessageLicense.getFaceData());
        if(bytes == null){
            log.error("file is not find path = :",faceLibraryMessageLicense.getFaceData());
            return ;
        }
        String base64File = Base64Utils.base64Encode(bytes);

        BeanUtils.copyProperties(faceLibraryMessageLicense ,faceLibraryMessage);

        //对所有的姓名进行加密
        if(!StringUtils.isBlank(faceLibraryMessageLicense.getFaceName())){
            faceLibraryMessage.setFaceName(Base64Utils.encodeData(faceLibraryMessageLicense.getFaceName()));
        }

        faceLibraryMessage.setFaceLen( base64File.length() );

        faceLibraryMessage.setFaceData(base64File);
        String jsonStr = JSON.toJSONString(faceLibraryMessage);
        ServerWriteUtils.writeHeadAndBody( Constans.AB  , jsonStr,
                NettyChannelMap.getSocketChannel(faceLibraryMessageLicense.getLicense(),LicenseSignEnum.NORMAL_LINK.getCode()));

    }

    /**
     * 3.8.删除人脸库
     */
    @LogAnnotation( title = "deleteFaceLibrary" ,type = LogTypeEnum.FACE_DELETE  ,isSign = true)
    public void deleteLibraryFace( DeleteFaceMessageLicense deleteFaceMessageLicense){

        log.info("------------deleteLibraryFace------------------");
        DeleteFaceMessage deleteFaceMessage  = new DeleteFaceMessage();
        BeanUtils.copyProperties(deleteFaceMessageLicense , deleteFaceMessage);
        String license = deleteFaceMessageLicense.getLicense();
        //向设备发送删除人脸库信息 TCP
        ServerWriteUtils.writeHeadAndBody(Constans.AD , JSON.toJSONString(deleteFaceMessage)
                ,NettyChannelMap.getSocketChannel(license,LicenseSignEnum.NORMAL_LINK.getCode()));
    }

    /**
     * 3.9.批量删除设备黑/白人脸库中全部的人脸接口
     */
    @LogAnnotation( title = "deleteAllFaceLibrary" ,type = LogTypeEnum.FACE_DELETE_ALL ,isSign = true)
    public void deleteAllFace( DeleteAllFaceMessageLicense deleteAllFaceMessageLicense){
        log.info("------------deleteAllFace------------------");
        DeleteAllFaceMessage deleteAllFaceMessage = new DeleteAllFaceMessage();
        BeanUtils.copyProperties(deleteAllFaceMessageLicense , deleteAllFaceMessage);
        String license = deleteAllFaceMessageLicense.getLicense();
        //向设备发送批量删除人脸库信息 TCP
        ServerWriteUtils.writeHeadAndBody(Constans.B1 ,
                JSON.toJSONString(deleteAllFaceMessage) ,NettyChannelMap.getSocketChannel(license,LicenseSignEnum.NORMAL_LINK.getCode()));
    }

    /**
     * 继续删除缓存中的人脸库队列
     * @param license
     */
    public void continueDeleteAllFace( String license){

        log.info("------------continueDeleteAllFace------------------");
        String redisKey = RedisConstans.FACE_DELETE_ALL_QUERY.concat(license);
        if(RedisClientUtil.querySize(redisKey) > 0){
            DeleteAllFaceMessageLicense deleteFaceDbMessage =   (DeleteAllFaceMessageLicense) RedisClientUtil.receiverQuery(redisKey);
            //统一生成唯一标识
            Long signId  = SnowflakeIdWorekrUtils.getOnlinePayId();
            deleteFaceDbMessage.setSign(signId);

            //添加删除人脸库记录
            logDeleteFaceDbProcess(license,NettyChannelMap.getCameraTypeByLicense(license),deleteFaceDbMessage,LogTypeEnum.FACE_ADD.getCode(),signId);
            this.deleteAllFace( deleteFaceDbMessage );
        }else{
            log.warn(" recovery redis  query[deleteAllFace] is null, delete redis query!");
        }
    }

    /**
     *  布控 -- > 从redis 队列中拉取 添加人脸信息去 同步到设备
     *
     *  后续引入抓拍机 ，需要从保存设备上报信息的相机类型
     */
    public void distributionCtrl( String license){
        log.info("------------distributionCtrl------------------");
        String redisKey = RedisConstans.FACE_DATABASE_ADD_QUERY.concat(license);
        if( RedisClientUtil.querySize(redisKey) > 0){
            FaceLibraryMessage message = RedisClientUtil.receiverQuery(redisKey);
            String pathUrl = message.getFaceData();
            byte [] bytes = IOUtil.fileUrlToByte(message.getFaceData());
            if( bytes == null){
                log.error(" path img is not exist!  path = {}" , message.getFaceData());
                //布控下一张人脸
                distributionCtrl(license);
                return ;
            }

            // 人脸日志 log.info(JSON.toJSONString( message , true  ));

            //统一生成唯一标识
            Long signId  = SnowflakeIdWorekrUtils.getOnlinePayId();
            String base64File = Base64Utils.base64Encode(bytes);

            //对所有的姓名进行加密
            if(!StringUtils.isBlank(message.getFaceName())){
                message.setFaceName(Base64Utils.encodeData(message.getFaceName()));
            }

            message.setFaceData(base64File);
            message.setFaceLen(base64File.length());
            message.setSign(signId);
            String jsonStr = JSON.toJSONString(message);



            ServerWriteUtils.writeHeadAndBody( Constans.AB  , jsonStr,
                    NettyChannelMap.getSocketChannel(license,LicenseSignEnum.NORMAL_LINK.getCode()));


            //布控手动记录日志
            logAddFaceProcess( license ,NettyChannelMap.getCameraTypeByLicense(license), message, LogTypeEnum.FACE_ADD.getCode(),signId,pathUrl);
        }else{
            //回收 redis空的队列
            RedisClientUtil.delete(redisKey);
            log.warn(" recovery redis  query is null, delete redis query!");
        }
    }

    /**
     * 保存同步日志
     * @param license
     * @param cameraType
     * @param requestParam
     * @param operType
     * @param sign
     * @param pathUrl
     */
    private void logAddFaceProcess(String license,Integer cameraType , FaceLibraryMessage  requestParam ,Integer operType,Long sign ,String pathUrl){

        requestParam.setFaceData(pathUrl);
        //------------布控单独插入日志记录
        MansFaceLogWithBLOBs mansFaceLog = new MansFaceLogWithBLOBs();
        mansFaceLog.setfId(sign);
        mansFaceLog.setLicense(license);
        mansFaceLog.setCameraType(cameraType);
        mansFaceLog.setParam(JSON.toJSONString(requestParam,true));
        mansFaceLog.setOperType(operType);
        mansFaceLogService.insert( mansFaceLog );
    }


    private void logDeleteFaceDbProcess(String license,Integer cameraType ,  DeleteAllFaceMessageLicense   requestParam ,Integer operType,Long sign ){

        //------------布控单独插入日志记录
        MansFaceLogWithBLOBs mansFaceLog = new MansFaceLogWithBLOBs();
        mansFaceLog.setfId(sign);
        mansFaceLog.setLicense(license);
        mansFaceLog.setCameraType(cameraType);
        mansFaceLog.setParam(JSON.toJSONString(requestParam,true));
        mansFaceLog.setOperType(operType);
        mansFaceLogService.insert( mansFaceLog );
    }

}
