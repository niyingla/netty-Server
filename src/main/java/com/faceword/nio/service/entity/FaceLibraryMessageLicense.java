package com.faceword.nio.service.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @Author: zyong
 * @Date: 2018/11/7 16:40
 * @Version 1.0
 * 服务器请求设备添加人脸的对象包括了License
 */
@Data
@ToString
public class FaceLibraryMessageLicense extends   FaceLibraryMessage{


    /**
     *  相机类型:0:普通摄像机,1:抓拍机,2:一体机
     */
    private int cameraType;

    private String license;


}
