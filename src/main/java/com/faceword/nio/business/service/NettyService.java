package com.faceword.nio.business.service;

import com.faceword.nio.business.NettyChannelMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: zyong
 * @Date: 2018/12/10 16:50
 * @Version 1.0
 */
@Slf4j
@Service
public class NettyService {


    /**
     * 返回license
     * @param clientId
     * @return
     */
    public  String process(String clientId){

        log.info(" camera state is onLine !! ");

        return NettyChannelMap.getLicense(clientId);
    }
}
