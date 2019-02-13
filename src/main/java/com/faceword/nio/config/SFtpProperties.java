package com.faceword.nio.config;




import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/11/2 17:32
 * @Version 1.0
 * SFTP配置信息加载
 */
@Data
@Component
@ConfigurationProperties(prefix = "sftp")
public class SFtpProperties {

    private String address;

    private int port;

    private String userName;

    private String password;

    private String rootPath;

    private String fileDomain;



}
