package com.faceword.nio.business.entity;

import lombok.Data;

import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/11/7 15:27
 * @Version 1.0
 *  删除人脸库 客户端返回的对象
 *  3.8.删除人脸库
 */
@Data
public class DeleteFaceLibraryResponse {

    /**
     * 1删除成功 ,255删除失败
     */
    private Integer errcode;

    /**
     * 删除失败时才有意义，删除失败的个数
     */
    private Integer failFacesNum;

    /**
     * 人脸库组
     */
    private Long groupID;

    private List<Face> faildelFaces;

    private Long sign;
}

@Data
class Face{

    private Long faceID;
}
