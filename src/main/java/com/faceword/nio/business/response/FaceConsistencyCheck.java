package com.faceword.nio.business.response;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/12/7 11:24
 * @Version 1.0
 * 6.4 人脸库版本一致性对比--修改设备人脸库版本号 返回的消息体
 */
@Data
@ToString
public class FaceConsistencyCheck {

    //人脸库数量
    private Integer groupNum;

    //列表详情
    private List<Result> data;

    @Data
    @ToString
    public static class Result{

        //人脸库id
        private String groupID;
        //对应人脸数量
        private Integer faceNum;
        //人脸数组
        private LongArrayList faceData;
    }

}
