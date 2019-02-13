package com.faceword.nio.ai.arcsoft.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/11/12 14:52
 * @Version 1.0
 * 人脸发现实体类 ->人脸检测
 */
@Data
public class FaceDetect {

    /**
     * 人脸数目
     */
    private int result_num;

    /**
     * 人脸请求标识码，随机数，唯一
     */
    private String face_token;

    /**
     * 人脸详情列表
     */
    List<Result> result;

    @Data
   public class Result{

        Localtion location;
        //人脸置信度，范围0-1
        BigDecimal face_probability;
        //人脸框相对于竖直方向的顺时针旋转角，[-180,180]
        BigDecimal rotation_angle;
        //三维旋转之左右旋转角[-90(左), 90(右)]
        BigDecimal yaw;
        //三维旋转之俯仰角度[-90(上), 90(下)]
        BigDecimal pitch;
        //平面内旋转角[-180(逆时针), 180(顺时针)]
        BigDecimal roll;
        //年龄
        Integer age ;
        //性别
        String gender;
        //性别置信度，范围[0~1]，face_fields包含gender时返回
        BigDecimal gender_probability;


        @Data
        public class Localtion{

            private long left;
            private long top;
            private long width;
            private long height;
        }
    }
}



