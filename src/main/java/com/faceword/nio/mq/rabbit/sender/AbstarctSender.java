package com.faceword.nio.mq.rabbit.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: zyong
 * @Date: 2018/11/20 20:18
 * @Version 1.0
 * 抽象的发送者
 */
@Slf4j
public abstract class AbstarctSender<T> {

    //自动注入RabbitTemplate模板类
    @Autowired
    protected RabbitTemplate rabbitTemplate;


    /**
     * 对发送消息进行抽象
     */
    public abstract void senderMassage(T t);
}
