package com.faceword.nio.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/11/5 20:03
 * @Version 1.0
 */
public class ByteUtils {


    /**
     * 定义的包头
     * mark:包头的标志，0x4c97
     * ver:协议的版本号, 高字节为大版本号，低字节为小版本号。
     * 比如第一个版本为V1.0, 则高字节值为1,低字节值为0。
     * devType:设备类型，0x01:抓拍机;0x02:一体机
     * mode:命令号，应答是在请求的基础上加1（如请求为0xa1,应答则为:0xa2）。
     * serial: 同类型消息的序列号，正常递增传递。
     * length:包体的长度。
     * @param mark
     * @param ver
     * @param devType
     * @param mode
     * @param serial
     * @param length
     * @return
     */
    public static  byte[] headConver(short mark ,short ver,short devType ,short mode,int serial,int length){

        // -- 转换成字节数组
        byte [] bytes1 = shortToByte(mark);
        byte [] bytes2 = shortToByte(ver);
        byte [] bytes3 = shortToByte(devType);
        byte [] bytes4 = shortToByte(mode);
        byte [] bytes5 = intToBytes(serial);
        byte [] bytes6 = intToBytes(length);

        int allLen = bytes1.length + bytes2.length + bytes3.length + bytes4.length + bytes5.length + bytes6.length;

        List<byte []> mergerList = new ArrayList<>();
        
        mergerList.add(bytes1);
        mergerList.add(bytes2);
        mergerList.add(bytes3);
        mergerList.add(bytes4);
        mergerList.add(bytes5);
        mergerList.add(bytes6);
        byte[] nByte = new byte[ allLen ];
        return byteArrayMerger(nByte , mergerList);
    }


    public static void main(String[] args) {

        byte[] bytes = headConver((short)1,(short)2,(short)3,(short)4,0,44);

        for (int i=0;i<bytes.length;i++){
            System.out.print(bytes[i]);
        }

//        String json = "gp4DkrIz9pMiVbEvsgm6DZFf1jADyG5Kna8xwQEGbkA=";
//        byte[] bytes2 =json.getBytes();
//        System.out.println(bytes2.length);
//        System.out.println();
//        for (int i=0;i<bytes2.length;i++){
//            System.out.print(bytes2[i]);
//        }
    }

    /**
     * 自定义包头字节数组合并
     * @param nByte
     * @param mergerList
     * @return
     */
    private static byte [] byteArrayMerger(byte[] nByte , List<byte []> mergerList){
        if( mergerList == null || nByte == null){
            return null;
        }
        int mergerIndex = 0;
        for ( byte [] bytes : mergerList){
               for (int i=0;i< bytes.length ;i++){
                   nByte[mergerIndex++] = bytes[i];
               }
        }
        return nByte;
    }

    public static void printByteArray(byte[] bytes){
        for (int i=0;i<bytes.length;i++){
            System.out.print(bytes[i]);
        }
        System.out.println();
    }

    public static byte[] shortToByte(short s){
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((s >>8)&0xFF);
        bytes[1] = (byte) (s &0xFF);
        return  bytes;
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1,0,byte_3,0,byte_1.length);
        System.arraycopy(byte_2,0,byte_3,byte_1.length,byte_2.length);
        return byte_3;
    }
    public static byte[] intToBytes(int value) {
        byte[] byte_src = new byte[4];
        byte_src[3] = (byte) ((value & 0x000000FF));
        byte_src[2] = (byte) ((value & 0x0000FF00) >> 8);
        byte_src[1] = (byte) ((value & 0x00FF0000) >> 16);
        byte_src[0] = (byte) ((value & 0xFF000000) >> 24);
        return byte_src;

    }

    public static byte[] intToMinByteArray(int i) {
        byte[] result = new byte[4]; //由高位到低位
        result[3] = (byte) ((i >> 24) & 0xFF);
        result[2] = (byte) ((i >> 16) & 0xFF);
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[0] = (byte) (i & 0xFF);
        return result;
    }



}
