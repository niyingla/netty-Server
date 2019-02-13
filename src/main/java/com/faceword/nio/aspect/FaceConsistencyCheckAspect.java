package com.faceword.nio.aspect;

import com.faceword.nio.annotations.FaceConsistencyCheckAnnotation;
import com.faceword.nio.annotations.FaceDbUniformityConfirm;
import com.faceword.nio.business.NettyChannelMap;
import com.faceword.nio.business.NettyServerProcess;
import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.business.utils.ServerWriteUtils;
import com.faceword.nio.common.DistributionControlVo;
import com.faceword.nio.config.Constans;
import com.faceword.nio.enums.LicenseSignEnum;
import com.faceword.nio.enums.LogTypeEnum;
import com.faceword.nio.redis.RedisConstans;
import com.faceword.nio.service.entity.DeleteAllFaceMessageLicense;
import com.faceword.nio.service.entity.DeleteFaceMessageLicense;
import com.faceword.nio.service.entity.FaceLibraryMessageLicense;
import com.faceword.nio.utils.CallBackMsgUtils;
import com.faceworld.base.redis.RedisClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/12/6 16:13
 * @Version 1.0
 * 人脸库一致性检测切面类  ，管理整个一致性的资源释放，与资源申请
 */
@Slf4j
@Order(1000)
@Aspect
@Component
public class FaceConsistencyCheckAspect {


    /**
     *  设备上报成功，触发事件
     */
    @Pointcut("execution(* com.faceword.nio.business.service.NettyService.process(..))")
    public void faceConsistencyCheckPoint() { }


    /**
     * 设备上报成功时触发
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("faceConsistencyCheckPoint()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("----------设备上报成功人脸库同步切面类触发-------");
        Object rsVal =  joinPoint.proceed();

        if(rsVal != null ){
            String license = rsVal.toString();
            NettyServerProcess.synchronousRequest(license);
        }
        return rsVal;
    }

    /**
     * nio触发aop的服务
     */
    @Pointcut("execution(* com.faceword.nio.handle.*.*(..))")
    public void faceConsistencyCheckAnnotationPoint() { }

    @Pointcut("@annotation(com.faceword.nio.annotations.FaceConsistencyCheckAnnotation)")
    public void messageAnnotationPoint() { }


    @Around("faceConsistencyCheckAnnotationPoint() && messageAnnotationPoint()")
    public Object aroundFaceConsistency(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("-------------aroundFaceConsistency----------------");
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        FaceConsistencyCheckAnnotation faceConsistencyCheckAnnotation = methodSignature.getMethod().getAnnotation(FaceConsistencyCheckAnnotation.class);
        if ( faceConsistencyCheckAnnotation == null ) {
            return joinPoint.proceed();
        }

        Object[] args = joinPoint.getArgs();
        //LogTypeEnum logTypeEnum = faceConsistencyCheckAnnotation.type();
        ServerReceiveMessage receiveMessage = (ServerReceiveMessage) args[0];
        String license = receiveMessage.getLicense();
        process(license);

        return joinPoint.proceed();
    }


    /**
     * 一致性同步完成检测逻辑
     * @param  license
     */
    private void process(String license){

        Integer synchronizationCnt = RedisClientUtil.get(RedisConstans.FACE_VERSION_CONTRACT.concat(license));
        log.info("check synchronizationCnt is {}" , synchronizationCnt);
        if(synchronizationCnt!=null && synchronizationCnt==3){
            //TODO 发送当前人脸库最新的版本号给设备
            //开启版本同步
            RedisClientUtil.delete(RedisConstans.FACE_VERSION_CONTRACT.concat(license));
            RedisClientUtil.increment(RedisConstans.FACE_VERSION_CONTRACT.concat(license));
            // 再次发起同步确认 ，解决同步过程中出现的人脸库变更问题
            ServerWriteUtils.writeHead( Constans.D1 , NettyChannelMap.getSocketChannel(license,LicenseSignEnum.NORMAL_LINK.getCode()));
            System.out.println("############################################################################");
            System.out.println("###################再次检查设备【"+license+"】同步状态###########################");
            System.out.println("############################################################################");
        }

    }

    /**
     * webserver 请求统一拦截器 ，如果设备进行一致性同步时，全部拦截
     */
    @Pointcut("execution(* com.faceword.nio.controller.NioServerController.*(..))")
    public void aroudServerRequestCheckPoint() { }


    @Around("aroudServerRequestCheckPoint()")
    public Object serverRequestCheckInterceptor(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("----------检查相机是否进行一致性同步-------");
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        FaceDbUniformityConfirm dbUniformityConfirm =  methodSignature.getMethod().getAnnotation(FaceDbUniformityConfirm.class);
        LogTypeEnum logConfig =  dbUniformityConfirm.type();
        if ( logConfig == null ) {
            return joinPoint.proceed();
        }
        String license = null;
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0 || args[0] ==null){
                return CallBackMsgUtils.noLicense();

        }
        if(args.length > 1){
            log.info(" current request param too many ,continue ");
            return joinPoint.proceed();
        }
        Object requestParam = args[0];

        if( logConfig == LogTypeEnum.SET_EQUIPMENT_TIME ){
            license = (String)requestParam;
        }else if( logConfig == LogTypeEnum.SERVER_DISCONNERCT_EQUIPMENT ){
            license = (String)requestParam;
        }else if(  logConfig == LogTypeEnum.FACE_ADD  ){
            FaceLibraryMessageLicense faceLibraryMessage = (FaceLibraryMessageLicense)requestParam;
            license = faceLibraryMessage.getLicense();
        }else if( logConfig ==  LogTypeEnum.FACE_DELETE ){
            DeleteFaceMessageLicense deleteFaceMessageLicense = (DeleteFaceMessageLicense)requestParam;
            license = deleteFaceMessageLicense.getLicense();
        }else if( logConfig == LogTypeEnum.FACE_DELETE_ALL ){
            DeleteAllFaceMessageLicense deleteAllFaceMessageLicense = (DeleteAllFaceMessageLicense)requestParam;
            license = deleteAllFaceMessageLicense.getLicense();
        }else if( logConfig == LogTypeEnum.FACE_DISTRIBUTION_CONTROL ){
            DistributionControlVo distributionControlVo = (DistributionControlVo)requestParam;
            license = distributionControlVo.getLicense();
        }

        //当前参数没有license
        if( StringUtils.isBlank(license) ){
            return CallBackMsgUtils.noLicense();
        }

        //检查当前license 有没在线
        if(!NettyChannelMap.isLicenseOnline( license  )){
            log.info("current license camera is no line = {} " , license);
            //直接返回  ，需要设备上线时进行人脸库一致性对比在进行同步
            return CallBackMsgUtils.noArgsSucess();
        }

        //检查当前相机是否再做一致性检测
        Integer synchronizationCnt = RedisClientUtil.get(RedisConstans.FACE_VERSION_CONTRACT.concat(license));
        if(synchronizationCnt!=null && synchronizationCnt>0){
            log.info(" current camera in synchronization ! ");
            //当前相机正在做一致性检测
            return CallBackMsgUtils.noArgsSucess();
        }

        return joinPoint.proceed();
    }
}
