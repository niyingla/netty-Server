package com.faceword.nio.enums;

/**
 * @Author: zyong
 * @Date: 2018/11/29 9:51
 * @Version 1.0
 */
public enum LicenseSignEnum {

    NORMAL_LINK( 1 ,"正常链接" ),
    BROKEN_NET_LINK( 2 , "断网链接"),
    VIDEO_LINK( 3 ,"下线");

    private Integer code;

    private String value;

    LicenseSignEnum(Integer code, String value) {
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

    public static LicenseSignEnum typeOf(Integer code) {
        for (final LicenseSignEnum option : LicenseSignEnum.values()) {
            if (option.getCode().equals(code)) {
                return option;
            }
        }
        return null;
    }
}
