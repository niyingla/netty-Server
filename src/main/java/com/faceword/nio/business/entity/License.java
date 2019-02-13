package com.faceword.nio.business.entity;

import lombok.Data;

/**
 * @Author: zyong
 * @Date: 2018/11/6 14:29
 * @Version 1.0
 */
@Data
public class License {

    /**
     *   分配给厂商的ID号+”,”+验证码，
     *     license采取Base64(Rc4(license))加密
     */
    private String license;

    private int devType;
}
