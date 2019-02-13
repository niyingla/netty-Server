package com.faceword.nio.business.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/12/10 9:57
 * @Version 1.0
 * 人脸库版本修改对象
 */
@ToString
@Data
public class FaceVersionUpdate {

    private Integer groupNum;

    private List<Relest> data;

    @NoArgsConstructor
    @ToString
    @Data
    public static class Relest{

        private Long groupID;

        private Integer ver;

        public Relest(Long groupID, Integer ver) {
            this.groupID = groupID;
            this.ver = ver;
        }
    }
}
