package com.faceword.nio.aspect;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.annotations.LogAnnotation;
import com.faceword.nio.enums.LogTypeEnum;
import com.faceword.nio.service.MansFaceLogService;
import com.faceword.nio.service.entity.DeleteAllFaceMessageLicense;
import com.faceword.nio.service.entity.DeleteFaceMessageLicense;
import com.faceword.nio.service.entity.FaceLibraryMessageLicense;
import com.faceword.nio.utils.SnowflakeIdWorekrUtils;
import com.faceworld.base.mybatis.model.MansFaceLogWithBLOBs;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: zyong
 * @Date: 2018/11/27 11:01
 * @Version 1.0
 *
 */
@Slf4j
@Aspect
@Order(10000)
@Component
public class WebServerRequestLogAspect {


    @Autowired
    private MansFaceLogService mansFaceLogService;

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Pointcut("execution(public * com.faceword.nio.controller.NioServerController.*(..))")
    public void webRequestLogPoint() {}

    @Pointcut("execution(public * com.faceword.nio.service.ServiceInformationTransmissionService.*(..))")
    public void servicePoint(){}

    @Pointcut("@annotation(com.faceword.nio.annotations.LogAnnotation)")
    public void serviceAnnotationPiont(){}


    @Around("(webRequestLogPoint() && serviceAnnotationPiont() )||(servicePoint() && serviceAnnotationPiont()) ")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        /*****************在该对象中可以获取到目标方法名****************************/
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        /************通过注解是否保存日志-扩展  如果使用了自定义的注解 ，才出发日志保存*********************/
        LogAnnotation logConfig = methodSignature.getMethod().getAnnotation(LogAnnotation.class);
        if (logConfig == null || !logConfig.isRecord()) {
            return joinPoint.proceed();
        }
        if( logConfig.isRecord() ){
            //当前的请求需要记录
            Object[] args = joinPoint.getArgs();

            //统一生成唯一标识
            Long signId  = SnowflakeIdWorekrUtils.getOnlinePayId();
            if( args !=null && args.length >0){
               Object requestParam = args[0];

               if(logConfig.type() == LogTypeEnum.FACE_ADD ){

                   FaceLibraryMessageLicense faceLibraryMessage = (FaceLibraryMessageLicense)requestParam;
                   setFieldMethodValue(requestParam, signId );
                   process( faceLibraryMessage.getLicense(),faceLibraryMessage.getCameraType() ,requestParam,LogTypeEnum.FACE_ADD.getCode(),signId);
               }else if ( logConfig.type() ==  LogTypeEnum.FACE_DELETE ){

                   DeleteFaceMessageLicense deleteFaceMessageLicense = (DeleteFaceMessageLicense)requestParam;
                   setFieldMethodValue(requestParam,  signId );
                   process( deleteFaceMessageLicense.getLicense(),deleteFaceMessageLicense.getCameraType() ,requestParam,LogTypeEnum.FACE_DELETE.getCode(),signId);
               }else if( logConfig.type() ==  LogTypeEnum.FACE_DELETE_ALL  ){

                   DeleteAllFaceMessageLicense deleteAllFaceMessageLicense = (DeleteAllFaceMessageLicense)requestParam;
                   setFieldMethodValue(requestParam,  signId );
                   process( deleteAllFaceMessageLicense.getLicense(),deleteAllFaceMessageLicense.getCameraType() ,requestParam,LogTypeEnum.FACE_DELETE_ALL.getCode(),signId);
               }
            }
        }

        return joinPoint.proceed();
    }

    private void process(String license,Integer cameraType ,Object requestParam ,Integer operType,Long sign){

        MansFaceLogWithBLOBs mansFaceLog = new MansFaceLogWithBLOBs();
        mansFaceLog.setfId( sign );
        mansFaceLog.setLicense(license);
        mansFaceLog.setCameraType(cameraType);
        mansFaceLog.setParam(JSON.toJSONString(requestParam,true));
        mansFaceLog.setOperType(operType);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                mansFaceLogService.insert( mansFaceLog );
            }
        });
    }

    /**
     * 通过反射调用 属性的set 方法
     * @param object 调用传入的参数对象
     * @param sign 属性值
     * @return
     */
    private void setFieldMethodValue(Object object ,Long sign){
        try {
            Class clazz = object.getClass();
            Method method = clazz.getMethod("setSign",Long.class);
            //传入一个Long
            method.invoke( object ,sign );
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
