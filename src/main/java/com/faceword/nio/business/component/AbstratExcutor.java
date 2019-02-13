package com.faceword.nio.business.component;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.business.NettyChannelMap;
import com.faceword.nio.business.NettyServerProcess;
import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.business.entity.LocalBuff;
import com.faceword.nio.business.utils.ResponceMessageUtils;
import com.faceword.nio.business.utils.ServerWriteUtils;
import com.faceword.nio.dispatcher.NioMessageDispatcher;
import com.faceword.nio.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: zyong
 * @Date: 2018/11/6 19:10
 * @Version 1.0
 *  使用spring bean + 策略模式实现 buff 的处理
 */
@Slf4j
public abstract class AbstratExcutor {


    //注入分发器
    @Resource
    protected NioMessageDispatcher nioMessageDispatcher;
    /**
     * 初始化业务连接池 ， 不阻塞NIO的线程， 用自定义的线程完成操作
     */
    protected static ExecutorService threadPool = Executors.newFixedThreadPool(50);

    public String getClientId(ChannelHandlerContext ctx){
        return ctx.channel().id().asLongText();
    }

    /**
     *  对应客户端发来的命令具体的策略
     */
    public abstract void excute(ChannelHandlerContext ctx,
                                ByteBuf byteBuf ,
                                LocalBuff localBuff ,
                                ThreadLocal<LocalBuff> localBuffThreadLocal);
    /**
     *  数据量过大分批读取
     * @param ctx
     * @param byteBuf
     * @param localBuff
     * @param localBuffThreadLocal
     * @param isReply 是否回复设备消息
     */
    public void repeatRead(ChannelHandlerContext ctx, ByteBuf byteBuf,
                           LocalBuff localBuff, ThreadLocal<LocalBuff> localBuffThreadLocal,boolean isReply){
        ServerReceiveMessage serverReceiveMessage = localBuff.getServerReceiveMessage();
        //由于会出现数据包比较大的情况，使用本地线程保存当前，可以重复读
        localBuffThreadLocal.set( localBuff );
        //每次读取buff里面所有的内容
        localBuff.getBuff().append( ByteBufUtils.readChannelMessage( byteBuf ) );
        if( localBuff.isEquals() ){
            log.info("###############读取消息完成####################");
            try {
                //标志消息已经读完
                serverReceiveMessage.setBody( localBuff.getBuff().toString() );
                // License  的 Channel 绑定
                String currLicense = NettyChannelMap.getLicense( getClientId(ctx) );
               //使用线程池处理业务
                NettyServerProcess.process(threadPool,nioMessageDispatcher,serverReceiveMessage,currLicense);
                if(isReply){
                    //统一完成包头加 1  内容的发送
                    ServerWriteUtils.writeHeadAndBody( serverReceiveMessage , ResponceMessageUtils.getSuccessBody() ,ctx);
                }

            }catch (Exception e){
                log.error( "exception message :{}" , e );
            }finally {
                //必须netty线程池中释放本地线程内存
                localBuffThreadLocal.set(null);
            }
        }else if(localBuff.getCurrLen() > localBuff.getLen()){
            // 如果当前读取内容的长度超过了文本的长度 -> 信息不合法 ，防止内存泄漏
            localBuffThreadLocal.set(null);
            NettyServerProcess.closeCtx(ctx);
        }
    }

    /***
     * 复现问题使用
     * @param ctx
     * @param byteBuf
     * @param localBuff
     * @param localBuffThreadLocal
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T repeatRead(ChannelHandlerContext ctx, ByteBuf byteBuf, LocalBuff localBuff,
                             ThreadLocal<LocalBuff> localBuffThreadLocal,Class<T> clazz){
        ServerReceiveMessage serverReceiveMessage = localBuff.getServerReceiveMessage();
        //由于会出现数据包比较大的情况，使用本地线程保存当前，可以重复读
        localBuffThreadLocal.set( localBuff );
        //每次读取buff里面所有的内容
        localBuff.getBuff().append( ByteBufUtils.readChannelMessage( byteBuf ) );
       // log.warn("localBuff  == {}" , JSON.toJSONString(localBuff));
        if( localBuff.isEquals() ){
            log.info("##############读取消息完成##############");
            try {
                //进入 if 标志消息已经读完
                serverReceiveMessage.setBody( localBuff.getBuff().toString() );
                  T t = JSON.parseObject(serverReceiveMessage.getBody(),clazz);
                return t;
            }catch (Exception e){
                log.error( e.getMessage() );
            }finally {
                //必须netty线程池中释放本地线程内存
                localBuffThreadLocal.set(null);
            }
        }else if(localBuff.getCurrLen() > localBuff.getLen()){
            // 如果当前读取内容的长度超过了文本的长度 -> 信息不合法 ，防止内存泄漏
            localBuffThreadLocal.set(null);
            NettyServerProcess.closeCtx(ctx);
        }
        return null;
    }
    /**
     * 不需要对数据库状态进行操作
     * 单次读完的方法  ----多次调用会出现包读不完的异常
     * @param byteBuf
     * @param localBuff
     * @param clazz
     * @param <T>
     * @return
     */
    @Deprecated
    public <T> T  read( ByteBuf byteBuf, LocalBuff localBuff ,Class<T> clazz){
        localBuff.getBuff().append(ByteBufUtils.readChannelMessage( byteBuf  ));
        localBuff.getServerReceiveMessage().setBody( localBuff.getBuff().toString() );
        String jsonStr = localBuff.getServerReceiveMessage().getBody();
        T t = JSON.parseObject(jsonStr,clazz);
        return t;
    }

    /**
     * 需要对数据库状态进行操作
     * 单次读完的方法 ----多次调用会出现包读不完的异常
     * threadOper ,是否需要命令号对线程进行操作
     * @param byteBuf
     * @param localBuff
     * @param clazz
     * @param <T>
     * @return
     */
    @Deprecated
    public <T> T  read( ByteBuf byteBuf, LocalBuff localBuff ,Class<T> clazz,boolean threadOper,ChannelHandlerContext ctx){
        localBuff.getBuff().append( ByteBufUtils.readChannelMessage( byteBuf  ) );
        localBuff.getServerReceiveMessage().setBody( localBuff.getBuff().toString() );
        if(threadOper){
            //Channel 绑定的 License
            String currLicense = NettyChannelMap.getLicense(getClientId(ctx));
            NettyServerProcess.process(threadPool,nioMessageDispatcher,localBuff.getServerReceiveMessage(),currLicense);
        }
        String jsonStr = localBuff.getServerReceiveMessage().getBody();


        T t = JSON.parseObject(jsonStr,clazz);
        return t;
    }
}
