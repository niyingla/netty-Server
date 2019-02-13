package com.faceword.nio.annotations;

import com.faceword.nio.enums.LogTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: zyong
 * @Date: 2018/12/7 9:20
 * @Version 1.0
 *  用来确认人脸库一致性
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FaceDbUniformityConfirm {

    /**
     * 解析设备端的bean类型
     * @return
     */
    Class<?> responceType() default Void.class;

    /**
     *  操作名称
     * @return
     */
    String title() default "";

    LogTypeEnum type() ;
}
