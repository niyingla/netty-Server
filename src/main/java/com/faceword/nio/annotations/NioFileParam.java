package com.faceword.nio.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: zyong
 * @Date: 2018/11/8 14:09
 * @Version 1.0
 * 文件上传参数级别的注解
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface NioFileParam {

    String value() default "";


    /**
     * 主要用来处理文件
     * @return
     */
    String type() default "file";

    /**
     * 文件最大5M
     * @return
     */
    long size() default 1024 * 1024 * 5;

    /**
     * 是否上传文件
     * @return
     */
    boolean isUpload() default false;
}
