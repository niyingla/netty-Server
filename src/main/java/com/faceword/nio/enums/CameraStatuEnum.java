package com.faceword.nio.enums;

/**
 * @Author: zyong
 * @Date: 2018/11/14 14:03
 * @Version 1.0
 *  相机状态枚举
 */
public enum CameraStatuEnum {


    ONLINE( 1 , "上线"),
    DOWNLINE( 0 ,"下线");


    private Integer code;

    private String value;

    CameraStatuEnum(Integer code, String value) {
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

    public static CameraStatuEnum typeOf(Integer code) {
        for (final CameraStatuEnum option : CameraStatuEnum.values()) {
            if (option.getCode().equals(code)) {
                return option;
            }
        }
        return null;
    }
}
