package com.faceword.nio.enums;

/**
 * @Author: zyong
 * @Date: 2018/11/14 15:07
 * @Version 1.0
 *  相机类型枚举
 */
public enum CameraTypeEnum {


    ORDINARY_CAMERA( 0 ,"普通摄像机" ),
    GRAB_CAMERA( 1 ,"抓拍机"),
    INTEGRATE_CAMERA( 2 ,"一体机");

    private Integer code;

    private String value;

    CameraTypeEnum(Integer code, String value) {
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

    public static CameraTypeEnum typeOf(Integer code) {
        for (final CameraTypeEnum option : CameraTypeEnum.values()) {
            if (option.getCode().equals(code)) {
                return option;
            }
        }
        return null;
    }
}
