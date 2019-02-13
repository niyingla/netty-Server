package com.faceword.nio;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.redis.RedisConstans;
import com.faceworld.base.redis.RedisClientUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: zyong
 * @Date: 2018/12/6 13:44
 * @Version 1.0
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {



    @Test
    public void intersectSet(){

        List set1 = new ArrayList();

        List<Integer> set2 = new ArrayList();
        for (int i=0;i<=10000;i++){
            set1.add(i);
            set2.add(i);
        }
        set1.set(600,2134456);
        set1.set(700,2132456);

        set2.set(9000,1561518);
        set2.set(8888,5145818);

        RedisClientUtil.setPipelineCollectionSet("set1",set1);

        RedisClientUtil.setPipelineCollectionSet("set2",set2);
    }

    @Test
    public void diffList(){
        List set1 = new IntArrayList();


        List<Integer> set2 = new IntArrayList();
        for (int i=0;i<=100000;i++){
            set1.add(i);
            set2.add(i);
        }
        set1.set(600,2134456);
        set1.set(700,2132456);

        set2.set(9000,1561518);
        set2.set(8888,5145818);
        long startTime = System.currentTimeMillis();

        set1.removeAll(set2);
        long endTime = System.currentTimeMillis();
        log.info("Pipelined 花费时间为：{}ms" , (endTime-startTime) );

        log.info("value = {}" ,set1 );
    }

    @Test
    public void intersectSetTest(){

        Set<Integer > rs = RedisClientUtil.intersectSet("set1","set2");
        log.info(JSON.toJSONString(rs,true));
    }

    @Test
    public void unionSetTest(){

        Set<Integer > rs = RedisClientUtil.unionSet("set1","set2");
        log.info(JSON.toJSONString(rs,true));
    }

    @Test
    public void differenceSetTest(){
        long startTime = System.currentTimeMillis();
        Set<Integer > rs = RedisClientUtil.differenceSet("set2","set1");
        log.info(JSON.toJSONString(rs,true));
        long endTime = System.currentTimeMillis();
        log.info("Pipelined 花费时间为：{}ms" , (endTime-startTime) );
    }

    @Test
    public void increment(){

        Long l =  RedisClientUtil.increment("asddsd");
        System.out.println(l);
    }

    @Test
    public void test2(){
        Long l =  RedisClientUtil.querySize(  RedisConstans.FACE_DELETE_QUERY.concat("123") );

        System.out.println("l==="+l);
    }

    @Test
    public void testDelete(){

        Set<String> keys = RedisClientUtil.keys("face:*");
        System.out.println("keys={}"+ JSON.toJSONString(keys));
         RedisClientUtil.delete(keys);
    }

    @Test
    public void testLongType(){
        String license = "MEIY-10D07AA-9A468";
        RedisClientUtil.increment(RedisConstans.FACE_VERSION_CONTRACT.concat(license));
        Integer synchronizationCnt = RedisClientUtil.get(RedisConstans.FACE_VERSION_CONTRACT.concat(license));
        log.info("redis key = {} , synchronizationCnt = {}" , RedisConstans.FACE_VERSION_CONTRACT.concat(license) ,synchronizationCnt );
    }
}
