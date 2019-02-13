package com.faceword.nio.service.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: zyong
 * @Date: 2018/11/6 17:29
 * @Version 1.0
 */
@Data
public class DeleteAllFaceMessage {

    /**
     * sAll:是否删除全部人脸库，isAll=1，gruopID无效，
     * 删除全部人脸库isAll =0，删除groupID 这个组的人脸库
     */
    private int isAll ;

    /**
     * 当isAll不等于1时，删除编号为groupID的人脸库
     */
    private Long groupID;

    //签名
    @ApiModelProperty(hidden = true)
    private Long sign ;
}
