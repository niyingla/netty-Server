package com.faceword.nio.listener;


import com.faceword.nio.business.HeartBeatServerHandler;
import com.faceword.nio.config.NettyServerConfig;
import com.faceword.nio.business.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zyong
 * @Date: 2018/10/31 9:10
 * @Version 1.0
 */
@Slf4j
@Component
public class NettyServerListener {

    /**
     * 创建bootstrap 是一个启动NIO服务的辅助启动类
     */
    private ServerBootstrap bootstrap = new ServerBootstrap();
    /**
     * BOSS 用来接收进来的连接 默认的线程数是2*cpu ，基于Netty主从多线程模型 ， 所以 主线程设置为1 ，减少资源的浪费
     */
    private EventLoopGroup bossGroup = new NioEventLoopGroup( 1 );
    /**
     * Worker 用来处理已经被接收的连接 默认的线程数是 2*cpu核心数
     */
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    /**
     * NETT服务器配置类
     */
    @Resource
    private NettyServerConfig nettyConfig;
    /**
     * 关闭服务器方法
     */
    @PreDestroy
    public void close() {
        log.info("#########【关闭服务器】#########");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }


    /**
     * netty 启动参数配置
     */
    @Async
    public void process(){
        // 从配置文件中(application.yml)获取服务端监听端口号
        int port = nettyConfig.getPort();
        bootstrap.group( bossGroup, workerGroup );
        bootstrap.channel( NioServerSocketChannel.class );
        //保持长连接状态
        bootstrap.childOption( ChannelOption.SO_REUSEADDR, true );
        //设置这样做好的好处就是禁用nagle算法
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        //通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
        bootstrap.option( ChannelOption.TCP_NODELAY, true );
        //队列的大小 ， 如果客户端非常多，服务端处理不过来 ， 可以放在队列中等候处理
        bootstrap.option( ChannelOption.SO_BACKLOG, 200 );
        try {
            //设置事件处理
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline p = socketChannel.pipeline();
                    //使用心跳机制，如果60S内没有回复 就触发一次userEventTrigger()方法  ,可以解决内存泄漏的问题
                    p.addLast( new IdleStateHandler( HeartBeatServerHandler.readerIdleTime, 0, 0, TimeUnit.SECONDS ) );
                    //由于内部使用了连接池，避免多次被实例化
                    p.addLast( NettyServerHandler.INSTANCE );
                    //自定义心跳时间的逻辑
                    p.addLast( new HeartBeatServerHandler() );
                }
            });
            ChannelFuture f = bootstrap.bind(port).sync();
            log.info("netty服务器在【{}】端口启动监听", port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.info("【出现异常】 释放资源");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 开启及服务线程
     */
//    public void start() {
//        // -- 使用单独的一个线程启动Netty，不阻塞启动主线程
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                process();
//            }
//        }).start();
//    }
}
