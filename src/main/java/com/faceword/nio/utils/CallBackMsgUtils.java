package com.faceword.nio.utils;

import com.faceword.nio.common.CallBackMsg;
import com.faceword.nio.enums.ResonceCodeEnum;

/**
 * @Author: zyong
 * @Date: 2018/11/10 11:20
 * @Version 1.0
 *
 */
public class CallBackMsgUtils {


    /**
     * 没有license
     * @return
     */
    public static CallBackMsg noLicense(){
        CallBackMsg callBackMsg = new CallBackMsg();
        callBackMsg.setErrcode(ResonceCodeEnum.NO_LICENSE.getCode());
        callBackMsg.setMessage(ResonceCodeEnum.NO_LICENSE.getValue());
        return callBackMsg;
    }

    /**
     *  当前客户端没有在线
     * @return
     */
    public static CallBackMsg isLicenseNoline(){
        CallBackMsg callBackMsg = new CallBackMsg();
        callBackMsg.setErrcode(ResonceCodeEnum.NO_LICENSE_ONLINE.getCode());
        callBackMsg.setMessage(ResonceCodeEnum.NO_LICENSE_ONLINE.getValue());
        return callBackMsg;
    }

    /**
     * 无参数成功方法
     * @return
     */
    public static  CallBackMsg noArgsSucess(){
        CallBackMsg callBackMsg = new CallBackMsg();
        callBackMsg.setErrcode(ResonceCodeEnum.SUCCESS.getCode());
        callBackMsg.setMessage(ResonceCodeEnum.SUCCESS.getValue());
        return callBackMsg;
    }

    /**
     * 不支持相机类型
     * @return
     */
    public static CallBackMsg notSupportCameraType(){
        CallBackMsg callBackMsg = new CallBackMsg();
        callBackMsg.setErrcode(ResonceCodeEnum.CAMERA_NO_SUPPORTED.getCode());
        callBackMsg.setMessage(ResonceCodeEnum.CAMERA_NO_SUPPORTED.getValue());
        return callBackMsg;
    }

    /**
     * 没有groupId 同意返回
     * @return
     */
    public static CallBackMsg noExistGroupId(){
        CallBackMsg callBackMsg = new CallBackMsg();
        callBackMsg.setErrcode(ResonceCodeEnum.NO_GROUP_ID.getCode());
        callBackMsg.setMessage(ResonceCodeEnum.NO_GROUP_ID.getValue());
        return callBackMsg;
    }

    /**
     * 有返回结果
     * @param obj
     * @return
     */
    public static CallBackMsg setRsSucess( Object obj ){
        CallBackMsg callBackMsg = new CallBackMsg();
        callBackMsg.setErrcode(ResonceCodeEnum.NO_GROUP_ID.getCode());
        callBackMsg.setMessage(ResonceCodeEnum.NO_GROUP_ID.getValue());
        callBackMsg.setT(obj);
        return callBackMsg;
    }

    /**
     * 服务端异常
     * @return
     */
    public static CallBackMsg exception(){
        CallBackMsg callBackMsg = new CallBackMsg();
        callBackMsg.setErrcode(ResonceCodeEnum.EXCEPTION.getCode());
        callBackMsg.setMessage(ResonceCodeEnum.EXCEPTION.getValue());
        return callBackMsg;
    }

    /**
     * 参数无效
     * @return
     */
    public static CallBackMsg invalidParam(){
        CallBackMsg callBackMsg = new CallBackMsg();
        callBackMsg.setErrcode(ResonceCodeEnum.INVALID_PARAM.getCode());
        callBackMsg.setMessage(ResonceCodeEnum.INVALID_PARAM.getValue());
        return callBackMsg;
    }
}
