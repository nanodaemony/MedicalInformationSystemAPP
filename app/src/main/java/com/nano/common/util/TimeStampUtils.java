package com.nano.common.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Vinicolor
 * <p>
 * Description:
 * 时间戳工具类
 */
public class TimeStampUtils {

    /**
     * 标记的时间格式
     */
    private static SimpleDateFormat markTimeFormat;

    /**
     * 设置时间日期格式
     */
    private static SimpleDateFormat inputMarkTimeFormat;

    /**
     * 解析输入的日期的格式
     */
    private static SimpleDateFormat parseInputTimeFormat;


    private static SimpleDateFormat simpleDateFormat;


    /**
     * 获取给服务器的时 间
     * @return 服务器本地时间
     */
    public static String getTimeForServer(){
        return "" + System.currentTimeMillis();
    }

    /**
     * 初始化
     */
    @SuppressLint("SimpleDateFormat")
    public static void init(){
        markTimeFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
        inputMarkTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
        parseInputTimeFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
        simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
    }

    /**
     * 将字符串形式的日期转化为long型日期
     * @param time 日期
     * @return long型日期
     */
    public static long getTimeStringToLong(String time) {
        Date date = null;
        try {
            date = parseInputTimeFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date == null) {
            return System.currentTimeMillis();
        } else {
            return date.getTime();
        }
    }

    /**
     * 获取默认标记的时间 格式:yyyy.MM.dd-HH:mm:ss
     */
    public static String getMarkTimeForServerDefault() {
        return markTimeFormat.format(System.currentTimeMillis());
    }

    /**
     * 从服务器获取到的时间转化
     * @param time 时间
     * @return 时间字符串
     */
    public static String getMarkTimeForServerDefault(long time) {
        return markTimeFormat.format(time * 1000);
    }


    /**
     * 获取手动输入时间的历史时间
     * @return 历史时间
     */
    public static String getInputHistoryTime(){
        return inputMarkTimeFormat.format(System.currentTimeMillis()) + "-";
    }


    /**
     * 得到当前的时间戳
     *
     * @return 当前时间戳
     */
    private static Integer getCurrentTimeStamp() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    /**
     * 得到与当前时间前多少分钟的时间戳
     *
     * @param minute 当前时间前多少分钟
     * @return 当前时间前多少分钟的时间戳
     */
    public static Integer getMinuteBeforeTimeStamp(int minute) {
        return getCurrentTimeStamp() - 60 * minute;
    }


    /**
     * 获取简单时间
     * @return 时间字符串
     */
    public static String getSimpleDateTime(long time) {
        return simpleDateFormat.format(time);
    }

    /**
     * 得到与当前时间前一天的时间戳
     *
     * @return 当前时间前一天的时间戳
     */
    public static Integer getDayBeforeTimeStamp() {
        return getCurrentTimeStamp() - 86400;
    }

    /**
     * 得到与当前时间前多少分钟的时间
     *
     * @param minute 当前时间前多少分钟就输入负
     *               需要退后就输入正
     *               例如：得到前十分钟，输入 -10
     * @return 当前时间前多少分钟的时间
     */
    public static Date getMinuteDate(int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    /**
     * @return
     */
    public static Date getDayBeforeDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    /**
     * @return
     */
    public static Date getDayAfterDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        return calendar.getTime();
    }

    /**
     * @return
     */
    public static Date getWeekBeforeDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        return calendar.getTime();
    }

    /**
     * 通过 year month date 得到Date实体，注意这里是返回的月份已经对月份减去了1，可以直接输入需要的月份
     *
     * @param year
     * @param month
     * @param date
     * @return
     */
    public static Date getDate(Integer year, Integer month, Integer date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, date, 0, 0, 0);
        return calendar.getTime();
    }

    /**
     * 得到当前的时间
     *
     * @return
     */
    public static Date getDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    /**
     * 通过 year month date hourOfDay minute second得到Date实体，注意这里是返回的月份已经对月份减去了1，可以直接输入需要的月份
     *
     * @param year
     * @param month
     * @param date
     * @param hourOfDay
     * @param minute
     * @param second
     * @return
     */
    public static Date getDate(Integer year, Integer month, Integer date, Integer hourOfDay, Integer minute, Integer second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, date, hourOfDay, minute, second);
        return calendar.getTime();
    }
}
