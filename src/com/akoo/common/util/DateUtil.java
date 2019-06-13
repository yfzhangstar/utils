package com.akoo.common.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class DateUtil {

    /**
     * 月的毫秒数
     */
    public static long MONTH_MILLIS = 30 * 24 * 60 * 60 * 1000L;
    /**
     * 星期的毫秒数
     */
    public static long WEEK_MILLIS = 7 * 24 * 60 * 60 * 1000L;
    /**
     * 天的毫秒
     */
    public static long DAY_MILLIS = 24 * 60 * 60 * 1000L;
    /**
     * 小时的毫秒数
     */
    public static long HOUR_MILLIS = 60 * 60 * 1000L;
    /**
     * 分钟的毫秒数
     */
    public static long MIN_MILLIS = 60 * 1000L;

    public static long SECONDS_MILLIS = 1000L;
    /**
     * 时区偏移
     */
    public static long TIME_ZONE_OFFSET = 8;

    /**
     * 取得当前小时
     */
    public static long getCurrentHour() {
        return LocalTime.now().getHour();
    }
    
    /**
     * 取得当前分钟
     */
    public static long getCurrentMinute() {
        return LocalTime.now().getMinute();
    }

    /**
     * 取得第二天的0点的毫秒
     */
    public static long getTomorrowMillis() {
        return System.currentTimeMillis()
                - (System.currentTimeMillis() % DateUtil.DAY_MILLIS)
                + DAY_MILLIS - DateUtil.TIME_ZONE_OFFSET * HOUR_MILLIS;
    }

    /**
     * 获取当前日期，根据当前时 返回格式：yyyy-MM-dd HH:mm:ss
     */
    public static Date getNowDate() {
        SimpleDateFormat simpledateformat = NDateUtil.sdf2.get();
        String s = simpledateformat.format(new Date());
        return simpledateformat.parse(s, new ParsePosition(0));
    }

    public static String getDataString(String s) {
        if (s.length() >= 19)
            return s.substring(0, 19);
        else
            return s;
    }

    /**
     * 将字符串转换为日期，返回日期格式 返回格式：yyyy-MM-dd HH:mm:ss
     */
    public static Date stringToDate(String s) {
        try {
            return NDateUtil.sdf2.get().parse(s);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取当前日期，并转换为字符串 返回格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
        return NDateUtil.sdf2.get().format(new Date()
                .getTime());
    }

    /**
     * 将以秒为单位的时间转换为日期，并以字符串形式返回 返回格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate(long time) {
        return NDateUtil.sdf2.get().format(time);
    }

    public static String getShortStringDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date().getTime());
    }

    /**
     * 将以秒为单位的时间转换为日期，并以字符串形式返回 返回格式：yyyy-MM-dd
     */
    public static String getShortStringDate(long time) {
        return NDateUtil.sdf2.get().format(time);
    }

    /**
     * 将日期转换为字符 返回格式：yyyy-MM-dd HH:mm:ss
     */
    public static String dateToString(Date date) {
        return NDateUtil.sdf2.get().format(date);
    }

    /**
     * getNewDateForDay 描述：获得多少天之后的日期，并转换为String类型
     *
     * @param day 天
     * @return 返回格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getNewDateForDay(int day) {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_YEAR, day);
        return DateUtil.dateToString(cal.getTime());
    }


    public static String getNewDateForDay(String oldDateStr, int day) {
        Calendar cal = new GregorianCalendar();
        Date oldDate = stringToDate(oldDateStr);
        cal.setTime(oldDate);
        cal.add(Calendar.DAY_OF_YEAR, day);
        return DateUtil.dateToString(cal.getTime());
    }

    /**
     * getNewDateForHour 描述：获得多少小时之后的日期，并转换为String类型
     *
     * @param hour 小时
     * @return 返回格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getNewDateForHour(int hour) {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.HOUR_OF_DAY, hour);
        return DateUtil.dateToString(cal.getTime());
    }

    public static String getNewDateForHour(String oldDateStr, int hour) {
        Calendar cal = new GregorianCalendar();
        Date oldDate = stringToDate(oldDateStr);
        cal.setTime(oldDate);
        cal.add(Calendar.HOUR_OF_DAY, hour);
        return DateUtil.dateToString(cal.getTime());
    }

    /**
     * getNewDateForMinute 描述：获得多少分钟之后的日期，并转换为String类型
     *
     * @param minute 分钟
     * @return 返回格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getNewDateForMinute(int minute) {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.MINUTE, minute);
        return DateUtil.dateToString(cal.getTime());
    }

    /**
     * getNewDateForMinute 描述：获得多少分钟之后的日期，并转换为String类型
     *
     * @param minute 分钟
     * @return 返回格式：yyyy-MM-dd HH:mm:ss
     */
    public static long getNewTimeForMinute(int minute) {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.MINUTE, minute);
        return cal.getTime().getTime();
    }

    /**
     * 获得oldDate后minute分钟的日
     */
    public static String getNewDateForMinute(String oldDateStr, int minute) {
        Calendar cal = new GregorianCalendar();
        Date oldDate = stringToDate(oldDateStr);
        cal.setTime(oldDate);
        cal.add(Calendar.MINUTE, minute);
        return DateUtil.dateToString(cal.getTime());
    }

    /**
     * 获得oldDate后minute分钟的日
     */
    public static String getNewDateForMinute(Date oldDate, int minute) {
        Calendar.getInstance();
        Calendar cal = new GregorianCalendar();
        cal.setTime(oldDate);
        cal.add(Calendar.MINUTE, minute);
        return DateUtil.dateToString(cal.getTime());
    }

    /**
     * getNewDateForSecond 描述：获得多少秒之后的日期，并转换为String类型
     *
     * @param second 秒
     * @return 返回格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getNewDateForSecond(int second) {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.SECOND, second);
        return DateUtil.dateToString(cal.getTime());
    }

    /**
     * 获得oldDate后minute秒的日期
     */
    public static String getNewDateForSecond(String oldDateStr, int second) {
        Calendar cal = new GregorianCalendar();
        Date oldDate = stringToDate(oldDateStr);
        cal.setTime(oldDate);
        cal.add(Calendar.SECOND, second);
        return DateUtil.dateToString(cal.getTime());
    }

    /**
     * 计算两个时间字符串的
     */
    public static Integer getMinutes(String s1, String s2) {
        Date d1 = DateUtil.stringToDate(s1);
        Date d2 = DateUtil.stringToDate(s2);
        return (int) ((d1.getTime() - d2.getTime()) / 1000 / 60);
    }

    /**
     * 计算两个时间的差值，精确到分
     */
    public static Integer getDay(String s) {
        Date d1 = new Date();
        Date d2 = DateUtil.stringToDate(s);
        return (int) ((d1.getTime() - d2.getTime()) / 1000 / 60 / 60 / 24);
    }

    public static int getDay(Date d2) {
        Date d1 = new Date();
        return (int) ((d1.getTime() - d2.getTime()) / 1000 / 60 / 60 / 24);
    }

    public static Integer getMinutes(String s) {
        Date d1 = new Date();
        Date d2 = DateUtil.stringToDate(s);
        return (int) ((d1.getTime() - d2.getTime()) / 1000 / 60);
    }

    public static Integer getMinutes(long m) {
        Date d1 = new Date();
        return (int) ((d1.getTime() - m) / 1000 / 60);
    }

    public static Integer getMinutes(Date d2) {
        Date d1 = new Date();
        return (int) ((d1.getTime() - d2.getTime()) / 1000 / 60);
    }

    /**
     * 计算两个时间的差值，精确到秒
     */
    public static Integer getSeconds(String s) {
        Date d1 = new Date();
        Date d2 = DateUtil.stringToDate(s);
        return (int) ((d1.getTime() - d2.getTime()) / 1000);
    }

    public static Integer getSeconds(String s1, String s2) {
        Date d1 = DateUtil.stringToDate(s1);
        Date d2 = DateUtil.stringToDate(s2);
        return (int) ((d1.getTime() - d2.getTime()) / 1000);
    }

    /**
     * 计算两个时间的差值，精确到秒
     */
    public static Integer getSeconds(Date d2) {
        Date d1 = new Date();
        return (int) ((d1.getTime() - d2.getTime()) / 1000);
    }

    /**
     * 比较个stringDate，是否在当前时间之前
     */
    public static boolean before(String stringDate) {
        Date now = new Date();
        return stringToDate(stringDate).before(now);
    }

    /**
     * 比较个stringDate，是否在当前时间之后
     */
    public static boolean after(String stringDate) {
        Date now = new Date();
        return stringToDate(stringDate).after(now);
    }

    /**
     * getWeekDay
     */
    public static int getWeekDay() {
        Calendar c = new GregorianCalendar();
        return c.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static int getWeekDay(Date d) {
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        return c.get(Calendar.DAY_OF_WEEK) - 1;
    }


    public static int getDaysBetween(Date startDate, Date endDate) {
        int betweenDays;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(startDate);
        c2.setTime(endDate);
        if (c1.after(c2)) { // 保证第二个时间一定大于第一个时间
            Calendar swap = c1;
            c1 = c2;
            c2 = swap;
        }
        int betweenYears = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        betweenDays = c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR);
        for (int i = 0; i < betweenYears; i++) {
            c1.add(Calendar.YEAR, i);
            betweenDays += c1.getActualMaximum(Calendar.DAY_OF_YEAR);
        }
        return betweenDays;
    }

    /**
     * 截去指定日期的时间，即日期 04:00:00
     *
     * @param time   指定日期
     * @param offset 小时
     * @return 指定日期的凌晨4点, 如果当前时间在4点之前, 则返回的是昨天的凌晨4点
     */
    public static long cutTimeTo4(long time, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < offset) calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, offset);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static boolean notSameDate(long d1, long d2, int offset) {
        LocalTime now = LocalTime.now();
        Calendar c0 = Calendar.getInstance();
        Calendar c1 = Calendar.getInstance();
        c0.setTimeInMillis(d1);
        c1.clear();
        //noinspection MagicConstant
        c1.set(c0.get(Calendar.YEAR), c0.get(Calendar.MONTH), c0.get(Calendar.DAY_OF_MONTH), offset, 0);
        c0.setTime(c1.getTime());
        long befor, after;
        if (c1.getTime().getTime() <= d1) {
            c0.add(Calendar.DAY_OF_MONTH, 1);
            befor = c1.getTimeInMillis();
            after = c0.getTimeInMillis();
        } else {
            c0.add(Calendar.DAY_OF_MONTH, -1);
            befor = c0.getTimeInMillis();
            after = c1.getTimeInMillis();
        }
        return !(d2 >= befor && d2 < after);
    }


    public static int getSecond(long milliseconds) {
        return (int) TimeUnit.MILLISECONDS.toSeconds(milliseconds);
    }
}
