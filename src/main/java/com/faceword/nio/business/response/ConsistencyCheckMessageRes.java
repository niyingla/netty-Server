package com.faceword.nio.business.response;

import lombok.Data;
import lombok.ToString;

/**
 * @Author: zyong
 * @Date: 2018/12/6 18:30
 * @Version 1.0
 * 6.2人脸库版本一致性对比-人脸库对比 人脸库出现差异版本的返回信息
 */
@ToString
@Data
public class ConsistencyCheckMessageRes extends ConsistencyCheckMessage {

    private Integer errcode;
}
