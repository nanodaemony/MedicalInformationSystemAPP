package com.nano.datacollection.util;

import android.annotation.SuppressLint;

import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Description: 时间相关工具类
 *
 * 时间戳工具类
 * 类中命名规则：
 * Date代表年月日
 * Time年月日时分秒
 */
@SuppressLint("NewApi")
public class TimeStampUtils {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 将某时间字符串转为自定义时间格式的LocalDateTime
     * 由于返回LocalDateTime，所以将会采用yyyy-MM-dd HH:mm:ss格式解析
     *
     * @param str 待解析的字符串
     *            字符串格式2019-08-01 11:11:11
     * @return LocalDateTime
     * @throws DateTimeException if an error occurs during formatting
     */

    public static LocalDateTime parseStringToLocalDateTime(String str) throws DateTimeException {
        return LocalDateTime.parse(str, DEFAULT_FORMATTER);
    }

    /**
     * 解析字符串返回LocalDate
     * 由于返回LocalDate，所以将会采用yyyy-MM-dd格式解析
     *
     * @param str 待解析的字符串
     *            字符串格式2019-08-01
     * @return LocalDate
     * @throws DateTimeException if an error occurs during formatting
     */
    public static LocalDate parseStringToLocalDate(String str) throws DateTimeException {
        return LocalDate.parse(str, DATE_FORMATTER);
    }

    /**
     * 将LocalDateTime转为自定义的时间格式的字符串
     * 默认转换为yyyy-MM-dd HH:mm:ss
     *
     * @param localDateTime 待转换的数据
     * @param pattern       自定义转换的格式
     * @return 输出示例2019-10-12 16:16:16
     * @throws DateTimeException if an error occurs during formatting
     */
    public static String parseLocalDateTimeToString(LocalDateTime localDateTime, String pattern) throws DateTimeException {
        if (Objects.isNull(pattern)) {
            return DEFAULT_FORMATTER.format(localDateTime);
        } else {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
            return dateTimeFormatter.format(localDateTime);
        }
    }

    /**
     * 得到当前的时间的字符串
     * 例如 2019-10-12 16:16:16
     *
     * @return 当前时间的字符串
     */
    public static String getCurrentTimeAsString() {
        return parseLocalDateTimeToString(parseTimeStampToLocalDateTime(getCurrentMillisecondTimeStamp()), null);
    }

    /**
     * 将long类型的timestamp转为LocalDateTime
     * 能够自动检测传入的时间戳的位数，如果传入秒单位，将会损失毫秒
     *
     * @param timestamp 传入的时间戳
     * @return 实例化的LocalDateTime对象
     */
    public static LocalDateTime parseTimeStampToLocalDateTime(long timestamp) {
        // 判断是否传入的是以秒为单位，如果是的，将会0补齐
        if (checkTimestampSecondDigit(timestamp)) {
            timestamp *= 1000;
        }
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * 判断是否传入的是以秒为单位
     *
     * @param timestamp 传入的时间戳
     * @return 如果是以秒为单位将返回true
     */
    private static boolean checkTimestampSecondDigit(long timestamp) {
        return String.valueOf(timestamp).length() == 10;
    }

    /**
     * 将LocalDateTime转为long类型的timestamp
     *
     * @param localDateTime 传入的localDateTime
     * @return 转换的timestamp
     */
    public static long parseLocalDateTimeToTimeStamp(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return getCurrentSecondTimeStamp();
        }
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli() / 1000;
    }

    private static long getCurrentSecondTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    private static long getCurrentMillisecondTimeStamp() {
        return System.currentTimeMillis();
    }

    public static Timestamp getCurrentTimeStamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 得到与当前时间前多少分钟的时间戳
     *
     * @param minute 当前时间前多少分钟
     * @return 当前时间前多少分钟的时间戳
     */
    public static long getMinuteBeforeTimeStampValue(int minute) {
        return getCurrentTimeStampValue() - 60 * minute * 1000L;
    }

    /**
     * 得到与当前时间前一天的时间戳
     *
     * @param days 当前时间前多少天
     * @return 当前时间前一天的时间戳
     */
    public static long getDayBeforeTimeStampValue(int days) {
        return getCurrentTimeStampValue() - 86400 * days;
    }

    @Deprecated
    public static Long getTimeStampOfTimeStampObject(Timestamp markTime) {
        return markTime.getTime() / 1000;
    }

    /**
     * 得到当天的零时的LocalDateTime
     * 例如当前时间为11:10，得到的为0:00的时间戳
     *
     * @return 对应的LocalDateTime
     */
    public static LocalDateTime getCurrentDayZeroLocalDateTime() {
        LocalDateTime current = LocalDateTime.now();
        return LocalDateTime.of(current.getYear(), current.getMonth(), current.getDayOfMonth(), 0, 0, 0, 0);
    }

    /**
     * 获取持续时间
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 持续的时间：单位为秒S
     */
    public static long getDurationTime(LocalDateTime startTime, LocalDateTime endTime) {
        return Duration.between(startTime, endTime).getSeconds();
    }


    /**
     * 得到历史的零时时间的LocalDateTime
     * @param days 历史天数
     * @return 如days=3.则获取的是3天前的时间戳
     */
    public static LocalDateTime getHistoryDayZeroLocalDateTimeBeforeNow(int days) {
        return getCurrentDayZeroLocalDateTime().minusDays(days);
    }


    /**
     * 得到历史的零时时间的LocalDateTime列表
     * @param days 历史天数
     * @return 如days=3.则获取的是3天，2天，1天前的时间戳列表
     */
    public static List<LocalDateTime> getHistoryDayZeroLocalDateTimeBeforeNowList(int days) {

        List<LocalDateTime> localDateTimeList = new ArrayList<>(days);

        for (int i = 1; i <= days; i++) {
            localDateTimeList.add(getCurrentDayZeroLocalDateTime().minusDays(i));
        }
        return localDateTimeList;
    }


    /**
     * 得到指定月份零时的LocalDateTime
     * 例如指定月时间为2019/11/1 11:11得到的为2019/11/1 0:0的时间戳
     * <p>如果输入的参数month大于12，将返回第二年的零时</p>
     *
     * @param month 指定的月份
     * @return 对应的LocalDateTime
     */
    public static LocalDateTime getMonthZeroLocalDateTime(int month) {
        if (month >= 1 && month <= 12) {
            return LocalDateTime.of(LocalDateTime.now().getYear(), month, 1, 0, 0, 0, 0);
        }
        return LocalDateTime.of(LocalDateTime.now().getYear() + 1, 1, 1, 0, 0, 0, 0);
    }

    /**
     * 得到指定月份有多少天
     *
     * @param year  年份
     * @param month 月份
     * @return 指定月份有多少天
     */
    public static int getMaxDayOfMonth(int year, int month) {
        LocalDate localDate = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1);
        return localDate.getDayOfMonth();
    }

    private static boolean checkYearAndMonthAndDay(int year, int month, int day) {
        if (month > 12 || month < 0 || day < 0 || day > 31) {
            return true;
        }
        if (year == 0) {
            return false;
        }
        if (month == 0) {
            return false;
        }
        return day > getMaxDayOfMonth(year, month);
    }

    /**
     * 如果传入0月，将会返回系统所支持的初始日期
     *
     * @param year  年份
     * @param month 月份
     * @param day   天数
     * @return LocalDate
     */
    public static LocalDate getStartLocalDate(int year, int month, int day) {
        LocalDate current = LocalDate.now();
        if (checkYearAndMonthAndDay(year, month, day)) {
            return current;
        }
        // 如果指定了年份
        if (year != 0) {
            // 没有指定月份或指定第12个月的
            if (month == 0) {
                // 指定年份，没有指定月份（当月），没有指定天数（当天），获取指定的当年1月1日
                if (day == 0) {
                    return current.withMonth(1).withDayOfMonth(1);
                }
                // 指定年份，没有指定月份（当月），指定天数，获取指定天数的第二天
                return current.withYear(year).withDayOfMonth(day);
            } else {
                // 指定年份，指定月份，没有指定天数（当天），获取指定月份的1日
                if (day == 0) {
                    return current.withYear(year).withMonth(month).withDayOfMonth(1);
                }
                // 指定年份，指定月份，指定天数，获取指定年月日的当天
                return current.withYear(year).withMonth(month).withDayOfMonth(day);
            }
            // 没有指定年份，获取初始日期
        } else {
            return LocalDate.of(0, Month.JANUARY.getValue(), 1);
        }
    }

    /**
     * 获得指定时间的LocalDate的结尾时刻，用下一时刻来表示
     * 传入0值，统一代表默认值，表示当前时间，传入0也代表没有指定
     * <p>传入2019/11/0，输出2019/12/1</p>
     * <p>传入2019/11/2，输出2019/11/3</p>
     * <p>传入2019/11/30，输出2019/12/1</p>
     *
     * @param year  年份
     * @param month 月份
     * @param day   天数
     * @return LocalDate
     */
    public static LocalDate getEndLocalDate(int year, int month, int day) {
        // TODO 检查年月日是否符合规则
        LocalDate current = LocalDate.now();
        if (checkYearAndMonthAndDay(year, month, day)) {
            return current;
        }
        // 如果指定了年份
        if (year != 0) {
            // 没有指定月份或指定第12个月的
            if (month == 0) {
                // 指定年份，没有指定月份（当月），没有指定天数（当天），获取指定的第二年1月1日
                if (day == 0) {
                    return current.withYear(year + 1).withMonth(1).withDayOfMonth(1);
                }
                // 指定年份，没有指定月份（当月），指定天数，获取指定天数的第二天
                return current.withYear(year).withDayOfMonth(day).plusDays(1);
            } else {
                // 指定年份，指定月份，没有指定天数（当天），获取指定月份的第二个月的1日
                if (day == 0) {
                    return current.withYear(year).withMonth(month).plusMonths(1).withDayOfMonth(1);
                }
                // 指定年份，指定月份，指定天数，获取指定年月日的第二天
                return current.withYear(year).withMonth(month).withDayOfMonth(day).plusDays(1);
            }
            // 没有指定年份获取当前年份的后10年，因为预期写完工时间不可能高于10年
        } else {
            return current.plusYears(10);
        }

    }

    /**
     * 得到指定时间之前的时间
     *
     * @param number     指定时间数
     * @param chronoUnit 指定单位
     * @return LocalDateTime
     */
    private static LocalDateTime getLocalDateTimeBefore(int number, ChronoUnit chronoUnit) {
        return LocalDateTime.now().minus(number, chronoUnit);
    }

    /**
     * 得到指定时间之后的时间
     *
     * @param number     指定时间数
     * @param chronoUnit 指定单位
     * @return LocalDateTime
     */
    private static LocalDateTime getLocalDateTimeAfter(int number, ChronoUnit chronoUnit) {
        return LocalDateTime.now().plus(number, chronoUnit);
    }

    public static LocalDateTime getLocalDateTimeBeforeYear(int years) {
        return getLocalDateTimeBefore(years, ChronoUnit.YEARS);
    }

    public static LocalDateTime getLocalDateTimeBeforeMonth(int months) {
        return getLocalDateTimeBefore(months, ChronoUnit.MONTHS);
    }

    public static LocalDateTime getLocalDateTimeBeforeMinute(int minutes) {
        return getLocalDateTimeBefore(minutes, ChronoUnit.MINUTES);
    }


    public static long getCurrentLongTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }




    /**
     * 得到当前的时间戳
     *
     * @return 当前时间戳
     */
    private static long getCurrentTimeStampValue() {
        return System.currentTimeMillis();
    }

    public static Long getMinuteAfterTimeStampValue(int minute) {
        return getCurrentTimeStampValue() + 60 * minute * 1000L;
    }

    public static LocalDateTime getLocalDateTimeAfterMinute(int minutes) {
        return getLocalDateTimeAfter(minutes, ChronoUnit.MINUTES);
    }

    public static long getSpecificLongFromCurrentTime(int months, int days, int hours, int minutes, boolean before) {
        return before ? getCurrentLongTimeStamp() - 2592000L * months - 86400 * days - 3600 * hours - 60 * minutes :
                getCurrentLongTimeStamp() + 2592000L * months + 86400 * days + 3600 * hours + 60 * minutes;
    }

    /**
     * 得到当前时间的之后或者之前指定的时间
     *
     * @param months  month
     * @param days    day
     * @param hours   hour
     * @param minutes minute
     * @param before  true代表之前，false代表之后
     * @return 返回指定的LocalDateTime
     */
    public static LocalDateTime getSpecificLocalDateTimeFromCurrentTime(int months, int days, int hours, int minutes, boolean before) {
        return parseTimeStampToLocalDateTime(getSpecificLongFromCurrentTime(months, days, hours, minutes, before));
    }

    public static Long getMonthBeforeTimeStampValue(int month) {
        return getCurrentLongTimeStamp() - 2592000L * month;
    }
}
