package com.faceword.nio.service;


import com.faceword.nio.enums.CameraStatuEnum;
import com.faceworld.base.mybatis.mapper.TbBusinessCameraMapper;
import com.faceworld.base.mybatis.model.TbBusinessCamera;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: zyong
 * @Date: 2018/11/14 13:59
 * @Version 1.0
 * 相机状态管理
 */
@Slf4j
@Service
@Transactional(readOnly = true )
public class CameraService {


    @Autowired
    private TbBusinessCameraMapper cameraDao;

    /**
     * 将相机状态修改为上线
     * @param license
     */
    @Transactional
    public void updateCameraOnLine(String license){
        if(StringUtils.isBlank(license)){
            log.info("----------license 不能为空---------");
            return ;
        }
        TbBusinessCamera record = new TbBusinessCamera();
        record.setCameraid( license );
        record.setCamerastatus(CameraStatuEnum.ONLINE.getCode());
        cameraDao.updateByPrimaryKeySelective(record);
    }



    /**
     * 将相机状态修改为下线
     * @param license
     */
    @Transactional
    public void updateCameraDownLine(String license){
        if(StringUtils.isBlank(license)){
            log.info("----------license 不能为空----------");
            return ;
        }
        TbBusinessCamera record = new TbBusinessCamera();
        record.setCameraid( license );
        record.setCamerastatus( CameraStatuEnum.DOWNLINE.getCode() );
        cameraDao.updateByPrimaryKeySelective(record);
    }


}
