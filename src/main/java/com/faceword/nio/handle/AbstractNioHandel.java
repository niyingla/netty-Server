package com.faceword.nio.handle;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.business.NettyChannelMap;
import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.business.entity.FaceInformationReport;
import com.faceword.nio.business.response.ConsistencyCheckMessage;
import com.faceword.nio.business.utils.ServerWriteUtils;
import com.faceword.nio.config.Constans;
import com.faceword.nio.enums.LicenseSignEnum;
import com.faceword.nio.enums.ListTypeEnum;
import com.faceword.nio.redis.RedisConstans;
import com.faceworld.base.redis.RedisClientUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zyong
 * @Date: 2018/12/17 9:01
 * @Version 1.0
 */
public abstract class AbstractNioHandel {


    /**
     * nio设备信息处理
     */
    public abstract void excute(ServerReceiveMessage receiveMessage);

    /**
     * 同步成功调用
     * @param license
     */
    protected void synchronousSuccess(String license){
        // 当前设备的人脸库与服务器的人脸库一致 ， 对比结束
        System.out.println("################################################################################################");
        System.out.println("################################【"+license+"】人脸库同步成功#######################");
        System.out.println("################################################################################################");
        this.sendNomalMessageToCamera(license);
        //TODO 释放资源  还有在设备掉线中也要释放资源
        //当前相机一致性同步完成
        RedisClientUtil.delete(RedisConstans.FACE_VERSION_CONTRACT.concat(license));
    }


    /**
     * 人脸库版本一致性对比，统一正常回复
     */
    public void sendNomalMessageToCamera(String license){

        Map<String, Integer> responceMap = new HashMap<>();
        responceMap.put("errcode",1);
        //回一个正常的消息 , 此时相机多余的人脸库已经被删除了
        ServerWriteUtils.writeHeadAndBody(Constans.D4, JSON.toJSONString(responceMap), NettyChannelMap.getSocketChannel(license, LicenseSignEnum.NORMAL_LINK.getCode()));

    }



    /**
     *  获取服务的人脸库多余设备人脸库的列表
     * @param sourceList
     * @param targetFaceMap
     * @return
     */
    public List<String> getServerFaceDbGreaterThanCameraDbList(List<ConsistencyCheckMessage.Result> sourceList , List<Map<String,String>> targetFaceMap){
        List<String> groupIdList = new ArrayList<>();
        for( Map<String,String> targetMap : targetFaceMap ){
            String groupId = targetMap.get("groupId");
            boolean isFond = false;
            for (ConsistencyCheckMessage.Result  sourceValue :sourceList) {
                String souceGroupId = String.valueOf(sourceValue.getGroupID());
                if(souceGroupId.equals(groupId)){
                    isFond = true;
                    break;
                }
            }
            if(!isFond){
                groupIdList.add(groupId);
            }
        }
        return groupIdList;
    }




}
