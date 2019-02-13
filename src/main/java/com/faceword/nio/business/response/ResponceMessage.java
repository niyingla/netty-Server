package com.faceword.nio.business.response;

import lombok.Data;
import lombok.ToString;

/**
 * @Author: zyong
 * @Date: 2018/11/6 10:25
 * @Version 1.0
 */
@ToString
@Data
public class ResponceMessage {

    private Integer errcode;

    private Long sign;
}
