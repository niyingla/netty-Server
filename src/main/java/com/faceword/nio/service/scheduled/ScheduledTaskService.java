package com.faceword.nio.service.scheduled;

import com.faceword.nio.business.NettyChannelMap;
import com.faceword.nio.business.entity.SocketChannelSign;
import com.faceword.nio.business.utils.ServerWriteUtils;
import com.faceword.nio.config.Constans;
import com.faceword.nio.enums.LicenseSignEnum;
import com.faceword.nio.mybatis.mapper.MansFaceLogCustomMapper;
import com.faceworld.base.mybatis.mapper.MansFaceLogMapper;
import com.faceworld.base.mybatis.model.MansFaceLogWithBLOBs;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Author: zyong
 * @Date: 2018/11/27 17:04
 * @Version 1.0
 */
@Slf4j
@EnableScheduling
@Service
@Transactional
public class ScheduledTaskService {


    @Autowired
    MansFaceLogCustomMapper mansFaceLogCustomDao;

    @Autowired
    MansFaceLogMapper mansFaceLogDao;

    /**
     * 固定时间执行（同步捷通通话详情） <br>
     * 说明：每天早上一点钟执行 <br>
     */
    @Scheduled(cron = "0 0 1 * * ?  ")
    //每5S同步一次 测试用
    //@Scheduled(cron = "0/5 * *  * * ?  ")
    public void executeSyncCallResult() {

        log.info("【开始统计】");
        Map<String , Map<String,SocketChannelSign>> channelLicenseMap = NettyChannelMap.getAllChannel();
        String license = "";
        SocketChannel socketChannel = null ;
        for (Map.Entry<String,  Map<String,SocketChannelSign>> entry : channelLicenseMap.entrySet()) {
            license = entry.getKey();

            for (Map.Entry <String,SocketChannelSign> entry2 : entry.getValue().entrySet()) {
                if( entry2.getValue().getSign() == LicenseSignEnum.NORMAL_LINK.getCode() ){
                    socketChannel = entry2.getValue().getSocketChannel();
                    //只向正常连接做补偿策略（其他的比如 断网重传，视频传入）
                    break;
                }
            }

            if( socketChannel == null ){
                log.warn(" socketChannel is null sync fail license = {}!!" ,license);
                continue;
            }
            List<MansFaceLogWithBLOBs> logWithBLOBsList=  mansFaceLogCustomDao.findListByLicense(license);
            for ( MansFaceLogWithBLOBs mansFaceLog : logWithBLOBsList){

                switch ( mansFaceLog.getOperType() ){
                    case 0 :
                        ServerWriteUtils.writeHeadAndBody( Constans.AB  , mansFaceLog.getParam(),socketChannel );
                        break;
                    case 1 :  ServerWriteUtils.writeHeadAndBody( Constans.AD  , mansFaceLog.getParam(),socketChannel );
                        break;
                    case 2 :  ServerWriteUtils.writeHeadAndBody( Constans.B1  , mansFaceLog.getParam(),socketChannel );
                        break;
                    default:
                }
                try {
                    Thread.sleep( 2000);
                }catch (Exception e){
                    log.warn( e.getMessage() );
                }

            }
        }
    }


}
