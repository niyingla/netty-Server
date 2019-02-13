package com.faceword.nio.exception;

import com.faceword.nio.utils.CallBackMsgUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.net.BindException;

/**
 * @Author: zyong
 * @Date: 2018/11/26 10:36
 * @Version 1.0
 *  web http 全局异常处理
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler  extends  RuntimeException {

    private static final long serialVersionUID = 1L;

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object baseErrorHandler(HttpServletRequest req, Exception e) {
        log.error("***********************************异常详细信息开始******************************************");
        log.error("---baseErrorHandler Handler---Host {} ",req.getRemoteHost() );
        log.error("---baseErrorHandler invokes---url {}",req.getRequestURL() );
        log.error("错误信息:{}",e);
        log.error("***********************************异常详细信息结束***************************************");
        return CallBackMsgUtils.exception();
    }
    @ExceptionHandler({
            MaxUploadSizeExceededException.class,
            BindException.class,
            MethodArgumentConversionNotSupportedException.class,
            ConstraintViolationException.class,
            ValidationException.class })
    @ResponseBody
    public Object defaultErrorHandler(HttpServletRequest req, Exception e)  {
        log.error("***********************************异常详细信息开始******************************************");
        log.error("---baseErrorHandler Handler---Host {} ",req.getRemoteHost() );
        log.error("---baseErrorHandler invokes---url {}",req.getRequestURL() );
        log.error("---baseErrorHandler ERROR---message: {}",e.toString() );
        log.error("*********************---baseErrorHandler ERROR---localtion---*********************");
        log.error("exception detail message -> {}" , e.getMessage());
        log.error("***********************************异常详细信息结束***************************************");
        return CallBackMsgUtils.exception();
    }
}
