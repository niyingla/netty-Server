package com.faceword.nio.service;


import com.faceword.nio.mybatis.mapper.FaceDbCustomMapper;
import com.faceword.nio.mybatis.model.FaceDbAllFace;
import com.faceword.nio.service.entity.FaceLibraryMessage;
import com.faceword.nio.service.entity.FaceLibraryMessageLicense;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: zyong
 * @Date: 2018/11/29 19:56
 * @Version 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true )
public class FaceDbService {

    @Autowired
    FaceDbCustomMapper faceDbCustomDao;


    public List<FaceLibraryMessage> findFaceLibraryList(List<String> dbList){

        List<FaceLibraryMessage> libraryMessageList =  faceDbCustomDao.findFaceLibraryList(dbList);

      //  log.info(JSON.toJSONString(libraryMessageList));
        return libraryMessageList;
    }


    public  List<Map<String,String>> findCameraAllFaceDbList(String license){

        return faceDbCustomDao.findCameraAllFaceDbList(license);
    }

    public FaceDbAllFace findFaceDbAllFaceList(List<String> groupList){

        return faceDbCustomDao.findFaceDbAllFaceList(groupList);
    }

    public List<FaceLibraryMessage> findFaceLibraryMessageLicenseList(LongArrayList faceSet){

        return  faceDbCustomDao.findFaceLibraryMessageLicenseList(faceSet);
    }

}
