package com.faceword.nio.utils;


import com.faceworld.base.config.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson.JSON;

/**
 * @Author: zyong
 * @Date: 2018/11/10 15:56
 * @Version 1.0
 *  http request client
 */
public class RestTemplateUtils {

    private static Logger logger = LoggerFactory.getLogger(RestTemplateUtils.class);

    private static RestTemplate restTemplate;

    /**
     * 获取RestTemplate对象
     * @return
     */
    public static RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = ApplicationContextProvider.getBean(RestTemplate.class);
        }
        return restTemplate;
    }

    /**
     * 发送post请求
     * @param url:请求地址
     * @param jsonObject:请求参数
     * @param responseType:返回类型
     * @return
     */
    public static <T> T doPostForJSON_UTF8(String url, Object jsonObject, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        // headers.add("Accept", headerValue);

        String body = null;
        if (jsonObject != null) {
            if (jsonObject instanceof String) {
                body = jsonObject.toString();
            } else {
                body = JSON.toJSONString(jsonObject);
            }
        }
        HttpEntity<String> httpEntity = new HttpEntity<String>(body, headers);
        ResponseEntity<T> responseEntity = null;
        try {
            logger.info("开始调用接口,接口地址:{},请求参数:{}", url, body);
            responseEntity = getRestTemplate().postForEntity(url, httpEntity, responseType);
        } catch (RestClientException e) {
            logger.error("接口调用出错:{}", e);
        }
        if (responseEntity == null) {
            return null;
        }
        return responseEntity.getBody();
    }

    /**
     * 发送post请求
     * @param url:请求地址
     * @param multiValueMap:请求参数
     * @param responseType:返回类型
     * @return
     */
    public static <T> T doPostHeaderForJSON_UTF8(String url, String header , MultiValueMap<String, Object> multiValueMap, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType( MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization", header);

        String body = null;

        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(multiValueMap, headers);

        ResponseEntity<T> responseEntity = null;
        try {

            logger.info("开始调用接口,接口地址:{},请求参数:{}", url, body);
            responseEntity = getRestTemplate().exchange(url, HttpMethod.POST, httpEntity, responseType);
        } catch (RestClientException e) {
            logger.error("接口调用出错:{}", e);
        }
        if (responseEntity == null) {
            return null;
        }
        return responseEntity.getBody();
    }

    /**
     * 发送 put请求
     * @param url:请求地址
     * @param responseType:返回类型
     * @return
     */
    public static <T> T doPutAuthForJSON_UTF8(String url,String headAuth, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("Authorization", headAuth);

        HttpEntity<String> httpEntity = new HttpEntity<String>( headers);

        ResponseEntity<T> responseEntity = null;
        try {

            logger.info("开始调用接口,接口地址:{},请求参数:{}", url, headAuth);
            responseEntity = getRestTemplate().exchange(url, HttpMethod.PUT,httpEntity,responseType);
        } catch (RestClientException e) {
            logger.error("接口调用出错:{}", e);
        }
        if (responseEntity == null) {
            return null;
        }
        return responseEntity.getBody();
    }


    /**
     * 发送post请求
     * @param url
     * @param jsonObject:请求参数
     * @return
     */
    public static String doPostForJSON_UTF8(String url, Object jsonObject) {
        return doPostForJSON_UTF8(url, jsonObject, String.class);
    }

    /**
     * 发送post请求
     * @param url
     * @return
     */
    public static String doPostForJSON_UTF8(String url) {
        return doPostForJSON_UTF8(url, null, String.class);
    }


    /**
     * 发送post请求
     * @param url
     * @param responseType:返回类型
     * @return
     */
    public static <T> T doPostForJSON_UTF8(String url, Class<T> responseType) {
        return doPostForJSON_UTF8(url, null, responseType);
    }

}

