package com.faceword.nio.business;



import com.faceword.nio.business.component.AbstratExcutor;
import com.faceword.nio.business.entity.LocalBuff;
import com.faceword.nio.business.interceptor.excute.DefaultNioInvoation;
import com.faceword.nio.business.utils.ServerWriteUtils;
import com.faceword.nio.config.Constans;
import com.faceword.nio.service.CameraService;
import com.faceword.nio.utils.ByteUtils;
import com.faceworld.base.config.ApplicationContextProvider;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: zyong
 * @Date: 2018/11/1 10:46
 * @Version 1.0
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<ByteBuf> {


    /**
     *  注入相机的服务
     */
    private CameraService cameraService = ApplicationContextProvider.getBean( CameraService.class );


    /**
     * 只初始化一个实例注册
     */
    public static final ChannelHandler INSTANCE =  new NettyServerHandler();

    /**
     *定义local线程对象 保存多次的包信息
     */
    private ThreadLocal<LocalBuff> localBuffThreadLocal = new ThreadLocal<LocalBuff>();

    /**
     *默认的NIO拦截器
     */
    private DefaultNioInvoation defaultNioInvoation =  ApplicationContextProvider.getBean(DefaultNioInvoation.class);




    /**
     *  当读取到消息时，Netty触发channelRead()。
     * @param ctx
     * @param byteBuf
     */
    @Override
    protected  void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        LocalBuff localBuff = localBuffThreadLocal.get();

        //初始化读取包头
        if( localBuff == null ) {
            localBuff = NettyServerProcess.readInit(byteBuf);
        }
        //当前TCP请求的命令号
        short mode = localBuff.getServerReceiveMessage().getMode();
        //判断mode是否合法
        boolean sign =  NettyServerProcess.modeCheck(mode);
        if(!sign){
            log.warn( " 非法命令号  ->{}" , mode);
            NettyServerProcess.closeCtx(ctx);
            return ;
        }
        //通过spring 容器动态获取nio消费对象
        AbstratExcutor excutor = (AbstratExcutor) ApplicationContextProvider.getBean(String.valueOf(mode));
        defaultNioInvoation.setExcutor(excutor);
        //使用拦截器模式实现接口的调用
        defaultNioInvoation.invoke( ctx, byteBuf, localBuff, localBuffThreadLocal );
    }


    /**
     * 连接断开时都会触发 channelInactive 方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String clientId =  ctx.channel().id().asLongText();
        log.info("############客户端连接【断开】###############");
        //如果客户端断开，释放掉打开的句柄
        if( NettyChannelMap.isNormalLink(clientId)  ){
            cameraService.updateCameraDownLine( NettyChannelMap.getLicense( clientId )  );
        }
        NettyServerProcess.closeCurrentCtx(ctx);

    }

    /**
     *  当socket建立连接时，Netty触发一个inbound事件channelActive
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        log.info("############客户端连接【建立】###############");

    }

    /**
     * 处理过程中ChannelPipeline中发生错误时被调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("############NIO出现【异常】->{}",cause );

        //发生异常 关闭资源 释放ChannelHandlerContext想关联的句柄资源
        NettyServerProcess.closeCurrentCtx(ctx);
    }


    /**
     * Channel已经注册到一个EventLoop上 , 可以确定的是 在channelUnregistered事件之后
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channelRegistered");
        //可以在此进行ip白名单限制
    }

    /**
     * Channel已创建，还未注册到一个EventLoop上
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        //TODO 可以在这个过程做校验

        log.info("------channelUnregistered======");
    }

    /**
     * 当读取不到消息后，Netty触发ChannelReadCompleted()
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //log.info( "channelReadComplete" );
    }


}