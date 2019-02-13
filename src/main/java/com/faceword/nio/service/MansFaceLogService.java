package com.faceword.nio.service;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.enums.SyncStatusEnum;
import com.faceword.nio.utils.SnowflakeIdWorekrUtils;
import com.faceworld.base.mybatis.mapper.MansFaceLogMapper;
import com.faceworld.base.mybatis.model.MansFaceLog;
import com.faceworld.base.mybatis.model.MansFaceLogWithBLOBs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Author: zyong
 * @Date: 2018/11/27 14:05
 * @Version 1.0
 * 服务向设备同步数据，日记记录表
 */
@Slf4j
@Service
@Transactional(readOnly = true )
public class MansFaceLogService {


   @Autowired
    MansFaceLogMapper  faceLogdDao;

    /**
     * 记录同步日志
     * @param mansFaceLog
     */
    @Transactional
    public void insert(MansFaceLogWithBLOBs mansFaceLog){
        log.info("insert add request insert log ");
        mansFaceLog.setCreateTime(new Date());
        mansFaceLog.setStatus(SyncStatusEnum.SYNCHRONIZING.getCode());
        faceLogdDao.insert(mansFaceLog);
    }

    /**
     * 修改同步记录
     * @param mansFaceLog
     * @param statusEnum
     */
    @Transactional
    public void update(MansFaceLogWithBLOBs mansFaceLog , SyncStatusEnum statusEnum){
        log.info("insert add request update log ");
        mansFaceLog.setUpdateTime(new Date());
        mansFaceLog.setOperType(statusEnum.getCode());
        faceLogdDao.updateByPrimaryKeySelective(mansFaceLog);
    }

    @Transactional
    public void delete(Long operId){
        log.info("insert add request delete log ");
        faceLogdDao.deleteByPrimaryKey(operId);
    }

    /**
     * 修改同步记录
     * @param operId 操作主键
     * @param result 设备返回的结果
     * @param statusEnum 需要修改的状态
     */
    @Transactional
    public void update(Long operId, Object result , SyncStatusEnum statusEnum){
        MansFaceLogWithBLOBs mansFaceLog = new MansFaceLogWithBLOBs();
        log.info("insert add request update log ");
        mansFaceLog.setfId(operId);
        mansFaceLog.setUpdateTime(new Date());
        mansFaceLog.setStatus( statusEnum.getCode() );
        mansFaceLog.setResult( JSON.toJSONString(result,true) );
        faceLogdDao.updateByPrimaryKeySelective( mansFaceLog );
    }
}
