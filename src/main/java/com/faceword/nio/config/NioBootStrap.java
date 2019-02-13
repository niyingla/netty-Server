package com.faceword.nio.config;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Set;

/**
 * 跟着应用加载
 * @Author: zyong
 * @Date: 2018/10/31 15:23
 * @Version 1.0
 * 系统初始化对象容器
 */
@Data
@Slf4j
//@Component
public class NioBootStrap {

    @Value("${nio.basePackage}")
    private String basePackage;

    /**
     * 直接加载
     */
    public static Set<Class<?>> bootStrapSet  ;



}
