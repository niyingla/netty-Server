package com.faceword.nio.service.nio;


import com.alibaba.fastjson.JSON;
import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.business.entity.FaceInformationReport;
import com.faceword.nio.enums.ListTypeEnum;
import com.faceword.nio.redis.RedisConstans;
import com.faceword.nio.service.EquipmentSynchronizationService;
import com.faceword.nio.service.entity.FaceLibraryMessage;
import com.faceworld.base.config.ApplicationContextProvider;
import com.faceworld.base.redis.RedisClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.List;


/**
 * @Author: zyong
 * @Date: 2018/12/7 11:02
 * @Version 1.0
 * 处理设备返回消息的业务层次
 */
@Slf4j
@Service
public class NioProcessService {



    /**
     * 添加人脸列表到redis队列 （布控人脸库到相机）
     */
    public void addFaceListToRedisQuery(List<FaceLibraryMessage> faceLibraryMessageList , String domain ,String license){
        if(faceLibraryMessageList == null){
            log.warn(" face list is null , stop add face list camera!");
            return ;
        }

        if(StringUtils.isBlank(domain)){
            log.warn("domain is null , stop add face list to camera!");
            return ;
        }

        if(StringUtils.isBlank(license)){
            log.warn("license is null , stop add face list to camera!");
            return ;
        }
        int listSize = faceLibraryMessageList.size();
        for (int i=0 ;i<listSize ;i++){

            FaceLibraryMessage currentMessage = faceLibraryMessageList.get(i);
            currentMessage.setFaceData(domain.concat(currentMessage.getFaceData()));

            FaceLibraryMessage nextMessage = null;
            if( i+1 < listSize){
                nextMessage = faceLibraryMessageList.get( i+1 );

            }
            //当前人脸 对应的人脸库 与列表中下一个人脸对应的人脸库相同，版本号-1
            if( nextMessage!=null && currentMessage.getGroupID().equals(nextMessage.getGroupID())){
                currentMessage.setVer( currentMessage.getVer()  - 1 );
            }

            //放入队列进行消费
            RedisClientUtil.publisherQuery(RedisConstans.
                    FACE_DATABASE_ADD_QUERY.concat(license),currentMessage);

        }
    }

}
