package com.faceword.nio.mq.rabbit.sender;



import com.faceworld.base.mq.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @Author: zyong
 * @Date: 2018/11/20 11:52
 * @Version 1.0
 * rabbitmq 配置类
 */
@Slf4j
@Component
public class RabbitSender  {

    //自动注入RabbitTemplate模板类
    @Autowired
    private RabbitTemplate rabbitTemplate;



//    //发送消息方法调用: 构建Message消息
//    public void send(Object message, Map<String, Object> properties) throws Exception {
//        //springframework的类放置消息属性
//        MessageHeaders mhs = new MessageHeaders(properties);
//        //创建消息 传入object的消息内容 和 消息属性
//        org.springframework.messaging.Message msg = MessageBuilder.createMessage(message, mhs);
//        //设置去确认消息发送类
//        rabbitTemplate.setConfirmCallback(confirmCallback);
//        //设置消息不可达处理类
//        rabbitTemplate.setReturnCallback(returnCallback);
//        //id + 时间戳 全局唯一
//        CorrelationData correlationData = new CorrelationData("12345678903");
//        rabbitTemplate.convertAndSend("exchange-1", "springboot.abc", msg, correlationData);
//    }

    //发送消息方法调用: 构建自定义对象消息
    public void sendOrder(Order order) throws Exception {

        //id + 时间戳 全局唯一
        CorrelationData correlationData = new CorrelationData("09876543212");

        rabbitTemplate.convertAndSend("exchange-2", "springboot.def", order, correlationData);
    }


}
