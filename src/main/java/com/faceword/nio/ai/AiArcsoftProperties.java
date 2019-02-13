package com.faceword.nio.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/11/2 17:32
 * @Version 1.0
 *  虹软算法的相关配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "nio.ai.arcsoft")
public class AiArcsoftProperties {

    private String username;

    private String password;


}
