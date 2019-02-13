package com.faceword.nio;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.ai.arcsoft.ArcsoftOpenApi;
import com.faceword.nio.ai.arcsoft.entity.ArcsoftAuth;
import com.faceword.nio.business.entity.EquipmentReportInformation;
import com.faceword.nio.common.UploadBean;
import com.faceword.nio.redis.RedisConstans;
import com.faceword.nio.service.EquipmentSynchronizationService;
import com.faceword.nio.service.RedisCacheService;
import com.faceword.nio.service.entity.FaceLibraryMessage;
import com.faceword.nio.utils.IOUtil;
import com.faceword.nio.utils.SFtpUtils;
import com.faceworld.base.redis.RedisClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class NioApplicationTests {

    @Autowired
    private EquipmentSynchronizationService equipmentSynchronizationService;


    @Test
    public void test(){

        EquipmentReportInformation equipmentReportInformation = new EquipmentReportInformation();
        equipmentReportInformation.setPicData("data123456");
        equipmentReportInformation.setEcoderType((byte) 1);
        equipmentReportInformation.setLicense("aabbb");
        equipmentSynchronizationService.processDeviceReportFaceInfo2("123",equipmentReportInformation , null);
    }



    @Autowired
    private ArcsoftOpenApi arcsoftOpenApi;

    @Test
    public void testArcsoftAuth(){
        ArcsoftAuth arcsoftAuth =  arcsoftOpenApi.authorizations();
        arcsoftOpenApi.refreshAuthorizations(arcsoftAuth.getAccess_token());
    }

    @Test
    public void testFaceDetect(){

       // arcsoftOpenApi.faceDetect();
    }

    /**
     * PPCS-0000005-DFBSD 匹配的 license
     */
    @Test
    public void testFaceAdd(){
        String imgUrl = "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2579997133,793881475&fm=26&gp=0.jpg";
        arcsoftOpenApi.faceAdd(imgUrl,"1234567891234567891" , "1234567891234567891");
    }

    @Test
    public void testFaceInfo(){
        //String imgUrl = "http://47.98.253.106/aabbb/20181110/18/c6689d3be6b241d1bfa95b6508230ab9.png";
        arcsoftOpenApi.getFaceInfo("1234567891234567891" , "1234567891234567891");
    }



    @Test
    public void testFaceDelete(){

        arcsoftOpenApi.getFaceInfo("asd1as56d1" , "441654165");
        System.out.println( "--------------1---------------");
        arcsoftOpenApi.faceDelete("asd1as56d1","441654165");
        System.out.println( "--------------2---------------");
        arcsoftOpenApi.getFaceInfo("asd1as56d1" , "441654165");
    }

    @Test
    public void testFaceSearch(){

        String imgUrl = "http://47.98.253.106/aabbb/20181110/18/c6689d3be6b241d1bfa95b6508230ab9.png";
        //arcsoftOpenApi.faceAdd(imgUrl,"asd1as56d1" , "441654165");

        System.out.println("-------------以下为搜索内容-----------------");
       // arcsoftOpenApi.faceSearch(imgUrl,"441654165");
    }

    @Autowired
    private RedisCacheService redisCacheService;

    @Test
    public void testLicenseChache(){
        List<HashMap> val =  redisCacheService.getFaceInfoByLicense("PPCS-0000005-DFBSD");
        System.out.println("val==->" + JSON.toJSONString(val));
    }

    @Test
    public void testSftp(){

        File file = new File("D:\\中文.png");


        UploadBean bean = new UploadBean("netty_server.png", IOUtil.readFile(file));
        List<UploadBean> list = new ArrayList();
        list.add(bean);
        SFtpUtils.upload("/123", list);
    }


    @Test
    public void redisQuery(){


        RedisClientUtil.publisherQuery(RedisConstans.FACE_DATABASE_ADD_QUERY.concat("1"),"1");
    }

    @Test
    public void testDate(){


        FaceLibraryMessage faceLibraryMessage = new FaceLibraryMessage();

        faceLibraryMessage.setFaceBirth("1544754341453");

        System.out.println(JSON.toJSONString( faceLibraryMessage ,true));
    }
}
