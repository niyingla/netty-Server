package com.faceword.nio.utils.test;

/**
 * @Author: zyong
 * @Date: 2018/11/1 10:39
 * @Version 1.0
 */
import java.io.Serializable;

public class Message implements Serializable {

    private static final long serialVersionUID = -5756901646411393269L;

    private String clientId;//发送者客户端ID

    private MsgType type;//消息类型

    private String data;//数据

    private String targetId;//目标客户端ID

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public MsgType getType() {
        return type;
    }

    public void setType(MsgType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Message(){

    }

    public Message(MsgType type){
        this.type = type;
    }
}