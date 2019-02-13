package com.faceword.nio.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * @Author: zyong
 * @Date: 2018/11/5 14:41
 * @Version 1.0
 */
@Slf4j
public class DateUtils {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd";

    private DateUtils() {}

    /**
     * 用给定的日期模式构造SimpleDateFormat对象
     *
     * @param pattern 日期模式
     * @return 指定日期模式的SimpleDateFormat对象
     */
    public static SimpleDateFormat getSimpleDateFormat(String pattern) {
        if (StringUtils.isBlank(pattern)) {
            return new SimpleDateFormat(DEFAULT_PATTERN, Locale.ENGLISH);
        } else {
            return new SimpleDateFormat(pattern, Locale.ENGLISH);
        }
    }

    /**
     * 获取十位日期 <br>
     *
     * @return yyyy-MM-dd，如:2018-01-01
     */
    public static String getTwelveDate() {
        return getSimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    /**
     *
     * @param date
     * @return
     */
    public static String getTwelveDate(Date date) {
        return getSimpleDateFormat("yyyy-MM-dd").format( date );
    }

    /**
     * 获取八位日期 <br>
     *
     * @return yyyyMMdd，如:20180101
     */
    public static String getTenDate() {
        return getSimpleDateFormat("yyyyMMdd").format(new Date());
    }

    /**
     * 获取年月日期 <br>
     * @return yyyy-MM，如：2018-01
     */
    public static String getYearMounth(){
        return getSimpleDateFormat("yyyy-MM").format(new Date());
    }


    public static String getDateTime(Date date) {
        return getSimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    /**
     * 获取日期时间
     *
     * @return yyyyMMddHHmmss，如：20180101010101
     */
    public static String getDateTime()  {
        return getSimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    /**
     * 获取当前日期的上一月份 <br>
     * 说明：执行该方法时，特别注意日期，如果是大小月可能会出问题；
     * 		最好选择在一个每个月都有的日期执行；
     * @return 返回上个月的今天(yyyy-MM-dd)
     */
    public static String getPreMounth() {
        Calendar calendar = Calendar.getInstance();
        //得到前一个月
        calendar.add(Calendar.MONTH, -1);
        String preMounth = getSimpleDateFormat(null).format(calendar.getTime());
        return preMounth;
    }

    /**
     * 返回一天最开始的日期对象
     * @return
     */
    public static Date getTadayFirstTime(Date date){

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        return cal1.getTime();
    }

    /**
     * 获取一天最后的时间
     * @param date
     * @return
     */
    public static Date getTadayEndTime(Date date){
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        cal1.set(Calendar.HOUR_OF_DAY, 23);
        cal1.set(Calendar.MINUTE, 59);
        cal1.set(Calendar.SECOND, 59);
        cal1.set(Calendar.MILLISECOND, 999);
        System.out.printf("%1$tF %1$tT\n", cal1.getTime());// cal1.getTime()返回的Date已经是更新后的对象
        return cal1.getTime();
    }

    /**
     * 获取昨天的日期
     * @return
     */
    public static Date getBeforeDate(){
        Calendar calendar = Calendar.getInstance();//此时打印它获取的是系统当前时间
        calendar.add(Calendar.DATE, -1);    //得到前一天
        return calendar.getTime();
    }

    /**
     * 获取日期 <br>
     * @param date
     * @return 返回：Mon Sep 03 22:51:39 CST 2018
     * @throws ParseException
     */
    public static Date getDateInfo(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = null ;
        try {
            strtodate  = formatter.parse(date, pos);
        }catch (Exception e){
            log.info(e.getMessage());
            strtodate = new Date();
        }
        return strtodate;
    }

    /**
     * 返回此类型的格式
     * @param date
     * @return
     */
    public static Date getDateInfoyyyyMMddHHmmss(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        ParsePosition pos = new ParsePosition(0);
        Date strtodate = null ;
        try {
            strtodate  = formatter.parse(date, pos);
        }catch (Exception e){
            log.info(e.getMessage());
            strtodate = new Date();
        }
        return strtodate;
    }

    public static Date getDateInfoyyyyMMdd(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_PATTERN);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = null ;
        try {
            strtodate  = formatter.parse(date, pos);
        }catch (Exception e){
            log.info(e.getMessage());
            strtodate = new Date();
        }
        return strtodate;
    }

    public static void main(String[] args) {

        System.out.println( DateUtils.getTwelveDate(DateUtils.getDateInfoyyyyMMdd("2018-12-28 00:00:00")) );
    }

}
