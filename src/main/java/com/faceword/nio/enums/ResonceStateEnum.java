package com.faceword.nio.enums;

/**
 * @Author: zyong
 * @Date: 2018/11/6 10:31
 * @Version 1.0
 */
public enum ResonceStateEnum {

    SUCCESS(1 , "成功");

    private Integer code;

    private String value;

    ResonceStateEnum(Integer code, String value) {
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
