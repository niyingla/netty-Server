package com.faceword.nio.enums;

/**
 * @Author: zyong
 * @Date: 2018/11/21 9:27
 * @Version 1.0
 */
public enum  LogTypeEnum {


    //定义枚举中的常量
    FACE_ADD(0, "人脸添加"),
    FACE_DELETE(1, "人脸删除"),
    FACE_DELETE_ALL(2, "人脸批量删除"),
    SET_EQUIPMENT_TIME( 3, "设置设备时间"),
    SERVER_DISCONNERCT_EQUIPMENT(4 , "服务器断开设备"),


    FACE_DISTRIBUTION_CONTROL(10, "人脸布控");

    //数字（系统:0，客户:1，订单:2）
    private int code;

    //数字描述信息
    private String name;


    private LogTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
