package com.faceword.nio.utils;




import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 字符处理类 <br>
 */
public class StringUtils {

    /**
     * 将日期格式化为指定格式字符串 <br>
     * @param myDate 日期
     * @param formatStr 指定日期格式
     * @return
     */
    public static String formatDateByFormatStr(Date myDate, String formatStr) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
        return formatter.format(myDate);
    }

    /**
     * 生成yyyy-MM-dd HH:mm:ss格式日期时间 <br>
     * @return
     */
    public static String formatDateYYYY_MM_DD_HH_mm_ss() {
        return formatDateByFormatStr(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 将日期转化为yyyy-MM-dd HH:mm:ss格式字符串 <br>
     * @param date 日期
     * @return
     */
    public static String formatDateYYYY_MM_DD_HH_mm_ss(Date date) {
        return formatDateByFormatStr(date, "yyyy-MM-dd HH:mm:ss");
    }



    /**
     *  获取文件格式的路径
     * @param date 日期
     * @return
     */
    public static String fileFormatDateYMDHMS(Date date) {
        return formatDateByFormatStr(date, "yyyyMMdd").concat("/").concat(formatDateByFormatStr(date, "HH"));
    }

    /**
     * 生成日历的年月日信息 <br>
     * @return
     */
    public static String formatDatePathYYYYMMDD() {
        Calendar c = Calendar.getInstance();
        //获取年
        int year = c.get(Calendar.YEAR);
        //获取月份，0表示1月份
        int month = c.get(Calendar.MONTH) + 1;
        //获取当前天数
        int day = c.get(Calendar.DAY_OF_MONTH);
        String rs = String.valueOf(year) + "/" + String.valueOf(month) +"/" +String.valueOf(day);
        return rs;
    }


    /**
     * 将首字母转成大写
     * @param s
     * @return
     */
    public static String toUpperCaseFirstOne(String s){
            if(s == null){
                return "";
            }
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }





    public static void main(String[] args) throws Exception {



        new Thread(new Runnable() {
            @Override
            public void run() {
                process(100,120);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                process(121,140);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                process(161,180);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                process(181,200);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                process(201,220);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                process(221,240);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                process(241,255);
            }
        }).start();
    }


    public static void process( int start ,int end){
        try {
            for ( int i=start;i<=end;i++){

                InetAddress address;
                String ipSuffix = "192.168.0.";
                address =InetAddress.getByName(ipSuffix.concat(i+""));
                boolean isTrue =  address.isReachable(1);
                System.out.println("Name:" + address.getHostName());
                System.out.println("Addr:" + address.getHostAddress());
                System.out.println("Reach:" +isTrue); //是否能通信 返回true或false
                if(isTrue){
                    System.out.println(address.toString());
                    System.out.println("++++++++++++++++++++++++++++++++");
                }else{

                    System.out.println("----------------------------------------------------");
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
