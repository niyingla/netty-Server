package com.faceword.nio.business.entity;

import com.faceword.nio.business.ServerReceiveMessage;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


/**
 * @Author: zyong
 * @Date: 2018/11/5 18:01
 * @Version 1.0
 * 本地线程保存的消息
 */
@Slf4j
@Data
@ToString
public class LocalBuff {

    //分多次接收打包， 要通过buff保存记录
    StringBuilder buff;
    //记录包头中包体的length长度
    int len ;

    ServerReceiveMessage serverReceiveMessage;

    /**
     * 获取当前读取的长度
     * @return
     */
    public int getCurrLen (){
        if(buff == null){
            return 0;
        }
        return this.buff.toString().length();
    }

    /**
     * 判断当前的长度是否等于总长度
     * @return
     */
    public boolean isEquals(){
        return getCurrLen() == len ;
    }

}
