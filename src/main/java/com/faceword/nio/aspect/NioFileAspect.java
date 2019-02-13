package com.faceword.nio.aspect;


import com.faceword.nio.annotations.*;
import com.faceword.nio.common.UploadBean;
import com.faceword.nio.enums.ImageTypeEnum;
import com.faceword.nio.utils.SFtpUtils;
import com.faceword.nio.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;

/**
 * @Author: zyong
 * @Date: 2018/11/8 13:45
 * @Version 1.0
 * 用于处理NIO文件上传的AOP类
 */
@Slf4j
@Order(1)
@Aspect
@Component
public class NioFileAspect {


    /**
     * 设置service切入点
     */
    @Pointcut("execution(* com.faceword.nio.service.EquipmentSynchronizationService.*(..))")
    public void nioRequest() { }

    /**
     * 文件上传，接收设备的文件进行上传
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("nioRequest()")
    public Object nioFileAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 目标方法是否存在 NioFileConfig 注解
        NioFileConfig logConfig = methodSignature.getMethod().getAnnotation(NioFileConfig.class);
        if ( logConfig == null ) {
            return joinPoint.proceed();
        }

        if(logConfig.existFile()){
            //获取所有的参数
            Object[] args = joinPoint.getArgs();
            Parameter[] parameters = methodSignature.getMethod().getParameters();
            for(int i=0;i<parameters.length ;i++){
                NioFileParam apiParam = parameters[i].getAnnotation(NioFileParam.class);
                /**
                 * 参数存在注解，并且注释需要上传
                 */
                if( apiParam!=null && apiParam.isUpload()){
                    //得到base64的文件编码
                    String picData = getSignFieldMethodValue(args[i],NioField.class);
                    byte [] fileBytes = Base64Utils.decodeFromString( picData );
                    //返回 文件类型对应的 数字
                    String intTypeImageValue =  getSignFieldMethodValue(args[i], NioFieldFileTyleValue.class);
                    //返回文件类型对应的真实值 如：jpg
                    String fileType = getFileType(Integer.valueOf(intTypeImageValue));
                    //获取对象的License
                    String license =  getSignFieldMethodValue(args[i], NioLicense.class);
                    //这里不需要修改  sourceFileName 最终会被uuid给替代的， 只是用于字符的截取
                    // -- 用于测试UploadBean uploadBean = new UploadBean("sourceFileName.".concat(fileType) , IOUtil.readFile(new File("D:\\111123.jpg")));
                    UploadBean uploadBean = new UploadBean("sourceFileName.".concat(fileType),fileBytes);
                    //开始sftp 文件的上传  uploadFolder 为动态的文件夹  如根据日期动态
                    SFtpUtils.upload(license.concat("/").concat(generoteFilePrefix()) , uploadBean );
                   // log.info("sucess upload json -> {}" ,JSON.toJSONString(uploadBean));
                    //将返回的文件对象信息，传递给目标对象
                    args[ args.length -1] = uploadBean;
                    //将文件的参数传递给目标方法  动态给service 文件对象赋值
                    return joinPoint.proceed(args);

                }
            }
        }
        return joinPoint.proceed();
    }

    /**
     * 根据一定的规则生成前缀
     * @return
     */
    private String generoteFilePrefix(){
        return StringUtils.fileFormatDateYMDHMS(new Date());
    }

    private String getFileType(Integer fileTypeValue){

        if (fileTypeValue == null){
            fileTypeValue = 0;
        }
        return ImageTypeEnum.typeOf(fileTypeValue).getValue();
    }


    /**
     * 获取被注解修饰的属性值
     * @return
     */
    public String getSignFieldMethodValue(Object argsObject ,Class annotationclazz ){
        Class clazz =  argsObject.getClass();
        //获取参数对象的所有属性值
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            //打开私有访问
            if(!field.isAccessible()){
                field.setAccessible(true);
            }
            //获取存在属性的方法名
            Object nioField = field.getAnnotation( annotationclazz );
            if(nioField!=null){

                return getFieldMethodValue(argsObject,clazz,field.getName());
            }
        }
        return "";
    }

    /**
     * 通过反射调用 属性的get 方法
     * @param Object 调用传入的参数对象
     * @param clazz 调用的class类型
     * @param fieldName 属性名称
     * @return
     */
    private String getFieldMethodValue(Object Object , Class clazz ,  String fieldName){

        try {
            Method method = clazz.getMethod("get".concat(StringUtils.toUpperCaseFirstOne(fieldName)) );
            Object value =  method.invoke(Object);
            //如果参数值类型为byte类型 这里需要处理下
            if(value instanceof  Byte){
                return value.toString();
            }else if(value instanceof  Integer){
                return value.toString();
            }
            return   (String) method.invoke(Object);
        }catch (Exception e){
            log.error(e.getMessage());
        }
       return "";
    }

}
