package com.faceword.nio.dispatcher;



import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.handle.AbstractNioHandel;
import com.faceworld.base.config.ApplicationContextProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @Author: zyong
 * @Date: 2018/10/31 14:20
 * @Version 1.0
 *  Nio socket 消息 分发器
 */
@Slf4j
@Component
public class NioMessageDispatcher{


    /**
     * 分发给业务bean处理
     * @param receiveMessage
     * @param commandNumber
     * @throws Exception
     */
   public void dispatcher(ServerReceiveMessage receiveMessage , String commandNumber) {
       //直接从内存中获取扫描的class文件
       log.info("-------------dispatcher--------------");

       AbstractNioHandel excutor = (AbstractNioHandel) ApplicationContextProvider.getBean("handle".concat(commandNumber));
       if(excutor == null){
           log.warn("############【{}对应handel不存在】#############",commandNumber);
           return;
       }else{
           excutor.excute(receiveMessage);
       }

   }





}
