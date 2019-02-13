package com.faceword.nio.service.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/11/6 17:03
 * @Version 1.0
 */
@Data
public class DeleteFaceMessage {

    /**
     * 删除的人脸库中人脸的个数，值为1为单张删除
     */
    int delFacesNum;

    /**
     * 人脸库组
     */
    Long groupID;

    /**
     * 删除人脸库中人脸的详细信息 接收web服务的数据
     */
    List<FaceID> delFaces;

    /**
     * 黑/白名单删除 0: 删除黑名单的人脸，1: 删除白名单的人脸
     */
    int listType;

    //签名
    @ApiModelProperty(hidden = true)
    private Long sign ;

    /**
     * 删除人脸库人脸版本号
     */
    private Integer ver;

    @Data
    @NoArgsConstructor
    public static class FaceID{
        Long faceID;

        public FaceID(Long faceID) {
            this.faceID = faceID;
        }
    }
}

