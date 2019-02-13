package com.faceword.nio.mybatis.mapper;

import com.faceworld.base.mybatis.model.MansFaceLogWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/11/27 17:23
 * @Version 1.0
 *  日志自定义的方法
 *
 */
public interface MansFaceLogCustomMapper {

    List<MansFaceLogWithBLOBs> findListByLicense(@Param(value = "license") String license);


}
