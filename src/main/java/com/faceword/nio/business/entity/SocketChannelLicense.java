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
public class SocketChannelLicense {

    /**
     * 保存跟客户端交互的socket
     */
    SocketChannel socketChannel;

    /**
     * 业务的License
     */
    String license;



    public SocketChannelLicense(SocketChannel socketChannel, String license) {
        this.socketChannel = socketChannel;
        this.license = license;
    }
}
