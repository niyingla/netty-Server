package com.faceword.nio.common;




import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

/**
 * @Author: zyong
 * @Date: 2018/11/2 17:46
 * @Version 1.0
 */
@Data
@ToString
public class UploadBean {

    private String oldName;

    private String newName;

    @JSONField(serialize = false)
    private byte[] source;

    private long size;

    private String folder;

    private String url;

    public UploadBean() {
        super();
    }

    public UploadBean(String oldName, byte[] source) {
        super();
        this.oldName = oldName;
        this.source = source;
        this.size = source.length;
    }



}
