package com.faceword.nio.service;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.annotations.NioFileConfig;
import com.faceword.nio.annotations.NioFileParam;
import com.faceword.nio.annotations.SendMassageToRabbitQuery;
import com.faceword.nio.business.entity.EquipmentReportInformation;
import com.faceword.nio.business.entity.FaceInformationReport;
import com.faceword.nio.common.UploadBean;
import com.faceword.nio.enums.CameraTypeEnum;
import com.faceword.nio.enums.ListTypeEnum;
import com.faceword.nio.enums.StatusEnum;
import com.faceword.nio.utils.SnowflakeIdWorekrUtils;
import com.faceworld.base.mybatis.mapper.MansAlarmBlackMapper;
import com.faceworld.base.mybatis.mapper.MansAlarmStrangeMapper;
import com.faceworld.base.mybatis.mapper.MansAlarmWhiteMapper;
import com.faceworld.base.mybatis.mapper.MansReportFaceInfoMapper;
import com.faceworld.base.mybatis.model.MansAlarmBlack;
import com.faceworld.base.mybatis.model.MansAlarmStrange;
import com.faceworld.base.mybatis.model.MansAlarmWhite;
import com.faceworld.base.mybatis.model.MansReportFaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * @Author: zyong
 * @Date: 2018/11/5 11:49
 * @Version 1.0
 * 设备信息持久化服务
 */
@Slf4j
@Service
public class EquipmentSynchronizationService {

    @Autowired
    private MansReportFaceInfoMapper mansReportFaceInfoDao;
    /**
     * 黑名单告警 dao
     */
    @Autowired
    private MansAlarmBlackMapper alarmBlacklistDao;

    /**
     * 白名单警告 dao
     */
    @Autowired
    private MansAlarmWhiteMapper alarmWhiteDao;

    /**
     * 陌生人告警 dao
     */
    @Autowired
    private MansAlarmStrangeMapper alarmStrangeDao;

    @Autowired
    private RedisCacheService redisCacheService;

    @PostConstruct
    public void init(){
        System.out.println("redisCacheService==" + redisCacheService );

    }

    /**
     *  用作测试sftp 用
     * @param str
     * @param reportInformation
     * @param uploadBean
     */
    @Deprecated
    @NioFileConfig( version = "1.0" )
    public void processDeviceReportFaceInfo2(String str ,@NioFileParam(isUpload = true)
            EquipmentReportInformation reportInformation , UploadBean uploadBean){
        log.info("----------------processDeviceReportFaceInfo2---------------");
        log.info( "json == -> {}" , JSON.toJSONString(uploadBean) );
    }

    /**
     * 只有抓拍机调用  - > 设备上报人脸信息
     * @param reportInformation
     */
    @SendMassageToRabbitQuery( type = CameraTypeEnum.GRAB_CAMERA ,messageType = ListTypeEnum.SNAPSHOT_RECORD )
    @NioFileConfig( version = "1.0" )
    @Transactional
    public MansReportFaceInfo processDeviceReportFaceInfo( @NioFileParam( isUpload = true) EquipmentReportInformation reportInformation ,
                                            UploadBean uploadBean){
          log.info("----------------processDeviceReportFaceInfo---------------");
            if(StringUtils.isBlank(uploadBean.getUrl())){
                //图片上传失败，不保存到数据库
                log.warn("图片上传失败，不保存到数据库");
                return null;
            }
            //----当前对象保存url 给后面的业务逻辑用
            reportInformation.setUrl(uploadBean.getUrl());

            //设备信息主键
            Long msId = SnowflakeIdWorekrUtils.getOnlinePayId();
            //设备上报抓拍人脸信息表
            MansReportFaceInfo mansReportFaceInfo = new MansReportFaceInfo();
            mansReportFaceInfo.setMsId( msId );
            mansReportFaceInfo.setLicense(reportInformation.getLicense());
            mansReportFaceInfo.setDwPicLen(reportInformation.getDwPicLen());
            mansReportFaceInfo.setCapType(reportInformation.getCapType());
            mansReportFaceInfo.setImgType(reportInformation.getImgType());
            mansReportFaceInfo.setEcoderType(reportInformation.getEcoderType());
            mansReportFaceInfo.setCapTime(reportInformation.getCapTime());
            mansReportFaceInfo.setFaceNum(reportInformation.getFaceNum());
            mansReportFaceInfo.setdFlag( StatusEnum.NORMAL.getCode().byteValue());
            mansReportFaceInfo.setRect( JSON.toJSONString(reportInformation.getRect()) );
            mansReportFaceInfo.setPicPath(uploadBean.getUrl());


            //2018-11-13
             mansReportFaceInfo.setAge(reportInformation.getAge());
             mansReportFaceInfo.setGender(reportInformation.getGender());
             mansReportFaceInfo.setCreateTime(new Date());

             //2018-12-12
            mansReportFaceInfo.setOrgId(redisCacheService.getCameraDepartment(reportInformation.getLicense()));
            mansReportFaceInfoDao.insert(mansReportFaceInfo);

            return mansReportFaceInfo;
    }

    /**
     *   一体机
     * 3.10.人脸信息及识别结果上报接口 - 黑名单告警
     * @param faceInformationReport
     *  faceInformationReport 里面 license
     */
    @SendMassageToRabbitQuery( type = CameraTypeEnum.INTEGRATE_CAMERA ,messageType = ListTypeEnum.BLACK_LIST )
    @Transactional
    @NioFileConfig( version = "1.0" )
    public MansAlarmBlack reportAlarmBlackListFaceInformation(@NioFileParam( isUpload = true) FaceInformationReport faceInformationReport,
                                                    UploadBean uploadBean){
          log.info("------------reportAlarmBlackListFaceInformation------------");

        if(StringUtils.isBlank(uploadBean.getUrl())){
            //图片上传失败，不保存到数据库
            log.warn("图片上传失败，不保存到数据库");
            return null;
        }
          MansAlarmBlack mansAlarmBlack = new MansAlarmBlack();
          Long id = SnowflakeIdWorekrUtils.getOnlinePayId();
          mansAlarmBlack.setbId(id);
          mansAlarmBlack.setFaceId(faceInformationReport.getFaceID());
          mansAlarmBlack.setLicense(faceInformationReport.getLicense());
          mansAlarmBlack.setbPictureType(Byte.valueOf(faceInformationReport.getPictureType().toString()));
          mansAlarmBlack.setbFaceLen(faceInformationReport.getFaceLen());
          mansAlarmBlack.setbFacePath(uploadBean.getUrl());
          mansAlarmBlack.setbThreshold(faceInformationReport.getThreshold());
          mansAlarmBlack.setbSimilarPair(faceInformationReport.getSimilarity());
          mansAlarmBlack.setdFlag(StatusEnum.NORMAL.getCode().byteValue());
          mansAlarmBlack.setbCreateTime(new Date());
          mansAlarmBlack.setUploadTime(faceInformationReport.getTime());

          //2018-12-12
          mansAlarmBlack.setOrgId(redisCacheService.getCameraDepartment(faceInformationReport.getLicense()));
          alarmBlacklistDao.insert(mansAlarmBlack);
          return mansAlarmBlack;
    }

    /**
     *   一体机
     * 3.10.人脸信息及识别结果上报接口 - 陌生人告警
     * @param faceInformationReport
     * 在 faceInformationReport 里面 license
     */
    @SendMassageToRabbitQuery( type = CameraTypeEnum.INTEGRATE_CAMERA ,messageType = ListTypeEnum.STRANGER_LIST )
    @Transactional
    @NioFileConfig( version = "1.0" )
    public MansAlarmStrange reportAlarmStrangeListFaceInformation(@NioFileParam( isUpload = true) FaceInformationReport faceInformationReport,
                                                    UploadBean uploadBean){
        log.info("------------reportAlarmStrangeListFaceInformation------------");

        if(StringUtils.isBlank(uploadBean.getUrl())){
            //图片上传失败，不保存到数据库
            log.warn("图片上传失败，不保存到数据库");
            return null;
        }
        MansAlarmStrange mansAlarmStrange = new MansAlarmStrange();
        Long id = SnowflakeIdWorekrUtils.getOnlinePayId();
        mansAlarmStrange.setsId(id);
        //mansAlarmStrange.setFaceId(faceInformationReport.getFaceID());
        mansAlarmStrange.setLicense(faceInformationReport.getLicense());
        mansAlarmStrange.setsPictureType(Byte.valueOf(faceInformationReport.getPictureType().toString()));
        mansAlarmStrange.setsFaceLen(faceInformationReport.getFaceLen());
        mansAlarmStrange.setsFacePath(uploadBean.getUrl());
        mansAlarmStrange.setsSimilarPair(faceInformationReport.getSimilarity());
        mansAlarmStrange.setsThreshold(faceInformationReport.getThreshold());
        mansAlarmStrange.setdFlag(StatusEnum.NORMAL.getCode().byteValue());
        mansAlarmStrange.setCreateTime(new Date());
        mansAlarmStrange.setUploadTime(faceInformationReport.getTime());

        //2018-12-12
        mansAlarmStrange.setOrgId(redisCacheService.getCameraDepartment(faceInformationReport.getLicense()));
        alarmStrangeDao.insert(mansAlarmStrange);
        return mansAlarmStrange;
    }


    /**
     *  一体机
     * 3.10.人脸信息及识别结果上报接口 - 白名单告警
     * @param faceInformationReport
     * 在 faceInformationReport 里面
     */
    @SendMassageToRabbitQuery( type = CameraTypeEnum.INTEGRATE_CAMERA ,messageType = ListTypeEnum.WHITE_LIST )
    @Transactional
    @NioFileConfig( version = "1.0" )
    public MansAlarmWhite reportAlarmWhiteListFaceInformation(@NioFileParam( isUpload = true) FaceInformationReport faceInformationReport,
                                                      UploadBean uploadBean){

        log.info("------------reportAlarmWhiteListFaceInformation------------");

        if(StringUtils.isBlank(uploadBean.getUrl())){
            //图片上传失败，不保存到数据库
            log.warn("图片上传失败，不保存到数据库");
            return null;
        }

        MansAlarmWhite mansAlarmWhite = new MansAlarmWhite();
        Long id = SnowflakeIdWorekrUtils.getOnlinePayId();
        mansAlarmWhite.setwId(id);
        mansAlarmWhite.setFaceId(faceInformationReport.getFaceID());
        mansAlarmWhite.setLicense(faceInformationReport.getLicense());
        mansAlarmWhite.setwPictureType(Byte.valueOf(faceInformationReport.getPictureType().toString()));
        mansAlarmWhite.setwFaceLen(faceInformationReport.getFaceLen());
        mansAlarmWhite.setwFacePath(uploadBean.getUrl());
        mansAlarmWhite.setwThreshold(faceInformationReport.getThreshold());
        mansAlarmWhite.setwSimilarPair(faceInformationReport.getSimilarity());
        mansAlarmWhite.setdFlag(StatusEnum.NORMAL.getCode().byteValue());
        mansAlarmWhite.setCreateTime(new Date());
        mansAlarmWhite.setUploadTime(faceInformationReport.getTime());

        //2018-12-12
        mansAlarmWhite.setOrgId(redisCacheService.getCameraDepartment(faceInformationReport.getLicense()));
        alarmWhiteDao.insert(mansAlarmWhite);
        return mansAlarmWhite;
    }
}
