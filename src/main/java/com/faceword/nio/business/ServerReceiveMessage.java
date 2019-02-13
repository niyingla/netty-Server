package com.faceword.nio.business;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: zyong
 * @Date: 2018/11/1 12:37
 * @Version 1.0
 * TCP 包头
 */
@Data
@ToString
@NoArgsConstructor
public class ServerReceiveMessage {

    //包头的标志，0x4c97
    private short mark;

    /**
     * 协议的版本号, 高字节为大版本号，低字节为小版本号。
     * 比如第一个版本为V1.0, 则高字节值为1,低字节值为0。
     */
    private short ver;

    /**
     * 设备类型，0x01:抓拍机;0x02:一体机
     */
    private short devType;

    /**
     * 命令号，应答是在请求的基础上加1（如请求为0xa1,应答则为:0xa2）
     */
    private short mode;

    /**
     * 同类型消息的序列号，正常递增传递
     */
    private int serial;

    /**
     * 包体的长度
     */
    private int length;

    /**
     * 内容
     */
    private String body;

    /**
     * 业务 license
     */
    private String license;




}
