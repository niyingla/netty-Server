package com.faceword.nio.business.entity;

import com.faceword.nio.annotations.NioField;
import com.faceword.nio.annotations.NioFieldFileTyleValue;
import com.faceword.nio.annotations.NioLicense;
import com.faceword.nio.service.algorithm.entity.AgeGender;
import lombok.Data;

import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/11/5 11:52
 * @Version 1.0
 * 设备上报抓拍人脸信息
 */
@Data
public class EquipmentReportInformation extends AgeGender {

    //图片长度
    private Integer dwPicLen;
    //抓拍类型  0:人离开后抓拍 1:实时抓拍 2:间隔抓拍
    private byte capType ;
    //照片类型 0:场景图  1:人脸图
    private byte imgType;

    /**
     * 用来标志文件的类型 (0:jepg类型的图 ， 1:png类型的图 ， 2:jpg类型的图 ， 3:bmp类型的图 )
     */
    //照片类型
    @NioFieldFileTyleValue
    private byte ecoderType;
    //抓图的时间
    private String capTime;
    //场景中人脸的个数
    private Integer faceNum;

    private List<Rect> rect;

    /**
     * 文件上传是反射需要
     */
    @NioField
    private String picData;

    /**
     * 文件上传是反射需要
     */
    @NioLicense
    private String  license;

    private String url ;

}

//场景位置类
@Data
class Rect{

    private Integer x;
    private Integer y;
    private Integer w;
    private Integer h;
}
