package com.faceword.nio.mybatis.model;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/12/7 13:57
 * @Version 1.0
 * 人脸库对应的所有人脸
 */
@Data
@ToString
public class FaceDbAllFace {

    private String groupId;

    private Integer version;

    private LongArrayList faceList;

    private Integer listType;
}
