package com.faceword.nio.aspect;


import com.faceword.nio.annotations.*;
import com.faceword.nio.mq.rabbit.sender.AbstarctSender;
import com.faceworld.base.mybatis.model.MansAlarmBlack;
import com.faceworld.base.mybatis.model.MansAlarmStrange;
import com.faceworld.base.mybatis.model.MansAlarmWhite;
import com.faceworld.base.mybatis.model.MansReportFaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: zyong
 * @Date: 2018/11/21 10:41
 * @Version 1.0
 *  rabbit 消息统一推送类
 */
@Slf4j
@Order(10000)
@Aspect
@Component
public class RabbitMqAsprct {

    /**
     * 设置service切入点
     */
    @Pointcut("execution(* com.faceword.nio.service.*.*(..))")
    public void messagePoint() { }

    @Pointcut("@annotation(com.faceword.nio.annotations.SendMassageToRabbitQuery)")
    public void messageAnnotationPoint() { }

    /**
     * 黑名单告警注入
     */
    @Resource
    AbstarctSender<MansAlarmBlack> alarmBlackSender;

    /**
     * GeneralReport
     * 陌生人告警注入
     */
    @Resource
    AbstarctSender<MansAlarmStrange> alarmStrangeSender;
    /**
     * 陌生人告警注入
     */
    @Resource
    AbstarctSender<MansAlarmWhite> alarmWhiteSender;

    @Resource
    AbstarctSender<MansReportFaceInfo> reportFaceInfoSender;

    /**
     * 初始化业务连接池 ， 不阻塞NIO的线程， 用自定义的线程完成操作
     */
    private static ExecutorService threadPool = Executors.newFixedThreadPool(2 );

    /**
     * 对使用rabbitMQ 注解的对象 进行消息统一发送
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("messagePoint() && messageAnnotationPoint()")
    public Object rabbitMQAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 目标方法是否存在 SendMassageToRabbitQuery 注解
        SendMassageToRabbitQuery massageToRabbitQuery = methodSignature.getMethod().getAnnotation(SendMassageToRabbitQuery.class);
        if ( massageToRabbitQuery == null ) {
            return joinPoint.proceed();
        }
        // 获取返回结果
        Object rsObject =  null;
       try {
           rsObject =  joinPoint.proceed();

           if(rsObject == null ){
               return rsObject;
           }
           final Object finalRsObject = rsObject;
           /**
            * 启动单独的线程池发送消息到MQ
            */
           threadPool.execute(() -> {
               try {
                   process(finalRsObject);
               }catch (Exception e){
                 log.info(e.getMessage());
               }
           });
       }catch (Exception e){
           e.printStackTrace();
       }finally {
           return rsObject;
       }
    }

    /**
     * 消息统一收集，发送
     * @param rsObject
     */
    private void process( Object rsObject ){
        if(rsObject instanceof MansReportFaceInfo){
            reportFaceInfoSender.senderMassage((MansReportFaceInfo)rsObject);
        }else if( rsObject instanceof  MansAlarmWhite){
            alarmWhiteSender.senderMassage((MansAlarmWhite)rsObject);
        }else if( rsObject instanceof MansAlarmStrange){
            alarmStrangeSender.senderMassage((MansAlarmStrange)rsObject);
        }else if(rsObject instanceof MansAlarmBlack){
            alarmBlackSender.senderMassage((MansAlarmBlack)rsObject);
        }
    }
}
