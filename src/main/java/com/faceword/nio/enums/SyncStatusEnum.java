package com.faceword.nio.enums;

/**
 * @Author: zyong
 * @Date: 2018/11/27 14:10
 * @Version 1.0
 * 数据的同步状态
 */
public enum SyncStatusEnum {

    SYNCHRONIZING( 0 ,"同步中" ),
    SYNCHRONIZTION_SUCCESS( 1 , "同步成功"),
    SYNCHRONIZTION_FAILURE( 2 , "同步失败");



    private Integer code;

    private String value;

    SyncStatusEnum(Integer code, String value) {
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

    public static SyncStatusEnum typeOf(Integer code) {
        for (final SyncStatusEnum option : SyncStatusEnum.values()) {
            if (option.getCode().equals(code)) {
                return option;
            }
        }
        return null;
    }
}
