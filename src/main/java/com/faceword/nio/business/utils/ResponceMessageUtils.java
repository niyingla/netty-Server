package com.faceword.nio.business.utils;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.business.response.ResponceMessage;
import com.faceword.nio.business.response.ResponceStateMessage;
import com.faceword.nio.enums.ResonceCodeEnum;
import com.faceword.nio.enums.ResonceStateEnum;

/**
 * @Author: zyong
 * @Date: 2018/11/6 10:37
 * @Version 1.0
 * 回复给客户端的公用类
 */
public class ResponceMessageUtils {

    /**
     * 类似
     * {“errcode”:1}
     * 1:成功，255:失败(失败直接断开与服务器之间的连接)
     * @return
     */
    public static String getSuccessBody(){
        ResponceMessage responceMessage = new ResponceMessage();
        responceMessage.setErrcode(ResonceCodeEnum.SUCCESS.getCode());
        return JSON.toJSONString(responceMessage);
    }
    /**
     * 类似
     * {“errcode”:1}
     * 1:成功，255:失败(失败直接断开与服务器之间的连接)
     * @return
     */
    public static String getFailBody(){
        ResponceMessage responceMessage = new ResponceMessage();
        responceMessage.setErrcode(ResonceCodeEnum.FAIL.getCode());
        return JSON.toJSONString(responceMessage);
    }

    /**
     * {“errcode”:1,”state”:1}
     * @return
     */
    public static String   getSuccessStateBody(){
        ResponceStateMessage responceMessage = new ResponceStateMessage();
        responceMessage.setErrcode(ResonceCodeEnum.SUCCESS.getCode());
        responceMessage.setState(ResonceStateEnum.SUCCESS.getCode() );
        return JSON.toJSONString(responceMessage);
    }

    /**
     * {“errcode”:1,”state”:1}
     * @return
     */
    public static String   getFailStateBody(){
        ResponceStateMessage responceMessage = new ResponceStateMessage();
        responceMessage.setErrcode(ResonceCodeEnum.FAIL.getCode());
        responceMessage.setState(ResonceStateEnum.SUCCESS.getCode() );
        return JSON.toJSONString(responceMessage);
    }


}
