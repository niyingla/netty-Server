package com.faceword.nio.mybatis.mapper;

import com.faceword.nio.mybatis.model.FaceDbAllFace;
import com.faceword.nio.service.entity.FaceLibraryMessage;
import com.faceword.nio.service.entity.FaceLibraryMessageLicense;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.apache.ibatis.annotations.Param;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: zyong
 * @Date: 2018/11/27 17:23
 * @Version 1.0
 *  人脸库操作自定义方法
 *
 */
public interface FaceDbCustomMapper {

    List<FaceLibraryMessage> findFaceLibraryList(@Param(value = "dbList") List<String> dbList);

    /**
     * 查询出相机对应的所有人脸库
     * @return
     */
    List<Map<String,String>> findCameraAllFaceDbList(String license);

    /**
     * 查询人脸库所有的人脸
     * @return
     */
    FaceDbAllFace findFaceDbAllFaceList(@Param(value = "groupList")List<String> groupList);

    List<FaceLibraryMessage> findFaceLibraryMessageLicenseList(@Param(value = "faceSet") LongArrayList faceSet);
}
