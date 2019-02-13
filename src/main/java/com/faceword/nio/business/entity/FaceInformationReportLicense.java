package com.faceword.nio.business.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @Author: zyong
 * @Date: 2018/11/7 16:08
 * @Version 1.0
 * 含有License的脸信息及识别结果上报接口
 * 3.10.人脸信息及识别结果上报接口
 */
@Data
@ToString
public class FaceInformationReportLicense extends FaceInformationReport {

    /**
     * 业务License
     */
    private String license;
}
