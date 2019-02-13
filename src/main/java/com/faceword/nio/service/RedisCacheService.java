package com.faceword.nio.service;


import com.faceworld.base.redis.RedisClientUtil;
import com.faceworld.camera.base.common.RedisConstans;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/11/12 14:33
 * @Version 1.0
 * 操作redis 的服务
 */
@Slf4j
@Service
public class RedisCacheService {


    /**
     *  通过license 查询 人脸库的信息
     *  faceDbType 为 人脸库类型 人脸库类型 : 1:黑名单 0:白名单
     *  faceDbId 为人脸库id
     * "faceDbType":1,"faceDbId":"FACEW-00001-00003-1"
     * @return
     */
    public List<HashMap> getFaceInfoByLicense(String license){
       Object value =  RedisClientUtil.get(RedisConstans.CAMERA_FACE_DB_PREFIX.concat("license"));
       if( value == null ){
           log.info(" 不存在相机对应的人脸库  license --> {}" , license  );
           return  null;
       }
       return (List<HashMap>)value;
    }

    /**
     * 获取相机对应的机构
     * @param license
     * @return
     */
    public String getCameraDepartment(String license){
        String orgId = RedisClientUtil.get(RedisConstans.REDIS_CAMERA_CACHE_PREFIX.concat(license));
        if(StringUtils.isBlank(orgId)){
            return "";
        }
        return orgId;
    }
}
