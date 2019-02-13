package com.faceword.nio.enums;

/**
 * @Author: zyong
 * @Date: 2018/12/5 17:17
 * @Version 1.0
 */
public enum  SexEnum {

    MALE(0 , "女"),
    FEMALE (1 , "男");

    private Integer code;

    private String value;

    SexEnum(Integer code, String value) {
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
