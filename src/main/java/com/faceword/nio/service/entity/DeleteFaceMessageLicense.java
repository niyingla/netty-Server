package com.faceword.nio.service.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @Author: zyong
 * @Date: 2018/11/7 15:13
 * @Version 1.0
 * 删除人脸库的对象，包括了License ,该对象主要用户与webServer的http对接
 */
@ToString
@Data
public class DeleteFaceMessageLicense extends DeleteFaceMessage {

    private String license;

    /**
     *  相机类型:0:普通摄像机,1:抓拍机,2:一体机
     */
    private int cameraType;


}
