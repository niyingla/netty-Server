package com.faceword.nio;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.mq.rabbit.sender.AlarmBlackSender;
import com.faceword.nio.mq.rabbit.sender.AlarmStrangeSender;
import com.faceword.nio.mq.rabbit.sender.AlarmWhiteSender;
import com.faceword.nio.mq.rabbit.sender.RabbitSender;
import com.faceworld.base.mq.entity.Order;
import com.faceworld.base.mybatis.model.MansAlarmBlack;
import com.faceworld.base.mybatis.model.MansAlarmStrange;
import com.faceworld.base.mybatis.model.MansAlarmWhite;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;


/**
 * @Author: zyong
 * @Date: 2018/11/20 14:40
 * @Version 1.0
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitMqTest  {


    @Autowired
    private RabbitSender rabbitSender;

    @Autowired
    AlarmWhiteSender alarmWhiteSender;

    @Autowired
    AlarmBlackSender alarmBlackSender;

    @Autowired
    AlarmStrangeSender alarmStrangeSender;

    /**
     * 测试连续发送消息的问题
     * @throws Exception
     */
    @Test
    public void send() throws Exception {
        Order order = new Order();
        order.setId("123123312321");
        rabbitSender.sendOrder(order);

        MansAlarmWhite mansAlarmWhite = new MansAlarmWhite();
        mansAlarmWhite.setwId(21312132321l);
        alarmWhiteSender.senderMassage(mansAlarmWhite);
    }


    @Test
    public void sendMq() throws  Exception{


        for (int i=0;i<1000000 ;i++){

            Thread.sleep(3000);
            String jsonStr = "{\"bId\":6465201039917322240+"+i+",\"faceId\":1234567891234567891,\"license\":\"TTJL01860500007\",\"bPictureType\":1,\"bFaceLen\":87332,\"bFacePath\":\"MEIY-B0F1ECF-B837D/20181123/16/fbf7eac3b3654dc980ad4e7893bed2b3.jpg\",\"bThreshold\":0,\"bSimilarPair\":100,\"bCreateTime\":\"2018-12-12\",\"age\":22,\"gender\":\"male\",\"dFlag\":0}\n";
            MansAlarmBlack mansAlarmBlack = JSON.parseObject(jsonStr , MansAlarmBlack.class);
            alarmBlackSender.senderMassage(mansAlarmBlack);

            Thread.sleep(3000);
            String jsonStr2 = "{\"wId\":6465201039917322240+"+i+",\"faceId\":1234567891234567891,\"license\":\"TTJL01860500007\",\"wPictureType\":1,\"wFaceLen\":87332,\"wFacePath\":\"MEIY-B0F1ECF-B837D/20181123/16/fbf7eac3b3654dc980ad4e7893bed2b3.jpg\",\"wThreshold\":0,\"wSimilarPair\":100,\"createTime\":\"2018-12-12\",\"age\":22,\"gender\":\"male\",\"dFlag\":0}\n";
            MansAlarmWhite mansAlarmWhite = JSON.parseObject(jsonStr2 , MansAlarmWhite.class);
            alarmWhiteSender.senderMassage(mansAlarmWhite);

            Thread.sleep(3000);
            String jsonStr3 = "{\"sId\":6465201039917322240+"+i+",\"faceId\":1234567891234567891,\"license\":\"TTJL01860500007\",\"sPictureType\":1,\"sFaceLen\":87332,\"sFacePath\":\"MEIY-B0F1ECF-B837D/20181123/16/fbf7eac3b3654dc980ad4e7893bed2b3.jpg\",\"sThreshold\":0,\"sSimilarPair\":100,\"createTime\":\"2018-12-12\",\"age\":22,\"gender\":\"male\",\"dFlag\":0}\n";
            MansAlarmStrange mansAlarmStrange  = JSON.parseObject(jsonStr3 , MansAlarmStrange.class);
            alarmStrangeSender.senderMassage(mansAlarmStrange);
        }


    }
}
