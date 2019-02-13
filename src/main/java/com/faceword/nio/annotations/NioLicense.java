package com.faceword.nio.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  设备的License
 * @Author: zyong
 * @Date: 2018/10/31 13:48
 * @Version 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NioLicense {


    /**
     *  属性中存在 License 的标志
     * @return
     */
    String version() default "";

}
