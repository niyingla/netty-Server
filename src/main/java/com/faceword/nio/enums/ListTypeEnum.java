package com.faceword.nio.enums;

/**
 * @Author: zyong
 * @Date: 2018/11/6 16:18
 * @Version 1.0
 * 黑名单与白名单枚举
 */
public enum ListTypeEnum {

    BLACK_LIST (0 , "黑名单"),
    WHITE_LIST ( 1, "白名单"),
    STRANGER_LIST(2 , "陌生人"),
    SNAPSHOT_RECORD(3 , "抓拍记录");
    private Integer code;

    private String value;

    ListTypeEnum(Integer code, String value) {
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
