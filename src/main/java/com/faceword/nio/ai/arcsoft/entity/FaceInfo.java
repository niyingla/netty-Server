package com.faceword.nio.ai.arcsoft.entity;

import lombok.Data;

import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/11/13 9:38
 * @Version 1.0
 * 接收极视角用户信息查询返回
 */
@Data
public class FaceInfo extends  ArcsoftFaceToken{

    private Integer result_num;


    /**
     * 查询人脸对应的具体信息
     */
    List<FaceInfoResult> result;
}

@Data
class FaceInfoResult{

    //对应人脸库人脸id
    private String uid;

    //对应人脸库id
    private String group_id;

    //添加用户时刻的时间戳
    private String user_info;
}
