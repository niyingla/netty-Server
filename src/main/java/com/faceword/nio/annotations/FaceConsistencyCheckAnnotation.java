package com.faceword.nio.annotations;

import com.faceword.nio.enums.LogTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: zyong
 * @Date: 2018/12/6 16:04
 * @Version 1.0
 *  人脸库一致性校验注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FaceConsistencyCheckAnnotation {

    /**
     * 操作名称
     * @return
     */
    String title();

    /**
     * 方法信息（后期扩展）
     * @return
     */
    String message() default "";

    boolean isCheck() default false;

    /**
     * 类型
     * @return
     */
    LogTypeEnum type() ;

}
