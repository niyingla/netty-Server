package com.faceword.nio.enums;

/**
 * @Author: zyong
 * @Date: 2018/11/9 16:04
 * @Version 1.0
 *  文件类型枚举
 */
public enum ImageTypeEnum {

    JPEG ( 0 , "jpeg"),
    PNG ( 1, "png"),
    JPG( 2 , "jpg"),
    BMP( 3 , "bmp");

    private Integer code;

    private String value;

    ImageTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static ImageTypeEnum typeOf(Integer code) {
        for (final ImageTypeEnum option : ImageTypeEnum.values()) {
            if (option.getCode().equals(code)) {
                return option;
            }
        }
        return null;
    }
}
