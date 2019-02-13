package com.faceword.nio.ai.arcsoft.entity;

import lombok.Data;
import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/11/13 9:53
 * @Version 1.0
 * 对应人脸检索返回对象
 */
@Data
public class FaceSearch extends ArcsoftFaceToken{

    //对应匹配到的人脸数
    Integer result_num;


    List<FaceSearchResult> result;


    @Data
    public class FaceSearchResult{

        //对应的人脸id
        String uid;

        //对应的人脸库id
        String group_id;

        //对应添加用户的时间戳
        String user_info;

        List<Integer> scores;
    }

}

