package com.faceword.nio.service.entity;

import com.faceword.nio.config.Constans;
import lombok.Data;
import lombok.ToString;

/**
 * @Author: zyong
 * @Date: 2018/11/7 13:48
 * @Version 1.0
 * 服务器向设备设置时间对象
 */
@Data
@ToString
public class TimeZone {

    private String timeZone = Constans.TIME_ZONE;

    private String time;

}
