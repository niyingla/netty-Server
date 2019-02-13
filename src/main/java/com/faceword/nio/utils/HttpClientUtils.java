package com.faceword.nio.utils;


import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.ai.arcsoft.entity.FaceDetect;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @Author: zyong
 * @Date: 2018/11/12 17:24
 * @Version 1.0
 */
@Slf4j
public class HttpClientUtils {



    private static void httpParamInit(MultipartEntityBuilder  multipartEntityBuilder , Map <String,String> param){
        if(param != null){
            //如果存在参数 ,对参数进行动态封装
            Iterator<Map.Entry<String, String>> entries = param.entrySet().iterator();
            StringBody stringBody = null;
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                stringBody = new StringBody(entry.getValue(),ContentType.TEXT_PLAIN);
                multipartEntityBuilder.addPart(entry.getKey(),stringBody);
            }
        }
    }
    /**
     * 发送http请求
     * @param path
     * @param imageFilePath 可以传http图片路径 ，可以传文件流
     * @param headerName
     * @param headerValue
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static <T> T sendHttpRequest(String path, String imageFilePath , String headerName, String headerValue , Map <String,String> param , Class<T> resopnseType) {
        //将文件流进行打包
        ByteArrayBody byteArrayBody  = null;
        // 2. 将所有需要上传元素打包成HttpEntity对象
       MultipartEntityBuilder  multipartEntityBuilder =    MultipartEntityBuilder.create();
       if(imageFilePath!=null){
           byteArrayBody =  new ByteArrayBody( IOUtil.fileUrlToByte(imageFilePath), UUID.randomUUID().toString() );
           //如果存在文件
           multipartEntityBuilder.addPart("image_file",byteArrayBody);
       }
       //http 参数的初始化
        httpParamInit(multipartEntityBuilder,param);

        HttpEntity reqEntity = multipartEntityBuilder.build();
        log.info("打包数据完成");
        // 3. 创建HttpPost对象，用于包含信息发送post消息
        HttpPost httpPost = new HttpPost(path);
        httpPost.setEntity(reqEntity);
        httpPost.setHeader(headerName,headerValue);
        log.info("创建post请求并装载好打包数据");
        // 4. 创建HttpClient对象，传入httpPost执行发送网络请求的动作
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        T t = null;
        try {
            //执行调用过程
             response = httpClient.execute(httpPost);
            log.info("----发送post请求并获取结果----");
            HttpEntity resultEntity = response.getEntity();
            t = getResponceContent(resultEntity, resopnseType);
        }catch (ClientProtocolException e){
            log.info(e.getMessage());
        }catch (IOException e2){
            log.info(e2.getMessage());
        }finally {
            close(response,httpClient);
        }
        return t;
    }

    /**
     * 发送http请求
     * @param path
     * @param imageFileBytes 文件二进制数组
     * @param headerName
     * @param headerValue
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static <T> T sendHttpRequest(String path, byte[] imageFileBytes , String headerName, String headerValue , Map <String,String> param , Class<T> resopnseType) {
        //将文件流进行打包
        ByteArrayBody byteArrayBody  = null;
        // 2. 将所有需要上传元素打包成HttpEntity对象
        MultipartEntityBuilder  multipartEntityBuilder =    MultipartEntityBuilder.create();
        if(imageFileBytes!=null){
            byteArrayBody =  new ByteArrayBody(imageFileBytes, UUID.randomUUID().toString());
            //如果存在文件
            multipartEntityBuilder.addPart("image_file",byteArrayBody);
        }
        //http 参数的初始化
        httpParamInit(multipartEntityBuilder,param);

        HttpEntity reqEntity = multipartEntityBuilder.build();
        // 3. 创建HttpPost对象，用于包含信息发送post消息
        HttpPost httpPost = new HttpPost(path);
        httpPost.setEntity(reqEntity);
        httpPost.setHeader(headerName,headerValue);

        // 4. 创建HttpClient对象，传入httpPost执行发送网络请求的动作
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        T t = null;
        try {
            //执行调用过程
            response = httpClient.execute(httpPost);
            log.info("-----httpClient 发送post请求并获取结果--------------");
            HttpEntity resultEntity = response.getEntity();

           t =  getResponceContent(resultEntity , resopnseType);
        }catch (ClientProtocolException e){
            log.info(e.getMessage());
        }catch (IOException e2){
            log.info(e2.getMessage());
        }finally {
            close(response,httpClient);
        }
        return t;
    }

    /**
     *  关闭连接释放资源
     * @param response
     * @param httpClient
     */
    private static  void close( CloseableHttpResponse response , CloseableHttpClient httpClient ){

        try {
            if(response != null){
                response.close();
            }
            if(httpClient!=null){
                httpClient.close();
            }
        }catch (IOException e){
            log.info(e.getMessage());
        }
    }

    /**
     * 对调用结果进行解析
     * @param resultEntity
     * @return
     */
    private static <T> T getResponceContent(HttpEntity resultEntity ,Class<T> responseType){
        String responseMessage = "";
        try{
            log.info("开始解析结果");
            if(resultEntity!=null){
                InputStream is = resultEntity.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while((line = br.readLine()) != null){
                    sb.append(line);
                }
                responseMessage = sb.toString();
                log.info("解析完成，解析内容为->{}",responseMessage);
            }
            //类似于释放资源
            EntityUtils.consume(resultEntity);
        }catch (IOException e){
            log.info(e.getMessage());
        }
        return JSON.parseObject(responseMessage ,responseType );
    }

    public static void main(String[] args)  {
        HttpClientUtils ht = new HttpClientUtils();
        String url = "https://api.cvmart.net/api/face/detect";
        String headerValue = "BearereyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2FwaS5jdm1hcnQubmV0L2FwaS9hdXRob3JpemF0aW9ucyIsImlhdCI6MTU0MjAxNTMzNiwiZXhwIjoxNTQyMDIyNTM2LCJuYmYiOjE1NDIwMTUzMzYsImp0aSI6Ik5SN0dHM2tDTWtlZVpEOHUiLCJzdWIiOjE1MzIsInBydiI6ImY2YjcxNTQ5ZGI4YzJjNDJiNzU4MjdhYTQ0ZjAyYjdlZTUyOWQyNGQifQ.j0t72Y5cL5nwyGak4HDjLNqV9u0X3RqzbWWW5vBmwgg";
        String filePath = "http://47.98.253.106/aabbb/20181110/18/c6689d3be6b241d1bfa95b6508230ab9.png";
        FaceDetect faceDetect = ht.sendHttpRequest(url,filePath ,"Authorization",headerValue,null , FaceDetect.class);

        System.out.println(JSON.toJSONString(faceDetect));
    }


}
