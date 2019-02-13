package com.faceword.nio.enums;

/**
 * @Author: zyong
 * @Date: 2018/11/6 10:31
 * @Version 1.0
 */
public enum ResonceCodeEnum {

    SUCCESS(1 , "成功"),
    FAIL ( 255, "失败"),
    NO_LICENSE( 301 , "没有license" ),
    CAMERA_NO_SUPPORTED( 302 , "相机类型不支持" ),
    NO_GROUP_ID( 303 , "没有groupId" ),
    NO_LICENSE_ONLINE( 304 , "当前license对应的客户端没有上线" ),
    INVALID_PARAM( 305 , "参数无效" ),

    EXCEPTION(500,"出现异常"),

    VERSIOIN_INCONSISTENT(1000,"相机版本与服务器人脸库版本不一致");


    private Integer code;

    private String value;

    ResonceCodeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
