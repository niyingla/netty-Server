package com.faceword.nio.handle;

import com.alibaba.fastjson.JSON;
import com.faceword.nio.business.NettyChannelMap;
import com.faceword.nio.business.ServerReceiveMessage;
import com.faceword.nio.business.response.FaceConsistencyCheck;
import com.faceword.nio.business.response.FaceVersionUpdate;
import com.faceword.nio.business.utils.ServerWriteUtils;
import com.faceword.nio.config.Constans;
import com.faceword.nio.config.NioBaseConfig;
import com.faceword.nio.enums.LicenseSignEnum;
import com.faceword.nio.mybatis.model.FaceDbAllFace;
import com.faceword.nio.redis.RedisConstans;
import com.faceword.nio.service.FaceDbService;
import com.faceword.nio.service.ServiceInformationTransmissionService;
import com.faceword.nio.service.entity.DeleteFaceMessage;
import com.faceword.nio.service.entity.DeleteFaceMessageLicense;
import com.faceword.nio.service.entity.FaceLibraryMessage;
import com.faceword.nio.service.nio.NioProcessService;
import com.faceworld.base.config.ApplicationContextProvider;
import com.faceworld.base.redis.RedisClientUtil;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/12/17 9:19
 * @Version 1.0
 */
@Slf4j
@Component(value = "handle213")
public class D5ProcessHandle  extends AbstractNioHandel {

    private FaceDbService faceDbService =
            ApplicationContextProvider.getBean( FaceDbService.class );

    private ServiceInformationTransmissionService transmissionService  =
            ApplicationContextProvider.getBean( ServiceInformationTransmissionService.class );

    private NioProcessService nioProcessService =  ApplicationContextProvider.getBean(NioProcessService.class);

    private NioBaseConfig nioBaseConfig = ApplicationContextProvider.getBean( NioBaseConfig.class );

    /**
     * 6.3人脸库版本一致性对比--人脸库人脸详情对比
     */
    @Override
    public void excute(ServerReceiveMessage receiveMessage) {
        log.info("---------faceConsistencyCheckD5  213 方法调用！！=========");
        String license = receiveMessage.getLicense();

        //设备返回的差异人脸
        FaceConsistencyCheck faceConsistencyCheck =  JSON.parseObject(receiveMessage.getBody() , FaceConsistencyCheck.class);

        if(faceConsistencyCheck.getGroupNum()==0){
            log.warn(" groupNum = {} , return " , faceConsistencyCheck.getGroupNum());
            return;
        }
        log.info(" get camera  face data size = {}" , faceConsistencyCheck.getGroupNum() );
        List<String> groupList = new ArrayList<>();
        faceConsistencyCheck.getData().forEach(value ->{
            groupList.add(value.getGroupID());
        });

        // 查询数据库中对应人脸库的所有人脸   ， -- 同步当前的数据到设备
        FaceDbAllFace targetFaceDbAllFace =  faceDbService.findFaceDbAllFaceList(groupList);


        // 使用fastutil进行人脸差异化对比
        LongArrayList sourceLongList = faceConsistencyCheck.getData().get(0).getFaceData();

        LongArrayList cameraDiffServerFaceSet = new LongArrayList();
        cameraDiffServerFaceSet.addAll(sourceLongList);

        LongArrayList serverDiffServerFaceSet = targetFaceDbAllFace.getFaceList();

        long startTime = System.nanoTime();
        cameraDiffServerFaceSet.removeAll(serverDiffServerFaceSet);

        serverDiffServerFaceSet.removeAll(sourceLongList);
        long endTime = System.nanoTime();

        log.info("人脸库对比 花费时间为：{}ns , {}ms" , (endTime-startTime) ,  (endTime-startTime)/1000000 );
        //将设备人脸集合放入redis缓存
//        faceConsistencyCheck.getData().forEach(sourceValue ->{
//
//
//            RedisClientUtil.setPipelineCollectionSet( RedisConstans.FACE_CONTRAST_SOURCE.concat(license),sourceValue.getFaceData() );
//        });

        //--log.info("设备端人脸列表  --> {} ",JSON.toJSONString(  faceConsistencyCheck.getData() ,true ));
        //--log.info("服务端人脸列表  --> {} ",JSON.toJSONString( targetFaceDbAllFace.getFaceList() ,true ));

        //将目标服务器人脸集合放入
//        RedisClientUtil.setPipelineCollectionSet(RedisConstans.FACE_CONTRAST_TARGET.concat(license),targetFaceDbAllFace.getFaceList());
//
//        //需要删除设备的集合
//        Set<String> cameraDiffServerFaceSet =
//                RedisClientUtil.differenceSet(RedisConstans.FACE_CONTRAST_SOURCE.concat(license),RedisConstans.FACE_CONTRAST_TARGET.concat(license));
//        //需要新增到设备的集合
//        Set<String> serverDiffServerFaceSet =
//                RedisClientUtil.differenceSet(RedisConstans.FACE_CONTRAST_TARGET.concat(license),RedisConstans.FACE_CONTRAST_SOURCE.concat(license));
//
//        //释放redis 对比集合 资源
//        RedisClientUtil.delete(RedisConstans.FACE_CONTRAST_SOURCE.concat(license));
//        RedisClientUtil.delete(RedisConstans.FACE_CONTRAST_TARGET.concat(license));

        //由于协议问题，暂时考虑一个人脸库差异对比
        if( cameraDiffServerFaceSet !=null && cameraDiffServerFaceSet.size()>0 ){

            log.info("need delete camera face , groupId = {} , faceList = {}" ,
                    targetFaceDbAllFace.getGroupId(), JSON.toJSONString(cameraDiffServerFaceSet,true));
            //需要删除相机人脸库人脸
            DeleteFaceMessageLicense deleteFaceMessageLicense = new DeleteFaceMessageLicense();
            deleteFaceMessageLicense.setLicense(license);
            deleteFaceMessageLicense.setCameraType(NettyChannelMap.getCameraTypeByLicense(license));
            deleteFaceMessageLicense.setDelFacesNum(cameraDiffServerFaceSet.size());
            deleteFaceMessageLicense.setGroupID(Long.valueOf(targetFaceDbAllFace.getGroupId()));
            deleteFaceMessageLicense.setListType(targetFaceDbAllFace.getListType());
            deleteFaceMessageLicense.setVer(targetFaceDbAllFace.getVersion());
            List<DeleteFaceMessage.FaceID> faceIDList = new ArrayList<>();
            for ( Long diffVal:cameraDiffServerFaceSet) {
                faceIDList.add(new DeleteFaceMessage.FaceID( diffVal ) );
            }

            deleteFaceMessageLicense.setDelFaces(faceIDList);

            // 发送 向设备 发送 删除命令
            transmissionService.deleteLibraryFace(deleteFaceMessageLicense);
            //由于对应为空，redis 取得值还是返回long 为1 ，所以此处不需要做加一处理
        }else{
            //进行占位操作   --为了最终的计数结果能为3
            RedisClientUtil.increment(RedisConstans.FACE_VERSION_CONTRACT.concat(license) );
        }

        if( serverDiffServerFaceSet!=null && serverDiffServerFaceSet.size()>0 ){
            log.info("server face need send to camera ,groupId = {} , faceList = {}" ,
                    targetFaceDbAllFace.getGroupId(),  JSON.toJSONString(serverDiffServerFaceSet,true));

            List<FaceLibraryMessage> faceLibraryMessageLicenseList = faceDbService.findFaceLibraryMessageLicenseList(serverDiffServerFaceSet);

            nioProcessService.addFaceListToRedisQuery(faceLibraryMessageLicenseList , nioBaseConfig.getFaceDbDomain() , license);
            log.info("同步设备人脸 与服务人脸差异的数据：{}" ,faceLibraryMessageLicenseList.size() );
            //开始同步
            transmissionService.distributionCtrl( license );

        }else{
            //进行占位操作   --为了最终的计数结果能为3
            RedisClientUtil.increment(RedisConstans.FACE_VERSION_CONTRACT.concat(license) );
        }

        if( cameraDiffServerFaceSet.size() == 0 && serverDiffServerFaceSet.size() == 0 ){
            // 相机的人脸与数据库中的人脸一致 人脸库版本不一致 ，更新相机人脸库版本为最新版本号
            FaceVersionUpdate faceVersionUpdate = new FaceVersionUpdate();
            faceVersionUpdate.setGroupNum(1);
            List<FaceVersionUpdate.Relest> list = new ArrayList<>();
            list.add( new FaceVersionUpdate.Relest(Long.valueOf(targetFaceDbAllFace.getGroupId()) , targetFaceDbAllFace.getVersion() ) );
            faceVersionUpdate.setData(list);
            ServerWriteUtils.writeHeadAndBody( Constans.D7  , JSON.toJSONString(faceVersionUpdate),
                    NettyChannelMap.getSocketChannel(license, LicenseSignEnum.NORMAL_LINK.getCode()));
            log.info("update camera face  db = {}， version ={} ok" , targetFaceDbAllFace.getGroupId(),targetFaceDbAllFace.getVersion() );
        }
    }
}
