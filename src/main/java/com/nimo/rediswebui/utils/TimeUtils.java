package com.nimo.rediswebui.utils;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-06-27 10:52
 * @Description: 描述
 */
public class TimeUtils {


    public static String getTimeDes(long second) {
        String viewtime = "";
        int year =  24 * 60 * 60 * 365;// 一年
        int mon =  24 * 60 * 60 * 30;// 一月
        int day =  24 * 60 * 60;// 一天
        int hour = 60 * 60;// 一小时
        int min = 60;// 一分钟
       if(second/year>0){
           viewtime=second/year+"年";
           second=second%year;
       }
       if(second/mon>0){
            viewtime+=second/mon+"月";
            second=second%mon;
       }
       if(second/day>0){
            viewtime+=second/day+"天";
            second=second%day;
       }
       if(second/hour>0){
            viewtime+=second/hour+"小时";
            second=second%hour;
        }
        if(second/min>0){
            viewtime+=second/min+"分";
            second=second%min;
        }
        if(second>0){
            viewtime+=second+"秒";
        }
        return viewtime;
    }

}
