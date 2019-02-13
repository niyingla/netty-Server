package com.faceword.nio.utils;





import java.io.InputStream;
import java.util.List;
import com.faceword.nio.common.UploadBean;
import com.faceword.nio.config.SFtpProperties;
import com.faceworld.base.config.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import com.alibaba.fastjson.JSONArray;


/**
 * @Author: zyong
 * @Date: 2018/11/2 17:34
 * @Version 1.0
 */

public class SFtpUtils {

    private static Logger logger = LoggerFactory.getLogger(SFtpUtils.class);

    private final static SFtpProperties SFTP_PROPERTIES = ApplicationContextProvider.getBean(SFtpProperties.class);

    private static String address = SFTP_PROPERTIES.getAddress();
    private static int port = SFTP_PROPERTIES.getPort();
    private static String username = SFTP_PROPERTIES.getUserName();
    private static String password = SFTP_PROPERTIES.getPassword();


    private static int maxActive = 20;

    private static int minActive = 1;

    private static long timeout = 3 * 60 * 1000;

    // 当前活动数量
    private static volatile int currentActive = 0;

    private static volatile boolean isClose = false;


    private SFtpUtils() {
        // 这样就耦合度高,不过后续会进行解耦
		/*address = ftpProperties.getAddress();
		port = ftpProperties.getPort();
		username = ftpProperties.getUserName();
		password = ftpProperties.getPassword();
		rootpath = ftpProperties.getRootPath();
		filedomain = ftpProperties.getFileDomain();*/
    }


    private final static LinkedList<ChannelSftp> SFTP_LIST = new LinkedList<>();


    private final static Map<Integer, Long> TIMEOUT_MAP = new ConcurrentHashMap<Integer, Long>();


    private static Timer timer = new Timer();

    private static void closeChannelSftp() {
        if (isClose) {
            return;
        }
        isClose = true;

        if (SFTP_LIST.size() <= minActive) {
            isClose = false;
            return;
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 最小连接数量，取消定时任务
                if (SFTP_LIST.size() <= minActive) {
                    timer.cancel();
                    isClose = false;
                    return;
                }

                Iterator<Entry<Integer, Long>> iterator = TIMEOUT_MAP.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<Integer, Long> entry = iterator.next();
                    Long lastActiveTime = entry.getValue();

                    Long currentTime = System.currentTimeMillis();

                    // 还没到超时时间
                    if ((currentTime - lastActiveTime) < timeout) {
                        continue;
                    }

                    int id = entry.getKey();

                    // 连接超过超时时间了，可以回收了
                    Iterator<ChannelSftp> listIterator = SFTP_LIST.iterator();
                    while (listIterator.hasNext()) {
                        ChannelSftp s = listIterator.next();
                        if (s.getId() == id) {
                            s.disconnect();
                            s = null;
                            // 把连接超时的从集合删除
                            listIterator.remove();
                            iterator.remove();
                            break;
                        }
                    }
                }

            }
        }, timeout, timeout / 2);
    }

    public static boolean init(int count) {
        JSch jsch = new JSch();
        boolean b = false;
        for (int i = 0; i < count; i++) {
            try {
                Session sshSession = jsch.getSession(username, address, port);
                sshSession.setPassword(password);
                // sshSession.setConfig("userauth.gssapi-with-mic", "no");
                sshSession.setConfig("StrictHostKeyChecking", "no");
                sshSession.connect();

                Channel channel = sshSession.openChannel("sftp");
                channel.connect();

                ChannelSftp sftpChannel = (ChannelSftp) channel;

                sftpChannel.setFilenameEncoding("UTF-8");
                try {
                    logger.info("初始上传文件路径:{}", SFTP_PROPERTIES.getRootPath());
                    // 初始路径
                    sftpChannel.cd(SFTP_PROPERTIES.getRootPath());
                } catch (SftpException e) {
                    e.printStackTrace();
                }
                SFTP_LIST.add(sftpChannel);
                b = true;
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("初始化连接异常:{}", e);
                b = false;
            }
        }
        return b;
    }


    public static ChannelSftp getChannelSftp2() {
        if (currentActive >= maxActive) {
            // 连接使用达到最大数量,等待归还连接
            // 需要设置延时?
            return getChannelSftp2();
        }
        Object object = null;
        synchronized (SFTP_LIST) {

            // 连接池中可用数量
            int poolUseLength = SFTP_LIST.size();
            logger.info("当前可用连接数量:{},当前连接活动数量:{}", poolUseLength, currentActive);

            // 连接池没有可用连接，并且连接池没有满，则创建一个新连接
            ++currentActive;
            if (poolUseLength == 0 && currentActive < maxActive) {
                if (!init(1)) {
                    --currentActive;
                }
            }

            final ChannelSftp channelSftp = SFTP_LIST.removeFirst();

            // 如果连接关闭了
            if (channelSftp == null || channelSftp.isClosed()) {
                --currentActive;
                return getChannelSftp2();
            }


            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(channelSftp.getClass());
            enhancer.setClassLoader(channelSftp.getClass().getClassLoader());
            // 代理增强关闭连接功能
            enhancer.setCallback(new MethodInterceptor() {
                @Override
                public Object intercept(Object object, Method method, Object[] args, MethodProxy arg3)
                        throws Throwable {
                    if (method.getName().equals("disconnect")) {
                        // 设置回收时间
                        int id = channelSftp.getId();
                        TIMEOUT_MAP.put(id, System.currentTimeMillis());
                        closeChannelSftp();
                        synchronized (SFTP_LIST) {
                            // 路径恢复
                            channelSftp.cd(SFTP_PROPERTIES.getRootPath());
                            // 归还连接池
                            SFTP_LIST.add(channelSftp);
                            --currentActive;
                            return null;
                        }
                    }
                    // 传入的参数是object会造成死循环,还没搞懂为什么是传入原对象才能正常执行，并且是调用invoke方法。
                    return arg3.invoke(channelSftp, args);
                    // 调用invokeSuper会报错
                    // return arg3.invokeSuper(arg0, args);
                }
            });
            // 使用代理对象
            object = enhancer.create();
        }
        // 没有获得可用连接
        if (object == null) {
            // 重试获取连接
            getChannelSftp2();
        }

        return (ChannelSftp) object;
    }



    /**
     * 检查文件路径
     * @param sftp
     * @param uploadFolder
     * @return
     */
    public static boolean notExistOnMkdirFolder(ChannelSftp sftp, String uploadFolder) {
        try {
			/*String pwd = sftp.pwd();

			System.out.println(pwd + "---------------" + uploadFolder);

			if (StringUtils.contains(StringUtils.replace(pwd, "\\", "/"), uploadFolder)) {
				return true;
			}
			if (!SFTP_PROPERTIES.getRootPath().equals(pwd)) {
				sftp.cd(SFTP_PROPERTIES.getRootPath());
			}*/
            if (sftp.stat(uploadFolder) != null) {
                sftp.cd(uploadFolder);
                return true;
            }

        } catch (SftpException e) {
            String[] paths = uploadFolder.split("/");
            for (String path : paths) {
                if (!mkdir(path, sftp)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean mkdir(String path, ChannelSftp sftp) {
        try {
            //需要对空字符进行判断， 不然会有BUG
            if("".equals(path)){
                return true;
            }
            // sftp.stat(path);
            sftp.cd(path);
        } catch (SftpException e) {
            // 报错说明不存在
            try {
                //logger.info("mkdir path = {}" , path );
                if(!sftp.isConnected()){
                    logger.warn(" mkdir  sftp connected is close try get connect! ");
                    sftp = getChannelSftp2();
                }
                sftp.mkdir(path);
                sftp.cd(path);
            } catch (SftpException e1) {
                logger.info("path= {} , 目录新建失败,异常信息:{}", path, e1);
                return false;
            }
        }
        return true;
    }

    public static void upload(String uploadFolder, UploadBean uploadBean) {
        if (uploadBean == null ) {
            return;
        }
        long startTime = System.currentTimeMillis();
        ChannelSftp channelSftp = getChannelSftp2();
        // 检查文件目录和创建目录失败
        if (!notExistOnMkdirFolder(channelSftp, uploadFolder)) {
            return;
        }
        try {
                logger.info("开始上传文件,上传文件到:{}", channelSftp.pwd());
                //String filedomain = SFTP_PROPERTIES.getFileDomain();
                String newFileName = UUIDUtils.generetorUUID() + getSuffix(uploadBean.getOldName());
                channelSftp.put(IOUtil.byteToInputStream(uploadBean.getSource()), newFileName);
                uploadBean.setFolder(uploadFolder);
                //现在的业务不需要域名访问
               // uploadBean.setUrl((filedomain + "/" + uploadFolder + "/" + newFileName));
                uploadBean.setUrl( uploadFolder + "/" + newFileName);
                uploadBean.setNewName(newFileName);
        } catch (Exception e) {
            logger.error("上传文件异常:{}", e);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("上传文件耗时:{}ms", endTime - startTime);
            channelSftp.disconnect();
        }
    }


    public static void upload(String uploadFolder, List<UploadBean> uploadBeanList) {
        if (uploadBeanList == null || uploadBeanList.size() == 0) {
            return;
        }
        long startTime = System.currentTimeMillis();

        ChannelSftp channelSftp = getChannelSftp2();
        // 检查文件目录和创建目录失败
        if (!notExistOnMkdirFolder(channelSftp, uploadFolder)) {
            return;
        }
        try {
            logger.info("开始上传文件,上传文件到:{}", channelSftp.pwd());
            //String filedomain = SFTP_PROPERTIES.getFileDomain();
            for (UploadBean uploadBean : uploadBeanList) {

            String newFileName = UUIDUtils.generetorUUID() + getSuffix(uploadBean.getOldName());

            channelSftp.put(IOUtil.byteToInputStream(uploadBean.getSource()), newFileName);

            uploadBean.setFolder(uploadFolder);
            //现在的业务不需要域名访问
            //uploadBean.setUrl((filedomain + "/" + uploadFolder + "/" + newFileName));
             uploadBean.setUrl(( uploadFolder + "/" + newFileName));
             uploadBean.setNewName(newFileName);
             logger.info("bean ->{}" ,uploadBean);
        }

        } catch (Exception e) {
            logger.error("上传文件异常:{}", e);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("上传文件耗时:{}ms", endTime - startTime);
            channelSftp.disconnect();
        }
    }






    /**
     * 上传文件
     * @param uploadFolder
     * @param uploadFolder
     * @param files
     * @return
     */
    public static JSONArray upload(String uploadFolder, MultipartFile... files) {
        if (files == null || files.length == 0) {
            return null;
        }

        ChannelSftp channelSftp = getChannelSftp2();
        // 检查文件目录和创建目录失败
        if (!notExistOnMkdirFolder(channelSftp, uploadFolder)) {
            return null;
        }

        JSONArray array = new JSONArray();
        long startTime = System.currentTimeMillis();
        try {
            logger.info("开始上传文件,上传文件到:{}", channelSftp.pwd());

            String filedomain = SFTP_PROPERTIES.getFileDomain();
            for (MultipartFile f : files) {
                if (f == null) {
                    continue;
                }
                JSONObject jsonObject = new JSONObject();
                String fileName = f.getOriginalFilename();
                String newFileName = UUIDUtils.generetorUUID() + getSuffix(fileName);
                InputStream inputStream = f.getInputStream();
                channelSftp.put(inputStream, newFileName);
                jsonObject.put("oldFileName", fileName);
                jsonObject.put("newFileName", newFileName);
                jsonObject.put("fileSize", f.getSize());
                jsonObject.put("folder", uploadFolder);
                jsonObject.put("url", (filedomain + "/" + uploadFolder + "/" + fileName));
                array.add(jsonObject);
            }
        } catch (Exception e) {
            logger.error("上传文件异常:{}", e);
            return null;
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("上传文件耗时:{}ms", endTime - startTime);
            channelSftp.disconnect();
        }
        return array;

    }


    private static String getSuffix(String fileName) {

        return fileName.substring(fileName.lastIndexOf("."));

    }


}