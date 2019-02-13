package com.faceword.nio.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: zyong
 * @Date: 2018/11/8 15:02
 * @Version 1.0
 * NIO的注解，加在文件对象的文件类型上面
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public  @interface NioFieldFileTyleValue {

    /**
     * 标识文件类型字段
     * @return
     */
    String value() default "";
}
