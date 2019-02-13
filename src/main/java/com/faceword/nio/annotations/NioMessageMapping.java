package com.faceword.nio.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * socket方法转发注解
 * @Author: zyong
 * @Date: 2018/10/31 13:48
 * @Version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NioMessageMapping {

    /**
     * 协议命令号
     * 应答是在请求的基础上加1
     * @return
     */
     String commandNumber() default "" ;

    /**
     * 协议的版本号, 高字节为大版本号，低字节为小版本号。
     * 比如第一个版本为V1.0, 则高字节值为1,低字节值为0。
     * @return
     */
    String version() default "";

}
