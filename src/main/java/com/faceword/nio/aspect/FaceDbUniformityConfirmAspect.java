package com.faceword.nio.aspect;


import com.faceword.nio.annotations.FaceDbUniformityConfirm;
import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.enums.LogTypeEnum;
import com.faceword.nio.redis.RedisConstans;
import com.faceworld.base.redis.RedisClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/12/7 9:30
 * @Version 1.0
 * 人脸库一致性切面检测
 */
@Slf4j
@Order(100)
@Aspect
@Component
public class FaceDbUniformityConfirmAspect {


    /**
     * 人脸库一致性扫描路径
     */
    @Pointcut("execution(* com.faceword.nio.handle.*.*(..))")
    public void faceCheckPoint() { }

    @Pointcut("@annotation(com.faceword.nio.annotations.FaceDbUniformityConfirm)")
    public void messageAnnotationPoint() { }

    /**
     *  切面通知类
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("faceCheckPoint() && messageAnnotationPoint()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("------------FaceDbUniformityConfirmAspect--------------");
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        FaceDbUniformityConfirm dbUniformityConfirm = methodSignature.getMethod().getAnnotation(FaceDbUniformityConfirm.class);
        if ( dbUniformityConfirm == null ) {
            return joinPoint.proceed();
        }
        Object[] args = joinPoint.getArgs();
        LogTypeEnum logTypeEnum = dbUniformityConfirm.type();
        //请求的参数
        ServerReceiveMessage receiveMessage = null;
        if( args != null && args.length > 0){
            for (Object paramArgs : args ) {
                if(paramArgs instanceof ServerReceiveMessage ){
                    receiveMessage = (ServerReceiveMessage) paramArgs;
                    break;
                }
            }
        }else{
            log.warn(" ServerReceiveMessage param type is not fond ! ");
            return joinPoint.proceed();
        }
        String license = receiveMessage.getLicense();


        if(logTypeEnum == LogTypeEnum.FACE_ADD ){
            log.info("---------------FACE_ADD--------------");
            //人脸添加
            String redisKey = RedisConstans.FACE_DATABASE_ADD_QUERY.concat(license);
            processCheck(license ,redisKey);
        }else if( logTypeEnum == LogTypeEnum.FACE_DELETE  ) {
            log.info("---------------FACE_DELETE--------------");
            //删除人脸库人脸
            String redisKey = RedisConstans.FACE_DELETE_QUERY.concat(license);
            processCheck(license, redisKey);

        }else if( logTypeEnum == LogTypeEnum.FACE_DELETE_ALL  ){
            log.info("---------------FACE_DELETE_ALL--------------");
            //删除人脸库
            String redisKey = RedisConstans.FACE_DELETE_ALL_QUERY.concat(license);
            processCheck(license ,redisKey);
        }else{
            log.warn(" current annotation is not fond  ");
        }

        return joinPoint.proceed();
    }

    private void processCheck( String license,String redisKey ){

        Integer synchronizationCnt = RedisClientUtil.get(RedisConstans.FACE_VERSION_CONTRACT.concat(license));
        log.info("redis key = {} , synchronizationCnt = {}" , RedisConstans.FACE_VERSION_CONTRACT.concat(license) ,synchronizationCnt );
        if(synchronizationCnt!=null && synchronizationCnt>0 ){
            Long len = RedisClientUtil.querySize( redisKey );
            if( len == 0 ){
                //将操作加1 ， 标志 向设备删除 人脸库人脸  或者 向设备添加人脸库人脸 中的其中一件事件已经完结
                RedisClientUtil.increment(RedisConstans.FACE_VERSION_CONTRACT.concat(license) );

            }
        }

    }

}
