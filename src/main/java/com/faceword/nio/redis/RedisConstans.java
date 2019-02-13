package com.faceword.nio.redis;

/**
 * @Author: zyong
 * @Date: 2018/11/12 14:30
 * @Version 1.0
 * redis常量定义
 */
public class RedisConstans {

    /**
     * ai 虹软算法的 auth 存放位置
     */
    final public static String AI_ARCSOFT_AUTH =  "ai:arcsoft:auth";

    /**
     * 默认设置AUTH_TIME 的时间为 7000L
     */
    final public static Long AUTH_TIME = 7000L;


    /**
     *  添加人脸库队列
     */
    final public static String FACE_DATABASE_ADD_QUERY = "face:db:add:";


    /**
     *  删除人脸库 人脸 队列
     */
    final public static String FACE_DELETE_QUERY = "face:db:del:";

    /**
     *  删除人脸库 人脸 队列
     */
    final public static String FACE_DELETE_ALL_QUERY = "face:db:delAll:";


    /**
     * 标志对应的相机在人脸对比
     *
     *  1 标识启动同步     3 标志人脸库一致性对比结束   ; -1 标志人脸库差异性
     */
    final public static String FACE_VERSION_CONTRACT = "face:ver:crt:";



    /**
     * 更新设备人脸库版本号键
     */
    final public static String FACE_VERSION_UP = "face:ver:up:";
}
