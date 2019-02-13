package com.faceword.nio.handle;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.business.NettyChannelMap;
import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.business.response.ConsistencyCheckMessage;
import com.faceword.nio.business.response.ConsistencyCheckMessageRes;
import com.faceword.nio.business.utils.ServerWriteUtils;
import com.faceword.nio.config.Constans;
import com.faceword.nio.config.NioBaseConfig;
import com.faceword.nio.enums.LicenseSignEnum;
import com.faceword.nio.enums.ResonceCodeEnum;
import com.faceword.nio.redis.RedisConstans;
import com.faceword.nio.service.FaceDbService;
import com.faceword.nio.service.MansFaceLogService;
import com.faceword.nio.service.ServiceInformationTransmissionService;
import com.faceword.nio.service.entity.DeleteAllFaceMessageLicense;
import com.faceword.nio.service.entity.FaceLibraryMessage;
import com.faceword.nio.service.nio.NioProcessService;
import com.faceworld.base.config.ApplicationContextProvider;
import com.faceworld.base.redis.RedisClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: zyong
 * @Date: 2018/12/17 9:18
 * @Version 1.0
 */
@Slf4j
@Component(value = "handle211")
public class D3ProcessHandle  extends AbstractNioHandel{


    private FaceDbService faceDbService =
            ApplicationContextProvider.getBean( FaceDbService.class );

    private NioProcessService nioProcessService =  ApplicationContextProvider.getBean(NioProcessService.class);

    private NioBaseConfig nioBaseConfig = ApplicationContextProvider.getBean( NioBaseConfig.class );

    private ServiceInformationTransmissionService transmissionService =
            ApplicationContextProvider.getBean( ServiceInformationTransmissionService.class ); ;

    /**
     * 6.2人脸库版本一致性对比-人脸库对比（设备发起人脸对比请求）
     * 【该过程主要保证人脸库数量的一致性 ，在设备与服务器人脸库数量一致性的情况下 才会对比人脸库人脸的一致性】
     * @param receiveMessage
     */
    @Override
    public void excute(ServerReceiveMessage receiveMessage) {
        log.info("---------faceDbConsistencyCheckD3  243 方法调用！！=========");

        //设备返回的数据
        ConsistencyCheckMessage consistencyCheckMessage = JSON.parseObject(receiveMessage.getBody() , ConsistencyCheckMessage.class);
        String license = receiveMessage.getLicense();
        //查询当前设备对应的人脸库 及 版本号

        List<ConsistencyCheckMessage.Result> sourceList = consistencyCheckMessage.getData();
        List<Map<String,String>> targetFaceMap =  faceDbService.findCameraAllFaceDbList(license);

        //防止sourceList 出现空指针
        Integer sourceListSize= sourceList == null? 0 :  sourceList.size();
        //{ "groupId":"6469103915685253120","version":2 } targetFaceMap 列表
        log.info( "camera face db size={} -- server face db={}!" ,sourceListSize , targetFaceMap.size() );
        if( sourceListSize < targetFaceMap.size() ){
            //相机人脸库数量小于云端人脸库
            this.cameraFaceDbSizeLessThanServerFaceDBSizeProcess(sourceList,targetFaceMap,faceDbService,nioBaseConfig,license);
        }else if( sourceListSize > targetFaceMap.size()){
            // 设备的人脸库数量比云端人脸库多  -- 删除
            this.deleteCarameMoreFaceDb(license,sourceList,targetFaceMap);
        }else {
            //设备人脸库数量等于云端人脸库数量
            this.cameraFaceDbSizeEqualToServerFaceDbSizeProcess(sourceList,targetFaceMap,sourceListSize,license);
        }
    }
    /**
     * 相机人脸库数量等于云端人脸库数量
     * @param sourceList
     * @param targetFaceMap
     * @param sourceListSize
     * @param license
     */
    private void cameraFaceDbSizeEqualToServerFaceDbSizeProcess(List<ConsistencyCheckMessage.Result> sourceList ,
                                                               List<Map<String,String>> targetFaceMap,Integer sourceListSize,String license){

        log.info("---------------cameraFaceDbSizeEqualToServerFaceDbSizeProcess-------------------");
        if(sourceListSize==0){
            synchronousSuccess(license);
            return;
        }

        //当前的情况是设备的人脸库数与服务的人脸库数量是一致的
        boolean isDel =  this.deleteCarameMoreFaceDb(license,sourceList,targetFaceMap);
        if(!isDel){

            ConsistencyCheckMessageRes checkMessageRes = new ConsistencyCheckMessageRes();
            List<ConsistencyCheckMessage.Result> rsParamList = new ArrayList<>();
            int groupNum = 0;
            /**
             * 返回差异的人脸库
             */
            for (ConsistencyCheckMessage.Result  sourceValue :sourceList ){
                //log.info("map==={}",JSON.toJSONString( targetFaceMap , true));
                for (Map<String,String> targetMap : targetFaceMap){
                    log.info( "groupId===={},version == {}", targetMap.get("groupId") , targetMap.get("version") );
                    Long targetGroupId = Long.valueOf( targetMap.get("groupId") );
                    String versionStr =  String.valueOf(targetMap.get("version"));
                    Integer version = Integer.parseInt(versionStr);
                    if(sourceValue.getGroupID().equals(targetGroupId) ){
                        //当前的人脸库id相同
                        if( sourceValue.getVer() != version ){
                            log.info(" camera faceDB version = {} , server faceDB version = {} " ,  sourceValue.getVer() ,version);
                            //版本号不同
                            groupNum ++;
                            ConsistencyCheckMessage.Result result = new ConsistencyCheckMessage.Result();
                            result.setGroupID(targetGroupId);
                            //  返回差异的人脸库版本号，当前人脸库的版本号 设备不需要进行写入配置文件
                            result.setVer( version );
                            rsParamList.add(result);
                            break;

                        }
                    }
                }
                // 当前版本一次只对比一个差异的人脸库
                if( rsParamList.size() ==1 ){
                    break;
                }
            }

            if(groupNum == 1){
                //向设备端发送不一致的人脸库版本号  -> 进入人脸库详情对比
                checkMessageRes.setGroupNum(groupNum);
                checkMessageRes.setData(rsParamList);
                checkMessageRes.setErrcode(ResonceCodeEnum.VERSIOIN_INCONSISTENT.getCode());
                ServerWriteUtils.writeHeadAndBody(Constans.D4,JSON.toJSONString(checkMessageRes), NettyChannelMap.getSocketChannel(license, LicenseSignEnum.NORMAL_LINK.getCode()));
                log.info("send face diff db version data to camera is = {}" ,JSON.toJSONString(checkMessageRes,true));
            }else{
                //同步成功 ，触发
                synchronousSuccess(license);
            }

        }
    }

    /**
     * 相机人脸库数量小于服务器人脸库数量 处理
     * @param sourceList
     * @param targetFaceMap
     * @param faceDbService
     * @param nioBaseConfig
     * @param license
     */
    private void cameraFaceDbSizeLessThanServerFaceDBSizeProcess(List<ConsistencyCheckMessage.Result> sourceList ,
                                                                List<Map<String,String>> targetFaceMap, FaceDbService faceDbService,
                                                                NioBaseConfig nioBaseConfig ,String license){
        log.info("--------------cameraFaceDbSizeLessThanServerFaceDBSizeProcess-----------------");
        // 设备的人脸库比服务器的人脸库少（或者没有人脸库）
        if( targetFaceMap!=null && targetFaceMap.size()>0 ){
            List<String> serverDbParamList = null;
            if(sourceList == null || sourceList.size()== 0){
                serverDbParamList = new ArrayList<>();
                for (Map<String,String> tarMap :targetFaceMap ) {
                    serverDbParamList.add( tarMap.get("groupId") );
                }
            }else{
                serverDbParamList = this.getServerFaceDbGreaterThanCameraDbList(sourceList,targetFaceMap);
            }
            //  需要添加人脸库的版本号
            List<FaceLibraryMessage> faceLibraryMessageList = faceDbService.findFaceLibraryList( serverDbParamList );


             //人脸列表放入缓存队列
            nioProcessService.addFaceListToRedisQuery(faceLibraryMessageList,nioBaseConfig.getFaceDbDomain(),license);


            log.info(" camera face db size is {} , Synchronized server  face db to camera! ",sourceList == null ? 0:sourceList.size() );

            //直接将状态值设为2，同步完以后会继续加一
            RedisClientUtil.set(RedisConstans.FACE_VERSION_CONTRACT.concat(license), 2 );
            //开始同步
            transmissionService.distributionCtrl( license );
        }
        /**
         * 统一回复
         */
        this.sendNomalMessageToCamera(license);
    }

    /**
     * 删除相机中 对应 服务器不存在的人脸库
     */
    private boolean deleteCarameMoreFaceDb(String license, List<ConsistencyCheckMessage.Result> sourceList , List<Map<String,String>> targetFaceMap){
        boolean isDelCameraDb = false;
        for (ConsistencyCheckMessage.Result  sourceValue :sourceList ){
            boolean isFond = false;
            String souceGroupId = String.valueOf(sourceValue.getGroupID());
            for (Map<String,String> targetMap : targetFaceMap){

                if(souceGroupId.equals(targetMap.get("groupId"))){
                    isFond = true;
                    break;
                }
            }
            if( !isFond ){
                log.info("current camera face db in server face db  not fond , not it groupID = {}" ,souceGroupId );

                //TODO 需要完善日志
                DeleteAllFaceMessageLicense deleteFaceDbMessage = new DeleteAllFaceMessageLicense();
                deleteFaceDbMessage.setLicense( license );
                deleteFaceDbMessage.setGroupID( sourceValue.getGroupID() );
                deleteFaceDbMessage.setIsAll(0);
                log.warn("camera type is not know!");
                // deleteAllFaceMessageLicense.setSign();
                /**
                 * 放入redis队列 ， 是为了删除完人脸库后继续做对比操作
                 */
                RedisClientUtil.publisherQuery(RedisConstans.
                        FACE_DELETE_ALL_QUERY.concat(license),deleteFaceDbMessage);

                log.info("send delete face db request mode ! ");
                isDelCameraDb = true;
            }
        }

        if( isDelCameraDb ){
            DeleteAllFaceMessageLicense deleteFaceDbMessage =  RedisClientUtil.receiverQuery(RedisConstans.
                    FACE_DELETE_ALL_QUERY.concat(license));

            //直接将状态值设为2，同步完以后会继续加一
            RedisClientUtil.set(RedisConstans.FACE_VERSION_CONTRACT.concat(license), 2 );

            //先回一个正常的消息 ，有序会再次触发比对
            this.sendNomalMessageToCamera(license);

            //开始执行删除操作
            transmissionService.deleteAllFace( deleteFaceDbMessage );
        }

        return isDelCameraDb;
    }
}
