package com.faceword.nio.business.entity;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.annotations.NioField;
import com.faceword.nio.annotations.NioFieldFileTyleValue;
import com.faceword.nio.annotations.NioLicense;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: zyong
 * @Date: 2018/11/7 11:28
 * @Version 1.0
 * .人脸信息及识别结果上报
 */
@Data
public class FaceInformationReport {

    /**
     * pictureType:照片类型
     * 0:jepg类型的人脸图
     * 1:png类型的人脸图
     * 2:jpg类型的人脸图
     * 3:bmp类型的人脸图
     */
    @NioFieldFileTyleValue
    private Integer pictureType;

    /**
     * base64编码的图片
     */
    @NioField
    private String faceData;

    /**
     * 业务的license
     */
    @NioLicense
    private String  license;


    /**
     * 图片的长度
     */
    private Long faceLen;

    /**
     * 上报的人脸的识别结果 0:黑名单；1:白名单；2:陌生人
     */
    private Integer listType;

    /**
     * 客户端返回的faceID
     */
    private Long faceID;

    /**
     * 设备返回阈值
     */
    private BigDecimal threshold;

    /**
     * 设备返回相似度
     */
    private BigDecimal similarity;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date time;



}
