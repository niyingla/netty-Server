package com.faceword.nio.business;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.business.entity.License;
import com.faceword.nio.business.entity.LocalBuff;
import com.faceword.nio.business.utils.NettyServerUtils;
import com.faceword.nio.business.utils.ServerWriteUtils;
import com.faceword.nio.config.Constans;
import com.faceword.nio.dispatcher.NioMessageDispatcher;
import com.faceword.nio.enums.CameraTypeEnum;
import com.faceword.nio.enums.LicenseSignEnum;
import com.faceword.nio.redis.RedisConstans;
import com.faceword.nio.utils.ByteBufUtils;
import com.faceworld.base.redis.RedisClientUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @Author: zyong
 * @Date: 2018/11/6 14:20
 * @Version 1.0
 *  服务接收信息处理类
 */
@Slf4j
public class NettyServerProcess {



    /**
     * 存在的mode
     */
    private static List<Short> modeList ;

    static {
        /**
         * 服务器接收的命令包括 ， 其余的命令都不接收
         */
        Short [] shorts = {161 , 163 , 166, 179 , 167 , 170,
                172 ,174,178 , 225 , 227 ,
                Constans.D3 ,Constans.D5 ,Constans.D6,Constans.D7,Constans.D8 };
        modeList = Arrays.asList(shorts);
    }


    public  static boolean  modeCheck(Short mode){
        if(modeList.contains(mode)){
            return true;
        }
        return false;
    }

    /**
     * 断开与服务器的链接 , 以及一些资源的释放
     */
    public static void closeCtx( ChannelHandlerContext ctx){
        ctx.close();
        String clientId = NettyServerUtils.getClientId(ctx);
        closeRedisData(clientId);
        NettyChannelMap.removeLicenseAllConnect(clientId);
    }


    public static void closeRedisData(String clientId){
        String license = NettyChannelMap.getLicense(clientId);

        if(StringUtils.isBlank(license)){
            return;
        }

        /***将license对应的redis资源释放*/
        String addKey = RedisConstans.FACE_DATABASE_ADD_QUERY.concat(license);
        String deleteAllKey = RedisConstans.FACE_DELETE_ALL_QUERY.concat(license);
        String crtKey = RedisConstans.FACE_VERSION_CONTRACT.concat(license);

        String vupKey = RedisConstans.FACE_VERSION_UP.concat(license);

        Set<String> keys = new HashSet<>();
        keys.add(addKey);
        keys.add(deleteAllKey);
        keys.add(crtKey);
        keys.add(vupKey);
        RedisClientUtil.delete(keys);

        log.info("断开链接，对应的redis缓存数据已经释放！");

    }

    /**
     * 只关闭当前对应的链接
     * @param ctx
     */
    public static void closeCurrentCtx( ChannelHandlerContext ctx){
        ctx.close();
        String clientId = NettyServerUtils.getClientId(ctx);
        closeRedisData(clientId);
        NettyChannelMap.remove(clientId);

    }


    /**
     * 在读取数据包的时候对象初始化过程
     * @param byteBuf
     * @param byteBuf
     * @return
     */
    public static LocalBuff readInit(ByteBuf byteBuf){
        //定义对象接收包头信息  在读的过程中，readIndex的指针也在移动
        LocalBuff localBuff = new LocalBuff();
        localBuff.setBuff(new StringBuilder());

        ServerReceiveMessage serverReceiveMessage = new ServerReceiveMessage();
        localBuff.setServerReceiveMessage(serverReceiveMessage);
        serverReceiveMessage.setMark(byteBuf.readShort());
        serverReceiveMessage.setVer(byteBuf.readShort());
        serverReceiveMessage.setDevType(byteBuf.readShort());
        serverReceiveMessage.setMode(byteBuf.readShort());
        serverReceiveMessage.setSerial( byteBuf.readInt());
        serverReceiveMessage.setLength(byteBuf.readInt());
        localBuff.setLen(serverReceiveMessage.getLength());


        return localBuff;
    }


    /**
     * 多线程线程池处理业务
     * @param receiveMessage
     */
    public static void process(final ExecutorService threadPool ,
                         final NioMessageDispatcher nioMessageDispatcher,
                         final ServerReceiveMessage receiveMessage,
                         final  String license){
        threadPool.execute(() -> {
            //使用线程池去进行业务保存 ， 可以不能暂用netty线程池的调度
            try {
                log.info("->process read license = ：{} , model = {}" , license ,receiveMessage.getMode() );
                //将license添加到对象中
                receiveMessage.setLicense(license);
                //使用自定义的去完成转发
                nioMessageDispatcher.dispatcher( receiveMessage ,String.valueOf(receiveMessage.getMode()) );
            }catch (Exception e){
                log.error("nio转发业务处理出现异常——>{}" , e.getMessage() );
            }
        });
    }

    /**
     * 客户端往服务端上报信息
     */
    public static License reportInformation(ByteBuf byteBuf , int readLen){
        //可以一次性读完
        String decodeLicense  = ByteBufUtils.readChannelMessage( byteBuf , readLen);
        String licenseJson = NettyServerUtils.decodeLicense(decodeLicense);
        License license = JSON.parseObject(licenseJson , License.class);
        String licenseServiceId = NettyServerUtils.getServiceLicenseId(license.getLicense());
        log.info("register licenseServiceId - > {} ,cameraType -> {}" , licenseServiceId , license.getDevType());
        return license;
    }


    /**
     * 服务向设备请求同步
     */
    public static void synchronousRequest(String license){
        SocketChannel socketChannel =  NettyChannelMap.getSocketChannel(license , LicenseSignEnum.NORMAL_LINK.getCode() );
        if(socketChannel == null){
            log.warn( "设备上报【失败】，socketChannel is null");
        }else{

            Integer cameraType = NettyChannelMap.getCameraTypeByLicense(license);
            if( cameraType == CameraTypeEnum.INTEGRATE_CAMERA.getCode()){

                ServerWriteUtils.writeHead( Constans.D1 ,socketChannel);
                log.info(" 设备上报【成功】 ,服务器请求设备发起同步请求");
                //开启版本同步缓存
                RedisClientUtil.delete(RedisConstans.FACE_VERSION_CONTRACT.concat(license));
                RedisClientUtil.increment(RedisConstans.FACE_VERSION_CONTRACT.concat(license));
            }

        }

    }


}
