package com.faceword.nio.ai.arcsoft.entity;

import lombok.Data;


/**
 * @Author: zyong
 * @Date: 2018/11/13 9:27
 * @Version 1.0
 */
@Data
public class ArcsoftFaceToken {

    /**
     * 3078223063060720//人脸请求标识码，随机数，唯一
     */
    private Long face_token;



    /**
     * 查询不到人脸时返回状态码  216611
     */
    long error_code;


    /**
     * 查询不到人脸时返回错误信息
     */
    String error_msg;
}
