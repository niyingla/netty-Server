package com.faceword.nio.business.entity;

import io.netty.channel.socket.SocketChannel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: zyong
 * @Date: 2018/11/6 13:46
 * @Version 1.0
 */
@Data
@ToString
@NoArgsConstructor
public class SocketChannelSign {

    /**
     * 保存跟客户端交互的socket
     */
    SocketChannel socketChannel;

    /**
     *  channel 业务的标志 ， 1 正常的连接， 2 断网续传的链接， 3视频的链接
     */
    Integer sign;

    /**
     * 相机类型
     */
    Integer cameraType;

    public SocketChannelSign(SocketChannel socketChannel, Integer sign ,Integer cameraType) {
        this.socketChannel = socketChannel;
        this.sign = sign;
        this.cameraType = cameraType;
    }
}
