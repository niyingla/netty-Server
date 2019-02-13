package com.faceword.nio.business.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/12/6 16:29
 * @Version 1.0
 * 6.2人脸库版本一致性对比-人脸库对比（设备发起人脸对比请求） 返回实体
 */
@ToString
@Data
public class ConsistencyCheckMessage {

    /**
     * 设备人脸库数量
     */
    private Integer groupNum;

    private List<Result> data;

    @Data
    @ToString
    public static class Result{
        //人脸库编号
        private Long groupID;
        //人脸库版本号
        private Integer ver;
    }

}
