package com.faceword.nio.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/11/29 20:05
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class DistributionControlVo {

    private String license;

    /**
     *  相机类型:0:普通摄像机,1:抓拍机,2:一体机
     */
    private int cameraType;

    /**
     * 人脸库列表
     */
    private List<String>  dbList;
}
