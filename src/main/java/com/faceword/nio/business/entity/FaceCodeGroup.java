package com.faceword.nio.business.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @Author: zyong
 * @Date: 2018/11/7 10:43
 * @Version 1.0
 * 向设备人脸库中添加人脸信息 客户端返回
 */
@Data
@ToString
public class FaceCodeGroup {

    /**
     * 1:注册成功 ,255注册失败
     */
    private Integer errcode;

    /**
     * 客户端返回的faceid
     */
    private Long faceID;

    /**
     * 人脸库的id
     */
    private String groupID;

    /**
     * 对应后台人脸的序列号
     */
    private Long sign;
}
