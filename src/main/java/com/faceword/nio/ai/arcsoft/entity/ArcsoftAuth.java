package com.faceword.nio.ai.arcsoft.entity;

import lombok.Data;

/**
 * @Author: zyong
 * @Date: 2018/11/12 13:54
 * @Version 1.0
 */
@Data
public class ArcsoftAuth {

    private String access_token;

    private String token_type;

    private Long expires_in;
}
