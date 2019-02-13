package com.faceword.nio.service;

import com.faceword.nio.ai.arcsoft.entity.FaceSearch;
import com.faceword.nio.annotations.SendMassageToRabbitQuery;
import com.faceword.nio.business.entity.EquipmentReportInformation;
import com.faceword.nio.enums.CameraTypeEnum;
import com.faceword.nio.enums.ListTypeEnum;
import com.faceword.nio.enums.StatusEnum;
import com.faceword.nio.service.algorithm.entity.AgeGender;
import com.faceword.nio.utils.SnowflakeIdWorekrUtils;
import com.faceworld.base.mybatis.mapper.MansAlarmBlackMapper;
import com.faceworld.base.mybatis.mapper.MansAlarmStrangeMapper;
import com.faceworld.base.mybatis.mapper.MansAlarmWhiteMapper;
import com.faceworld.base.mybatis.model.MansAlarmBlack;
import com.faceworld.base.mybatis.model.MansAlarmStrange;
import com.faceworld.base.mybatis.model.MansAlarmWhite;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: zyong
 * @Date: 2018/11/21 18:56
 * @Version 1.0
 * 抓拍机service
 */
@Slf4j
@Service
@Transactional(readOnly = true )
public class GrabMachineService {


    /**
     * 黑名单告警 dao
     */
    @Autowired
     MansAlarmBlackMapper alarmBlacklistDao;

    /**
     * 白名单警告 dao
     */
    @Autowired
     MansAlarmWhiteMapper alarmWhiteDao;

    /**
     * 陌生人告警 dao
     */
    @Autowired
     MansAlarmStrangeMapper alarmStrangeDao;

    @Autowired
     EquipmentSynchronizationService equipmentSynchronizationService;

    @Autowired
    private RedisCacheService redisCacheService;

    @PostConstruct
    public void init(){
        System.out.println("redisCacheService==" + redisCacheService );

    }

    /**
     * 保存抓拍信息  ，不推荐调用
     */
    @Deprecated
    @Transactional
    public void saveCaptureInfo(EquipmentReportInformation recode) {
        equipmentSynchronizationService.processDeviceReportFaceInfo(recode , null);
    }

    /**
     * 保存陌生人
     * @param recode
     */

    @Transactional
    @SendMassageToRabbitQuery( type = CameraTypeEnum.GRAB_CAMERA ,messageType = ListTypeEnum.STRANGER_LIST )
    public MansAlarmStrange saveAlarmStrange(EquipmentReportInformation recode, AgeGender ageGender) {
        log.info("------------saveAlarmStrange------------");
        MansAlarmStrange mansAlarmStrange = new MansAlarmStrange();
        Long id = SnowflakeIdWorekrUtils.getOnlinePayId();
        mansAlarmStrange.setsId(id);
        mansAlarmStrange.setLicense(recode.getLicense());
        mansAlarmStrange.setsPictureType(Byte.valueOf(recode.getCapType()));
        mansAlarmStrange.setsFaceLen( Long.valueOf( recode.getDwPicLen().toString()));
        mansAlarmStrange.setsFacePath(recode.getUrl());

        //TODO 陌生人是没有相似度 跟阈值的 到时候上线了在具体修改
        // mansAlarmStrange.setsSimilarPair(recode.get);
        //mansAlarmStrange.setsThreshold(recode.getThreshold());
        mansAlarmStrange.setdFlag(StatusEnum.NORMAL.getCode().byteValue());
        mansAlarmStrange.setAge(ageGender.getAge());
        mansAlarmStrange.setGender(ageGender.getGender());
        mansAlarmStrange.setCreateTime(new Date());

        //2018-12-12
        mansAlarmStrange.setOrgId(redisCacheService.getCameraDepartment(recode.getLicense()));

        alarmStrangeDao.insert(mansAlarmStrange);
        return mansAlarmStrange;
    }

    /**
     * 保存黑名单告警
     * @param recode
     */

    @Transactional
    @SendMassageToRabbitQuery( type = CameraTypeEnum.GRAB_CAMERA ,messageType = ListTypeEnum.BLACK_LIST )
    public MansAlarmBlack saveAlarmBlack(EquipmentReportInformation recode , Object faceSearch, AgeGender ageGender) {

        if(faceSearch == null  ) {
            return null;
        }
        FaceSearch face = null;
        if(faceSearch instanceof  FaceSearch){
            face = (FaceSearch)faceSearch;
        }
        FaceSearch.FaceSearchResult faceSearchResult = null;
        String faceId = null;
        if(face.getResult()== null || face.getResult().size()==0){
            return null;
        }
        //TODO  先处理一张人脸
        faceSearchResult = face.getResult().get(0);
        faceId = faceSearchResult.getUid();

        log.info("------------saveAlarmBlack------------");
        BigDecimal threshold = new BigDecimal(0);
        BigDecimal similarPair = new BigDecimal(faceSearchResult.getScores().get(0));
        MansAlarmBlack mansAlarmBlack = new MansAlarmBlack();
        Long id = SnowflakeIdWorekrUtils.getOnlinePayId();
        mansAlarmBlack.setbId(id);
        mansAlarmBlack.setFaceId( Long.valueOf(faceId) );
        mansAlarmBlack.setLicense(recode.getLicense());
        mansAlarmBlack.setbPictureType(Byte.valueOf(recode.getEcoderType()));
        mansAlarmBlack.setbFaceLen( Long.valueOf(recode.getDwPicLen().toString()));
        mansAlarmBlack.setbFacePath(recode.getUrl());
        mansAlarmBlack.setbThreshold(threshold);
        mansAlarmBlack.setbSimilarPair(similarPair);
        mansAlarmBlack.setdFlag(StatusEnum.NORMAL.getCode().byteValue());
        mansAlarmBlack.setbCreateTime(new Date());
        mansAlarmBlack.setAge(ageGender.getAge());
        mansAlarmBlack.setGender(ageGender.getGender());

        //2018-12-12
        mansAlarmBlack.setOrgId(redisCacheService.getCameraDepartment(recode.getLicense()));
        alarmBlacklistDao.insert(mansAlarmBlack);

        return mansAlarmBlack;
    }

    /**
     * 保存白名单告警
     * @param recode
     */
    @Transactional
    @SendMassageToRabbitQuery( type = CameraTypeEnum.GRAB_CAMERA ,messageType = ListTypeEnum.WHITE_LIST )
    public MansAlarmWhite saveAlarmWhite(EquipmentReportInformation recode, Object faceSearch, AgeGender ageGender) {
        log.info("------------saveAlarmWhite------------");
        if(faceSearch == null  ) {
            return null;
        }
        FaceSearch face = null;
        if(faceSearch instanceof  FaceSearch){
            face = (FaceSearch)faceSearch;
        }
        FaceSearch.FaceSearchResult faceSearchResult = null;
        String faceId = null;
        if(face.getResult() == null || face.getResult().size()==0){
            return null;
        }
        //TODO  先处理一张人脸
        faceSearchResult = face.getResult().get(0);
        faceId = faceSearchResult.getUid();
        BigDecimal threshold = new BigDecimal(0);
        BigDecimal similarPair = new BigDecimal(faceSearchResult.getScores().get(0));

        MansAlarmWhite mansAlarmWhite = new MansAlarmWhite();
        Long id = SnowflakeIdWorekrUtils.getOnlinePayId();
        mansAlarmWhite.setwId(id);
        mansAlarmWhite.setFaceId( Long.valueOf(faceId) );
        mansAlarmWhite.setLicense(recode.getLicense());
        mansAlarmWhite.setwPictureType(Byte.valueOf(recode.getEcoderType()));
        mansAlarmWhite.setwFaceLen( Long.valueOf(recode.getDwPicLen()));
        mansAlarmWhite.setwFacePath(recode.getUrl());
        mansAlarmWhite.setwThreshold(threshold);
        mansAlarmWhite.setwSimilarPair(similarPair);
        mansAlarmWhite.setdFlag(StatusEnum.NORMAL.getCode().byteValue());

        mansAlarmWhite.setCreateTime(new Date());

        mansAlarmWhite.setAge(ageGender.getAge());
        mansAlarmWhite.setGender(ageGender.getGender());

        //2018-12-12
        mansAlarmWhite.setOrgId(redisCacheService.getCameraDepartment(recode.getLicense()));

        alarmWhiteDao.insert(mansAlarmWhite);

        return mansAlarmWhite;
    }
}
