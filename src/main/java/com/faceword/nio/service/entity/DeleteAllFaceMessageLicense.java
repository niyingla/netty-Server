package com.faceword.nio.service.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @Author: zyong
 * @Date: 2018/11/7 15:41
 * @Version 1.0
 * 批量删除设备人脸的参数对象 ， 包括了License
 */
@Data
@ToString
public class DeleteAllFaceMessageLicense extends DeleteAllFaceMessage{

    private String license;
    /**
     *  相机类型:0:普通摄像机,1:抓拍机,2:一体机
     */
    private int cameraType;




}
