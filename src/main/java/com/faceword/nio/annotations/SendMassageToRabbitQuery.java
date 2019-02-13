package com.faceword.nio.annotations;

import com.faceword.nio.enums.CameraTypeEnum;
import com.faceword.nio.enums.ListTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: zyong
 * @Date: 2018/11/21 10:24
 * @Version 1.0
 *  发送消息去 rabbitmq aop 处理类
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SendMassageToRabbitQuery {

    /**
     * 相机类型
     * @return
     */
     CameraTypeEnum type() ;

    /**
     * 抓拍记录
     * @return
     */
    ListTypeEnum messageType();

    String message() default "";
}
