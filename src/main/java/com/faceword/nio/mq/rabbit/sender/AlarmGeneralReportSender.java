package com.faceword.nio.mq.rabbit.sender;



import com.faceworld.base.mybatis.model.MansReportFaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Component;

/**
 * @Author: zyong
 * @Date: 2018/11/20 20:19
 * @Version 1.0
 *  普通抓拍
 */
@Slf4j
@Component
public class AlarmGeneralReportSender extends  AbstarctSender<MansReportFaceInfo>{

    /**
     *  向消息队里中发送黑名单告警对象
     * @param mansReportFaceInfo
     */
    public void senderMassage(MansReportFaceInfo mansReportFaceInfo)  {
        //id + 时间戳 全局唯一
        CorrelationData correlationData = new CorrelationData(System.nanoTime()+"" );
        try {
            rabbitTemplate.convertAndSend("general_report", "alarm.report", mansReportFaceInfo, correlationData);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

}
