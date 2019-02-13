package com.faceword.nio.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数中存在文件数据的标志
 * @Author: zyong
 * @Date: 2018/10/31 13:48
 * @Version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NioFileConfig {

    /**
     * 标志这个文件体中是否存在文件
     * @return
     */
     boolean existFile() default true ;

    /**
     * 协议的版本号, 高字节为大版本号，低字节为小版本号。
     * 比如第一个版本为V1.0, 则高字节值为1,低字节值为0。
     * @return
     */
    String version() default "";

}
