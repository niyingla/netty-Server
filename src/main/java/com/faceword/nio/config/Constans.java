package com.faceword.nio.config;

/**
 * @Author: zyong
 * @Date: 2018/11/1 11:38
 * @Version 1.0
 */
public class Constans {

    /**
     * RC4 加密对应的key
     */
    final public static String RC4_KEY = "k1oET&Yj7@EQno2XdTP1o/Vo=";


    /**
     *默认客户端发送的心跳包命令号
     */
    final public static short COMMAND_NUMBER = 161 ;

    /**
     * 向服务器上报设备信息的命令号
     */
    final public static short EQUIPMENT_INFO = 163 ;


    /**
     *  服务器断开连接某个设备 Disconnect equipment
     */
    final public static short A9 = 169 ;

    /**
     * 服务器断开连接某个设备 客户端返回
     */
    final public static short A9_1 = 170 ;

    /**
     * 删除人脸库的命令号
     */
    final public static short DELETE_FACE = 173;


    /**
     * 定义包头 mark
     */
    final public static short MARK = 19607;

    /**
     * 定义包头 ver
     */
    final public static short VER = 10;

    /**
     * 定义包头 devType 0x01:抓拍机;0x02:一体机 ,0x03服务端
     */
    final public static short DEV_TYPE = 3;

    /**
     * 定义包头 mode 服务端给客户端回的mode
     */
    final public static short MODE = 162;

    /**
     * 定义包头 serial 服务端给客户端回的serial
     */
    final public static int serial = 0;

    /**
     * 定义包头 length 服务端给客户端回的length
     */
    final public static int length = 0;

    /**
     * 默认时间区域
     */
    final public static String TIME_ZONE = "GTM+8";

    final public static short A5 = 165;

    /**
     * 删除设备人脸库中的人脸 命令号
     */
    final public static short AD = 173;

    /**
     * 批量删除指定黑/白名单
     */
    final public static short B1 = 177;

    /**
     * 3.7.向设备人脸库中添加人脸信息接口
     */
    final public static short AB = 171;

    /**
     * 断网重连上报的编号 ，不需要过滤
     */
    final public static short E1 = 225 ;


    /*******************一下命令号没有加入到过滤器************************/
    /**
     * 6.1 基础服务器要求设备人脸库发起对比
     */
    final public static short D1 = 209;

    /**
     * 6.2 设备端与服务端对比人脸库版本号
     */
    final public static short D3 = 211;

    /**
     * 服务端回复设备 6.2
     */
    final public static short D4 = 212;

    /**
     * 服务端回复设备 6.2
     */
    final public static short D5 = 213;

    /**
     * 服务端回复设备 6.2
     */
    final public static short D6 = 216;

    /**
     * 6.4 人脸库版本一致性对比--修改设备人脸库版本号
     */
    final public static short D7 = 215;

    /**
     * 6.4 人脸库版本一致性对比--修改设备人脸库版本号
     */
    final public static short D8 = 216;
}
