package com.faceword.nio.ai;

import com.faceword.nio.redis.RedisConstans;
import com.faceworld.base.redis.RedisClientUtil;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/11/10 16:52
 * @Version 1.0
 * 对调用算法的抽象
 */
@Component
public abstract class AbstructOpenApi {

    //-------------------------极视角  配置开始 -------------------------------------
    final public static String AUTHORIZATION =  "Authorization";
    //头部前缀
    final public static String AUTH_HDEAR_PRIFFIX = "Bearer";

    //-------------------------极视角URL-----------------------------
    final public static String AUTHORIZATION_URL = "https://api.cvmart.net/api/authorizations";
    final public static String REFRESH_AUTHORIZATION_URL = "https://api.cvmart.net/api/authorizations/current";
    final public static String FACE_DETECT_URL = "https://api.cvmart.net/api/face/detect";
    final public static String FACE_ADD = "https://api.cvmart.net/api/face/add";
    final public static String FACE_INFO_URL = "https://api.cvmart.net/api/face/get";
    final public static String FACE_DELETE_URL = "https://api.cvmart.net/api/face/del";
    final public static String FACE_SEARCH_URL = "https://api.cvmart.net/api/face/search";

    //-------------------------极视角 配置结束 -------------------------------------




    /**
     * 抽象方法，每个算法分别实现自己的auth 值获取
     * @return
     */
    public abstract String authValue();


    /**
     * 获取Cache的值
     * @param cacheKey
     * @return
     */
    public String getAuthValue(String cacheKey){
       String cacheValue =  RedisClientUtil.get(cacheKey);
       if(cacheValue == null){
           cacheValue = authValue();
           //当前的auth已经失效
           RedisClientUtil.set( cacheKey ,cacheValue ,RedisConstans.AUTH_TIME );
       }
        return cacheValue;
    }
}
