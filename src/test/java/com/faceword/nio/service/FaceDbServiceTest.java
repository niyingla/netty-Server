package com.faceword.nio.service;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.mybatis.model.FaceDbAllFace;
import com.faceword.nio.service.entity.FaceLibraryMessage;
import com.faceword.nio.service.entity.FaceLibraryMessageLicense;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @Author: zyong
 * @Date: 2018/11/29 19:57
 * @Version 1.0
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class FaceDbServiceTest {


    @Autowired
    FaceDbService faceDbService;


    @Test
    public void test1(){
        List<String> list = new ArrayList<>();
        list.add("FACEW-00001-00003-1");
        list.add("FACEW-00001-00003-1");
        faceDbService.findFaceLibraryList(list);
    }

    @Test
    public void findCameraAllFaceDbListTest(){

        log.info(JSON.toJSONString(faceDbService.findCameraAllFaceDbList("TTJL018605000322"),true));
    }

    @Test
    public void findFaceDbAllFaceListTest(){

        List<String> list = new ArrayList<>();
        list.add("6469103915685253120");
        FaceDbAllFace list2 = faceDbService.findFaceDbAllFaceList(list);
        log.info( JSON.toJSONString(list2,true));

    }

    @Test
    public void findFaceLibraryMessageLicenseList(){
        LongArrayList faceSet = new LongArrayList();
        faceSet.add(6469534442586247168L);
        faceSet.add(6469534694299013120L);
        faceSet.add(6469534829204606976L);
        List<FaceLibraryMessage> list =  faceDbService.findFaceLibraryMessageLicenseList(faceSet);
        log.info(JSON.toJSONString(list,true));
    }

}