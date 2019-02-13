package com.faceword.nio.common;

import lombok.Data;

/**
 * @Author: zyong
 * @Date: 2018/11/10 11:13
 * @Version 1.0
 * 回调的参数
 */
@Data
public class CallBackMsg <T>  {

    /**
     * 1:成功，255:失败(失败直接断开与服务器之间的连接)
     */
    private Integer errcode;

    /**
     * 需要返回的对象
     */
    private T t;

    /**
     * 返回信息
     */
    private String message;
}
