package com.faceword.nio.business;

/**
 * @Author: zyong
 * @Date: 2018/11/1 10:43
 * @Version 1.0
 */
import com.faceword.nio.business.entity.SocketChannelSign;
import com.faceword.nio.enums.LicenseSignEnum;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class NettyChannelMap {

    /**
     * 维护license与channel 对应关系
     */
    private static Map<String , Map<String,SocketChannelSign>> currentMap = new ConcurrentHashMap<>();

    /**
     * 维护与client license 对应关系
     */
    private static Map<String , String> clientMap =  new ConcurrentHashMap<>();

    /**
     * 采用公平竞争策略
     */
    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    /**
     * 添加链接信息
     * @param clientId
     * @param channel
     */
    public static void add(String clientId ,SocketChannel channel ,String license,Integer sign,Integer cameraType){

        if( StringUtils.isBlank(clientId) || channel==null || StringUtils.isBlank(license)){
            log.warn("clientId is = {} , channel is = {} ,license is ={} add to map is fail" , clientId,channel,license);
            return ;
        }
        try {
            readWriteLock.writeLock().lock();
            Map<String,SocketChannelSign> map = currentMap.get( license );
            SocketChannelSign socketChannelSign = new SocketChannelSign( channel , sign ,cameraType);

            clientMap.put( clientId,license );
            if( map == null){
                map  = new ConcurrentHashMap<>();
                map.put( clientId ,  socketChannelSign );
                currentMap.put( license , map  );

            }else{
                map.put( clientId, socketChannelSign );
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }


    /**
     * 检查当前的链接是否是正常的链接
     * @param clientId
     * @return
     */
    public  static boolean isNormalLink(String clientId){
        if(StringUtils.isBlank(clientId)){
            log.warn("clientId is null !");
            return false;
        }
        try {
            String license = clientMap.get(clientId);
            if( StringUtils.isBlank(license)){
                log.warn("license is null !");
                return false;
            }
            Map<String,SocketChannelSign> map = currentMap.get( license );
            if( map != null ){
                for ( Map.Entry<String, SocketChannelSign> entry : map.entrySet() ) {
                    if(entry.getKey().equals(clientId)){
                        if( entry.getValue().getSign() == LicenseSignEnum.NORMAL_LINK.getCode() ){
                            //当前连接是正常链接
                            return true;
                        }
                        return false;
                    }
                }
            }else{
                log.info(" clientId {} mapping license map is null " ,clientId );
            }
            return false;
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * 获取链接信息
     * @param clientId
     * @return Channel
     */
    public static Channel getChannel(String clientId){
        try {
            readWriteLock.readLock().lock();
            String license = clientMap.get(clientId);
            Map<String,SocketChannelSign> map = currentMap.get( license );
            if( map != null ){
                for ( Map.Entry<String, SocketChannelSign> entry : map.entrySet() ) {
                    if(entry.getKey().equals(clientId)){
                        return entry.getValue().getSocketChannel();
                    }
                }
            }else{
                log.info(" clientId {} mapping license map is null " ,clientId );
            }
        }catch (Exception e){
            log.info(e.getMessage());
        }finally {
            readWriteLock.readLock().unlock();
        }
        return null;
    }


    /**
     * 获取链接的License
     * @param clientId
     * @return
     */
    public static String getLicense(String clientId){
        if( StringUtils.isBlank(clientId) ){
            log.warn(" license is null !!! ");
            return "";
        }
        return clientMap.get(clientId);
    }

    /**
     * 主链接断开，移出所有链接
     * @param license
     */
    public static void removeLicenseAllConnect(String license){
        if( StringUtils.isBlank(license) ){
            log.warn(" license is null !!! ");
            return ;
        }
        try {
            readWriteLock.writeLock().lock();
            Map<String,SocketChannelSign> map = currentMap.get( license );
            if( map != null ){
                for ( Map.Entry<String, SocketChannelSign> entry : map.entrySet() ) {
                    clientMap.remove( entry.getKey() );
                }
                currentMap.remove(license);
                log.info("remove license mapping all connect!!");
            }else{
                log.info(" license {} mapping license map is null " ,license );
            }
        }catch (Exception e){
            log.error( e.getMessage() );
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * 通过id去移出链接信息
     * @param clientId
     */
    public static void remove(String clientId){

        if( StringUtils.isBlank(clientId)){
            log.warn("clientId is not null!!");
            return ;
        }
        try {
            readWriteLock.writeLock().lock();
            String license = clientMap.get(clientId);

            if(StringUtils.isBlank(license)){
                log.warn("license is not null!!");
                return ;
            }
            log.info(" remove license is {}  , clientID = {}" , license , clientId );
            Map<String,SocketChannelSign> map = currentMap.get( license );
            if( map != null ){
                for ( Map.Entry<String, SocketChannelSign> entry : map.entrySet() ) {
                    if(entry.getKey().equals(clientId)){
                        log.info("remove connect map info clientId = {}" , clientId);
                        map.remove(clientId);
                        clientMap.remove(clientId);
                    }
                }
                //整个license 对应的链接断了
                if( map.size() == 0){
                    currentMap.remove(license);
                }
            }else{
                log.info(" clientId {} mapping license map is null " ,clientId );
            }
        }catch (Exception e){
            log.error( "message:" ,e );
        }finally {
            readWriteLock.writeLock().unlock();
        }

    }

    /**
     * 检查当前的license 是否在线
     * @return
     */
    public static boolean isLicenseOnline(String licenseId){

        if( StringUtils.isBlank(licenseId) ){
            log.warn( "licenseId is null" );
            return false;
        }
        try {
            readWriteLock.readLock().lock();
            Map<String,SocketChannelSign> map = currentMap.get(licenseId);
            if( map == null ){
                return false;
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            readWriteLock.readLock().unlock();
        }
        return true;
    }

    /**
     * 通过license 查 channel
     * @param license
     * @return
     */
    public static Channel getChannelByLicense(String license , Integer sign){

        if( StringUtils.isBlank(license) ){
            log.warn(" license is null !!! ");
            return null;
        }
        try {
            readWriteLock.readLock().lock();
            Map<String,SocketChannelSign> map = currentMap.get( license );
            for (Map.Entry<String, SocketChannelSign> entry:map.entrySet()){
                SocketChannelSign socketChannelSign =  entry.getValue();
                if ( socketChannelSign.getSign() == sign ){
                    return entry.getValue().getSocketChannel();
                }
            }
        }catch (Exception e){
            log.warn(e.getMessage());
        }finally {
            readWriteLock.readLock().unlock();
        }
        return null;
    }

    /**
     * 通过license 获取相机类型
     * @param license
     * @return
     */
    public static int getCameraTypeByLicense(String license){
        if( StringUtils.isBlank(license) ){
            log.warn(" license is null !!! ");
            return -1;
        }
        try {
            readWriteLock.readLock().lock();
            Map<String,SocketChannelSign> map = currentMap.get( license );
            for (Map.Entry<String, SocketChannelSign> entry:map.entrySet()){
                SocketChannelSign socketChannelSign =  entry.getValue();
                //返回正常相机的类型
                if ( socketChannelSign.getSign() ==  LicenseSignEnum.NORMAL_LINK.getCode() ){
                    return entry.getValue().getCameraType();
                }
            }
        }catch (Exception e){
            log.warn(e.getMessage());
        }finally {
            readWriteLock.readLock().unlock();
        }
        return -1;
    }

    /**
     * 返回所有的链接，健康检查用
     * @return
     */
    public static Map<String , Map<String,SocketChannelSign>> getAllChannel(){

        return currentMap;
    }

    /**
     * 通过license 返回一个链接，健康检查用
     * @return
     */
    public static  Map<String,SocketChannelSign> getChannelByLicenseCheck (String license ){

        if( StringUtils.isBlank(license) ){
            log.warn(" license is null !!! ");
            return null;
        }
        try {
            readWriteLock.readLock().lock();
            Map<String,SocketChannelSign> map = currentMap.get( license );
            return map;
        }catch (Exception e){
            log.error( e.getMessage() );
        }finally {
            readWriteLock.readLock().unlock();
        }
        return null;
    }



    public static SocketChannel getSocketChannel(String licenseId ,Integer sign){

        if(StringUtils.isBlank(licenseId) ){
            log.warn("licenseId is null");
            return null;
        }
        try {
            readWriteLock.readLock().lock();
            Map<String,SocketChannelSign> map = currentMap.get( licenseId );


            if(map == null){
                log.warn(" get SocketChannel 【fail】 , license = {} camera is 【offline】 !! " ,licenseId );
                return null;
            }
            Set<Map.Entry<String, SocketChannelSign>> keys = map.entrySet();
            for (Map.Entry<String, SocketChannelSign> entry:keys){
                SocketChannelSign socketChannelSign =  entry.getValue();
                if ( socketChannelSign.getSign() == sign ){
                    return entry.getValue().getSocketChannel();
                }
            }
        }catch (Exception e){
           e.printStackTrace();
        }finally {
            readWriteLock.readLock().unlock();
        }
        return null;
    }
}