package com.faceword.nio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/10/31 9:15
 * @Version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "netty")
public class NettyServerConfig {

    /**
     * 端口
     */
    private int port;
    /**
     * 最大线程数
     */
    private int maxThreads;
    /**
     * 最大数据包长度
     */
    private int maxFrameLength;


}
