package com.faceword.nio.enums;


/**
 * @Author: zyong
 * @Date: 2018/11/5 11:34
 * @Version 1.0
 * 对象数据状态枚举
 */
public enum StatusEnum {

    NORMAL(0 , "正常数据"),
    UNNOMAL (1 , "标记删除");

    private Integer code;

    private String value;

    StatusEnum(Integer code, String value) {
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
