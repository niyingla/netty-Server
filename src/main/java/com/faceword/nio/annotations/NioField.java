package com.faceword.nio.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: zyong
 * @Date: 2018/11/8 15:02
 * @Version 1.0
 * NIO的注解，加在对象的base64值得属性上
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public  @interface NioField {

    String value() default "";
}
