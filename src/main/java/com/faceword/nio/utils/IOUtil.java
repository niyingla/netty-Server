package com.faceword.nio.utils;



import com.alibaba.fastjson.JSON;
import com.faceword.nio.service.entity.DeleteFaceMessage;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: zyong
 * @Date: 2018/11/2 17:39
 * @Version 1.0
 */
@Slf4j
public class IOUtil {

    /**
     * 文件转byte数组
     * @param file
     * @return
     */
    public static byte[] readFile(File file) {
        byte[] buffer = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bos != null)
                    bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return buffer;
    }


    /**
     * 输入流转换成byte数组
     * @param inputStream
     * @return
     */
    public static byte[] inputStreamToByte(InputStream inputStream) {
        ByteArrayOutputStream bos = null;
        byte[] buffer = null;
        try {
            bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = inputStream.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            buffer = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bos != null)
                    bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer;
    }

    /**
     * 获取文件输入流
     * @param file
     * @return
     */
    public static InputStream tileToInpuStream(File file) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputStream;
    }


    public static InputStream byteToInputStream(byte[] buf) {
        InputStream inputStream = new ByteArrayInputStream(buf);
        return inputStream;
    }

    public static void byteToFile(byte[] bytes, String path)
    {
        try
        {
            // 根据绝对路径初始化文件
            File localFile = new File(path);
            if (!localFile.exists())
            {
                localFile.createNewFile();
            }
            // 输出流
            OutputStream os = new FileOutputStream(localFile);
            os.write(bytes);
            os.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * 通过http 请求下载图片
     * 完整的路径实例
     * http://www.baidu.com/img/bd_logo1.png
     * @param destUrl
     */
    public static  void saveToFile(String destUrl) {
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        HttpURLConnection httpUrl = null;
        URL url = null;
        int BUFFER_SIZE = 1024;
        byte[] buf = new byte[BUFFER_SIZE];
        int size = 0;
        try {
            url = new URL(destUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            fos = new FileOutputStream("d://baidu.png");
            while ((size = bis.read(buf)) != -1) {
                fos.write(buf, 0, size);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
        } finally {
            try {
                fos.close();
                bis.close();
                httpUrl.disconnect();
            } catch (IOException e) {
            } catch (NullPointerException e) {
            }
        }
    }


    /**
     * 通过http 请求下载图片
     * 完整的路径实例
     * http://www.baidu.com/img/bd_logo1.png
     * @param destUrl
     */
    public static  byte[] fileUrlToByte(String destUrl) {

        BufferedInputStream bis = null;
        HttpURLConnection httpUrl = null;
        URL url = null;
        ByteArrayOutputStream baos ;
        try {
            url = new URL(destUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            baos = new ByteArrayOutputStream();
            int sbyte=bis.read();//读取bis流中的下一个字节
            while( sbyte!=-1 ){
                baos.write(sbyte);
                sbyte=bis.read();
            }
            return baos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (ClassCastException e) {
            log.error(e.getMessage());
        } finally {
            try {
                bis.close();
                bis.close();
                httpUrl.disconnect();
            } catch (IOException e) {
                log.info(e.getMessage());
            } catch (NullPointerException e) {
                log.info(e.getMessage());
            }
        }
        return null;
    }

    //www.baidu.com/img/bd_logo1.png
    public static void main(String[] args) {

        long startTime = System.nanoTime();

        long endTime = System.nanoTime();

        log.info("人脸库对比 花费时间为：{}ms" , (endTime-startTime) );
    }
}
