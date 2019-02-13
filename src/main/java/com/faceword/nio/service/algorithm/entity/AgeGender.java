package com.faceword.nio.service.algorithm.entity;

import lombok.Data;

/**
 * @Author: zyong
 * @Date: 2018/11/13 14:23
 * @Version 1.0
 *  返回疑似年龄和性别
 */
@Data
public class AgeGender {

    Integer age;

    /**
     * 疑似性别（male 男， female女）
     */
    String gender;


}
