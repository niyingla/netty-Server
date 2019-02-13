package com.faceword.nio.utils;

import com.faceword.nio.config.SnowflakeIdWorker;

/**
 * @Author: zyong
 * @Date: 2018/11/5 11:47
 * @Version 1.0
 *  雪花算法id生成工具类
 */
public class SnowflakeIdWorekrUtils {

    /**
     * 生成业务编号对象 <br>
     */
    private static SnowflakeIdWorker worker = new SnowflakeIdWorker(01L, 01L);

    /**
     * 获取在线支付商户编号 <br>
     * @return
     */
    public static Long getOnlinePayId() {
        return worker.nextId();
    }


}
