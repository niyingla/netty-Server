package com.faceword.nio.controller;

import com.faceword.nio.common.DistributionControlVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/11/30 9:56
 * @Version 1.0
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class NioServerControllerTest {

    @Autowired
    NioServerController nioServerController;


    @Autowired
    TestController testController;

    @Test
    public void test2(){
        testController.testAopOrder();
    }

    @Test
    public void test(){
        DistributionControlVo distributionControlVo
                = new DistributionControlVo();
        distributionControlVo.setLicense("123");
        distributionControlVo.setCameraType(1);
        List list = new ArrayList();
        list.add("FACEW-00001-00003-1");
        distributionControlVo.setDbList(list);
        nioServerController.distributionControl(distributionControlVo);

    }

}