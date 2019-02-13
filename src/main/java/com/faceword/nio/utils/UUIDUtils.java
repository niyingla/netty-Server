package com.faceword.nio.utils;




import java.util.UUID;

/**
 * @Author: zyong
 * @Date: 2018/11/2 17:40
 * @Version 1.0
 */
public class UUIDUtils {


    /**
     * 生成UUID <br>
     * @return
     */
    public static String generetorUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }


}

