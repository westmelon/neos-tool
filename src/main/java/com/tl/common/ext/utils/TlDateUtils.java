package com.tl.common.ext.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
 * @Description: 时间处理工具类
 * @Author Neo Lin
 * @Date  2018/4/14 14:40
 */
public class TlDateUtils {

    public static final String YYYY_MM_DD = "yyyyMMdd";


    private TlDateUtils() {
    }

    public static Date parseString2Date(String dateStr, String pattern) {
        if (dateStr == null || "".equals(dateStr) || pattern == null || "".equals(pattern)) {
            return null;
        }
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String format(Date date, String pattern) {
        if (date == null || pattern == null || "".equals(pattern)) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static Date getFirstDayOfMonth() {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1, 0, 0, 0);
        return c.getTime();
    }

    public static Date getFirstDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1, 0, 0, 0);
        return c.getTime();
    }

    public static Date getFirstDayOfNextMonth() {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, 1, 0, 0, 0);
        return c.getTime();
    }

    public static Date getFirstDayOfNextMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, 1, 0, 0, 0);
        return c.getTime();
    }

    public static Date addDay(Date date, Integer add) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE) + add, 0, 0, 0);
        return c.getTime();
    }

    public static Date addMinute(Date date, Integer add) {
        return addTime(date, Calendar.MINUTE, add);
    }

    public static Date addTime(Date date, int field, Integer add) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(field, add);
        return c.getTime();
    }

    /*
     * @Description: 如果是2019-01-01之前 返回2019-01-01 否则返回传入日期
     * @param  []
     * @return  java.util.Date
     */
    public static Date getShitDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.set(2019, 0, 1);
        if (date.getTime() < c.getTime().getTime()) {
            return c.getTime();
        } else {
            return date;
        }

    }


    /*
     * @Description:  获取当月首个工作日
     * @param  [date]
     * @return  java.util.Date
     */
    public static Date getFirstWorkingDayOfMonth(Date date) {
        Date firstDate = getFirstDayOfMonth(date);
        Calendar c = Calendar.getInstance();
        c.setTime(firstDate);
        int week = c.get(Calendar.DAY_OF_WEEK);
        if (week == 1) { //周日
            c.add(Calendar.DAY_OF_MONTH, 1);
            return c.getTime();
        }
        if (week == 7) { //周六
            c.add(Calendar.DAY_OF_MONTH, 2);
            return c.getTime();
        } else {
            return c.getTime();
        }
    }

    //如果那个周末是调休上班的，返回false
    private static boolean isWeekends(Date date, String working){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        if(working == null){
            working = "";
        }
        int week = c.get(Calendar.DAY_OF_WEEK);
        if ((week == 1 || week == 7) && !working.contains(format(date, YYYY_MM_DD))) { //周日
            c.add(Calendar.DAY_OF_MONTH, 1);
            return true;
        }else {
            return false;
        }
    }

    /*
     * @Description: 排除法定假日和双休日(顺延）
     * @param  [date]
     * @return  java.util.Date
     */
    public static Date excludeHoliday(Date date, String holiday, String working){
        String dateStr = "";

        Date temp = date;
        dateStr = format(temp, YYYY_MM_DD);
        if(holiday == null){
            holiday = "";
        }
        while(holiday.contains(dateStr) || isWeekends(temp, working) ){
            Calendar c = Calendar.getInstance();
            c.setTime(temp);
            c.add(Calendar.DAY_OF_MONTH,1);
            temp = c.getTime();
            dateStr = format(temp, YYYY_MM_DD);
        }
        return temp;
    }



    /*
     * @Description: -1 less than  0 equal 1 greatter than
     * @param  [date]
     * @return  int
     */
    public static int compareWithNow(Date date) {
        Date now = new Date();
        long nowTime = now.getTime();
        long dateTime = date.getTime();
        return Long.compare(dateTime, nowTime);
    }


}
