package com.faceword.nio.service.entity;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Author: zyong
 * @Date: 2018/11/6 16:17
 * @Version 1.0
 * 服务器请求设备添加人脸的对象
 */
@Data
public class FaceLibraryMessage {

    /**
     * listType:黑/白名单 0:黑名单注册，1:白名单注册
     */
    private int listType;
    /**
     * pictureType:照片类型
     * 0:jepg类型的人脸图
     * 1:png类型的人脸图
     * 2:jpg类型的人脸图
     * 3:bmp类型的人脸图
     */
    private int pictureType;

    /**
     * 人脸数据长度
     */
    private int faceLen;

    /**
     * 人脸数据，base64处理 【 如果是web端发起的请求，当前字段存的是http 路径】
     */
    private String faceData;

    /**
     * 图片的标识， 客户端处理完以后会返回  -- 为抓拍机和一体机的 faceID
     */
    private Long faceID;

    /**
     *  对应人脸库id
     */
    private Long groupID;

    /**
     * 签名标志
     */
    @ApiModelProperty(hidden = true)
    private Long sign ;

    /**
     * 人脸姓名
     */
    private String faceName;

    /**
     * 0是女，1是男
     */
    private Integer faceSex;


    @JsonFormat(pattern="yyyy-MM-dd")
    private String faceBirth;

    public String getFaceBirth() {
        return faceBirth;
    }

    /**
     * 人脸库的版本号
     */
    private Integer ver;



}
