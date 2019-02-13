package com.faceword.nio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/12/11 14:45
 * @Version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "nio")
public class NioBaseConfig {

    private String basePackage;

    private String faceDbDomain;
}
