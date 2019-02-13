package com.faceword.nio.controller;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.annotations.FaceDbUniformityConfirm;
import com.faceword.nio.business.entity.EquipmentReportInformation;
import com.faceword.nio.common.UploadBean;
import com.faceword.nio.enums.LogTypeEnum;
import com.faceword.nio.mq.rabbit.sender.RabbitSender;
import com.faceword.nio.service.entity.FaceLibraryMessage;
import com.faceword.nio.utils.IOUtil;
import com.faceword.nio.utils.SFtpUtils;
import com.faceworld.base.mq.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/10/31 15:47
 * @Version 1.0
 * 测试控制器
 */
@Slf4j
@RestController
@ApiIgnore
public class TestController {




    @Autowired
    private RabbitSender rabbitSender;

    @RequestMapping(value = "/test3")
    public void test4(@RequestBody EquipmentReportInformation reportInformation ){
        log.info( JSON.toJSONString(reportInformation ));
        String license = "RWXJ-0000001-MVKJH, JDUMFG";
       // equipmentSynchronizationService.processDeviceReportFaceInfo(reportInformation ,license ,null );
    }


    @RequestMapping("/send")
    public void send() throws Exception {
        Order order = new Order();
        order.setId("orderid");
        order.setName("orderName");
        rabbitSender.sendOrder(order);
    }

    @FaceDbUniformityConfirm(type = LogTypeEnum.FACE_ADD)
    public void testAopOrder(){

        System.out.println("--------testAopOrder----------");
    }

    @RequestMapping(value = "/test2")
    public Object test2() throws Exception {

        File file = new File("D:\\log\\netty_server.log");


        UploadBean bean = new UploadBean("netty_server.log", IOUtil.readFile(file));
        List<UploadBean> list = new ArrayList();
        list.add(bean);
        SFtpUtils.upload("/usr/local/slave_mysql", list);

        return "ok";
    }


}
