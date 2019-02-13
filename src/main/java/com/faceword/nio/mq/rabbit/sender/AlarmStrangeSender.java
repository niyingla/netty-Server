package com.faceword.nio.mq.rabbit.sender;

import com.faceworld.base.mybatis.model.MansAlarmStrange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/11/20 20:24
 * @Version 1.0
 * 发送陌生人告警消息
 */
@Slf4j
@Component
public class AlarmStrangeSender extends  AbstarctSender<MansAlarmStrange>{

    /**
     *  向消息队里中发送陌生人告警对象
     * @param mansAlarmStrange
     */
    public void senderMassage(MansAlarmStrange mansAlarmStrange)  {

        //id + 时间戳 全局唯一
        CorrelationData correlationData = new CorrelationData(System.nanoTime()+"" );
        try {
            rabbitTemplate.convertAndSend("alarm_strange", "alarm.strange", mansAlarmStrange, correlationData);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
