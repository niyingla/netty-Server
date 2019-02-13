package com.faceword.nio.business.interceptor.excute;

import com.faceword.nio.business.NettyServerProcess;
import com.faceword.nio.business.component.AbstratExcutor;
import com.faceword.nio.business.entity.LocalBuff;
import com.faceword.nio.business.interceptor.LicenseInterceptor;
import com.faceword.nio.business.interceptor.NioInterceptor;
import com.faceword.nio.business.interceptor.NioInvocation;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/11/7 9:25
 * @Version 1.0
 *  默认的NIO拦截器容器类
 */
@Slf4j
@Component
public class DefaultNioInvoation extends NioInvocation {

    @PostConstruct //1在构造函数执行完之后执行
    public void init(){
        log.info(" ------------DefaultNioInvoation 拦截器初始化-------------------- ");
        this.addNioInterceptor( new LicenseInterceptor());

    }
    /**
     * 依赖具体调用器
     */
    private AbstratExcutor action;

    /**
     * 拦截器列表
     */
    private List<NioInterceptor> interceptors = new ArrayList<NioInterceptor>();

    /**
     * 获取执行器的对象
     * @return
     */
    public AbstratExcutor getExcutor(){
        return action;
    }

    /**
     * 动态改变执行器的对象
     * @param excutor
     */
    public void setExcutor (AbstratExcutor excutor){
        this.action = excutor;
    }

    /**
     * 添加到拦截器内部
     * @param nioInterceptor
     */
    public void addNioInterceptor(NioInterceptor nioInterceptor){
        this.interceptors.add(nioInterceptor);
    }

    /**
     * 封装调用过程
     * @return
     */
    public boolean invoke(ChannelHandlerContext ctx, ByteBuf byteBuf, LocalBuff localBuff, ThreadLocal<LocalBuff> localBuffThreadLocal) {
        for (NioInterceptor interceptor: this.interceptors ) {
            //执行过滤器
            boolean flag =  interceptor.before(ctx,byteBuf,localBuff);
            if(!flag){ //存在不合理的逻辑
                NettyServerProcess.closeCtx(ctx);
                return false;
            }
        }
        //执行业务
        action.excute(ctx,byteBuf,localBuff,localBuffThreadLocal);
        return true;
    }


}
