package com.faceword.nio.utils;


import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @Author: zyong
 * @Date: 2018/11/1 11:40
 * @Version 1.0
 */
@Slf4j
public class Base64Utils {

    /**
     * base64 译码
     * @param code
     * @return
     */
    public static byte[] base64Decode(String code){

        return Base64.getDecoder().decode(code.getBytes());
    }

    public static String base64Encode(byte [] bytes){

        return Base64.getEncoder().encodeToString(bytes);
    }

    // 字符串编码
    private static final String UTF_8 = "UTF-8";

    /**
     * 加密字符串
     * @param inputData
     * @return
     */
    public static String decodeData(String inputData) {
        try {
            if (null == inputData) {
                return null;
            }
            return new String(Base64.getDecoder().decode(inputData.getBytes(UTF_8)), UTF_8);
        } catch (UnsupportedEncodingException e) {
            log.error(inputData, e);
        }
        return null;
    }

    /**
     * 解密加密后的字符串
     * @param inputData
     * @return
     */
    public static String encodeData(String inputData) {
        try {
            if (null == inputData) {
                return null;
            }
            return new String(Base64.getEncoder().encode(inputData.getBytes(UTF_8)), UTF_8);
        } catch (UnsupportedEncodingException e) {
            log.error(inputData, e);
        }
        return null;
    }

    public static void main(String[] args) {

        String  endStr = Base64Utils.encodeData("张勇");

        System.out.println( endStr );

        System.out.println(Base64Utils.decodeData(endStr));
    }

}
