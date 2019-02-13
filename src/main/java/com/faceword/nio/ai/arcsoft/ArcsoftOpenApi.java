package com.faceword.nio.ai.arcsoft;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.ai.AbstructOpenApi;
import com.faceword.nio.ai.AiArcsoftProperties;
import com.faceword.nio.ai.arcsoft.entity.*;
import com.faceword.nio.redis.RedisConstans;
import com.faceword.nio.utils.HttpClientUtils;
import com.faceword.nio.utils.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author: zyong
 * @Date: 2018/11/12 13:29
 * @Version 1.0
 *  虹软的人脸库api
 */
@Slf4j
@Component
public class ArcsoftOpenApi extends AbstructOpenApi {

    /**
     * 虹软对应的用户信息注入
     */
    @Autowired
    private AiArcsoftProperties AiArcsoftProp;


    /**
     * 极视角auth的返回值
     * @return
     */
    @Override
    public String authValue() {
        String acess_token =  authorizations().getAccess_token();
        return "Bearer".concat(acess_token);

    }

    /**
     * auth认证
     * @return
     */
    public ArcsoftAuth authorizations(){
        log.info("--------authorizations---------");
        ArcsoftAuth arcsoftAuth = RestTemplateUtils.doPostForJSON_UTF8( AUTHORIZATION_URL,AiArcsoftProp,ArcsoftAuth.class);
        return arcsoftAuth;
    }

    /**
     * 刷新auth
     */
    public ArcsoftAuth refreshAuthorizations(String authValue){
        log.info("--------refreshAuthorizations---------");
        ArcsoftAuth arcsoftAuth =
                RestTemplateUtils.doPutAuthForJSON_UTF8(  REFRESH_AUTHORIZATION_URL,AUTH_HDEAR_PRIFFIX.concat(authValue),ArcsoftAuth.class);
        return arcsoftAuth;
    }



    /**
     *人脸检测
     *
     * 例如： http://47.98.253.106/aabbb/20181110/18/c6689d3be6b241d1bfa95b6508230ab9.png
     * @return
     */
    public FaceDetect faceDetect(byte [] bytes){
        log.info("-------------faceDetect-------------------");
        FaceDetect faceDetect = HttpClientUtils.sendHttpRequest(FACE_DETECT_URL, bytes ,AUTHORIZATION ,getAuthValue(RedisConstans.AI_ARCSOFT_AUTH ),null , FaceDetect.class);

        System.out.println(JSON.toJSONString(faceDetect));

        return faceDetect;
    }

    /**
     * 人脸添加
     * @param faceId 对应的是人脸库的人脸id
     * @param imgPath 人脸文件
     * @param groupId 人脸库id
     * @return
     */
    public ArcsoftFaceToken faceAdd(String imgPath ,String faceId,String groupId){
        log.info("-------------faceAdd-------------------");
        Map<String,String> paramMap = new TreeMap<>();
        paramMap.put("uid",faceId);
        paramMap.put("group_id",groupId);

        ArcsoftFaceToken arcsoftFaceToken =
                HttpClientUtils.sendHttpRequest(FACE_ADD,imgPath,AUTHORIZATION,getAuthValue(RedisConstans.AI_ARCSOFT_AUTH ),paramMap,ArcsoftFaceToken.class);

        System.out.println("arcsoftFaceToken ->" + JSON.toJSONString(arcsoftFaceToken));
        return arcsoftFaceToken;
    }

    /**
     * 人脸信息查询
     * @param faceId
     * @param groupId
     * @return
     */
    public FaceInfo getFaceInfo(String faceId,String groupId){
        log.info("-------------getFaceInfo-------------------");
        Map<String,String> paramMap = new TreeMap<>();
        paramMap.put("uid",faceId);
        paramMap.put("group_id",groupId);
        String imgPath = null;
        FaceInfo faceInfo = HttpClientUtils.sendHttpRequest(FACE_INFO_URL,imgPath,AUTHORIZATION,getAuthValue(RedisConstans.AI_ARCSOFT_AUTH ),paramMap,FaceInfo.class);
        System.out.println("FaceInfo ->" + JSON.toJSONString(faceInfo));
        return faceInfo;
    }

    /**
     * 人脸信息删除
     * @param faceId
     * @param groupId
     * @return
     */
    public ArcsoftFaceToken faceDelete(String faceId,String groupId){
        log.info("-------------faceDelete-------------------");
        Map<String,String> paramMap = new TreeMap<>();
        paramMap.put("uid",faceId);
        paramMap.put("group_id",groupId);
        String imgPath = null;
        ArcsoftFaceToken arcsoftFaceToken = HttpClientUtils.sendHttpRequest(FACE_DELETE_URL,imgPath,AUTHORIZATION,getAuthValue(RedisConstans.AI_ARCSOFT_AUTH ),paramMap,ArcsoftFaceToken.class);
        System.out.println("arcsoftFaceToken ->" + JSON.toJSONString(arcsoftFaceToken));
        return arcsoftFaceToken;
    }

    /**
     * 人脸检索
     * @param bytes 对应的是图片二进制数组
     * @param groupId 对应人脸库
     * @return
     */
    public FaceSearch faceSearch(byte [] bytes , String groupId){
        log.info("-------------faceSearch-------------------");
        Map<String,String> paramMap = new TreeMap<>();
        paramMap.put("group_id",groupId);
        FaceSearch faceSearch = HttpClientUtils.sendHttpRequest(FACE_SEARCH_URL,bytes,AUTHORIZATION,getAuthValue(RedisConstans.AI_ARCSOFT_AUTH ),paramMap,FaceSearch.class);
        System.out.println("faceSearch ->" + JSON.toJSONString(faceSearch));
        return faceSearch;
    }


}


