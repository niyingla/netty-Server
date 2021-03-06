package com.faceword.nio.mq.rabbit.sender;


import com.faceworld.base.mybatis.model.MansAlarmBlack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/11/20 20:19
 * @Version 1.0
 * 黑名单告警消息发送
 */
@Slf4j
@Component
public class AlarmBlackSender extends  AbstarctSender<MansAlarmBlack>{

    /**
     *  向消息队里中发送黑名单告警对象
     * @param mansAlarmBlack
     */
    public void senderMassage(MansAlarmBlack mansAlarmBlack)  {
        //id + 时间戳 全局唯一
        CorrelationData correlationData = new CorrelationData(System.nanoTime()+"" );
        try {
            rabbitTemplate.convertAndSend("alarm_black", "alarm.black", mansAlarmBlack, correlationData);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

}
