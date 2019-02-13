package com.faceword.nio.annotations;


import com.faceword.nio.enums.LogTypeEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @Author: zyong
 * @Date: 2018/11/21 9:25
 * @Version 1.0
 * 设备操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAnnotation {

    /**
     * 操作方法名称 <br>
     * @return
     */
     String title();

    /**
     * 方法信息（后期扩展）
     * @return
     */
     String message() default "";

    /**
     * 操作方法类型
     * @return
     */
     LogTypeEnum type();

    /**
     * 是否记录日志
     * @return
     */
    boolean isRecord() default true;

    /**
     * 敏感数据不记录参数
     * @return
     */
    boolean sensitive() default false;

    /**
     * 参数类型,json、key
     * @return
     */
    String parameterType() default "";


    /**
     * 是否从aop中自动注入参数（用于人脸库同步结果标记记录，设备处理完后会返回标记号）
     * @return
     */
    boolean isSign() default false;




}