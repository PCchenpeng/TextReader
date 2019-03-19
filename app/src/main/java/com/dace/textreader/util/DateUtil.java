package com.dace.textreader.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间格式转换
 * Created by 70391 on 2017/8/13.
 */

public class DateUtil {

    /**
     * 获取当前时间的时间戳
     *
     * @return
     */
    public static String getTime() {
        long time = System.currentTimeMillis();
        return String.valueOf(time);
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getTodayDateTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm",
                Locale.getDefault());
        return format.format(new Date());
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getTodayDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        return format.format(new Date());
    }

    /**
     * 获取时间差
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static String getTimeDiff_(String startTime, String endTime) {
        String timeStr;
        long start = getTimeMillis(startTime);
        long end = getTimeMillis(endTime);
        long dur = end - start; //获取时间差（毫秒）

        long year = dur / (365 * 24 * 60 * 60 * 1000);
        long month = dur / (30 * 24 * 60 * 60 * 1000);
        long day = dur / (24 * 60 * 60 * 1000);
        long hour = dur / (60 * 60 * 1000);
        long m = dur / (60 * 1000);

        if (year >= 1) {
            timeStr = startTime;
        } else if (month >= 1) {
            timeStr = startTime;
        } else if (day >= 1) {
            timeStr = day + "天前";
        } else if (hour >= 1) {
            timeStr = hour + "小时前";
        } else {
            if (m < 1) {
                timeStr = "刚刚";
            } else {
                timeStr = m + "分钟前";
            }
        }

        return timeStr;
    }

    /**
     * 是否是一天前
     *
     * @return
     */
    public static boolean isOneDayBefore(String startTime, String endTime) {
        boolean isOneDayBefore = false;
        int startY = Integer.valueOf(startTime.split("-")[0]);
        int endY = Integer.valueOf(endTime.split("-")[0]);
        if (endY - startY >= 1) {
            isOneDayBefore = true;
        } else {
            int startM = Integer.valueOf(startTime.split("-")[1]);
            int endM = Integer.valueOf(endTime.split("-")[1]);
            if (endM - startM >= 1) {
                isOneDayBefore = true;
            } else {
                int startD = Integer.valueOf(startTime.split("-")[2]);
                int endD = Integer.valueOf(endTime.split("-")[2]);
                if (endD - startD >= 1) {
                    isOneDayBefore = true;
                }
            }
        }
        return isOneDayBefore;
    }

    /**
     * 获取时间的毫秒数
     *
     * @param strTime
     * @return
     */
    public static long getTimeMillis(String strTime) {
        long returnMillis = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d = null;
        try {
            d = sdf.parse(strTime);
            returnMillis = d.getTime();
        } catch (ParseException e) {
        }
        return returnMillis;
    }

    /**
     * 把时间转换成分秒
     *
     * @param currentPosition
     * @return
     */
    public static String formatterTime(int currentPosition) {
        SimpleDateFormat sdateformat = new SimpleDateFormat("mm:ss");
        String format = sdateformat.format(new Date(currentPosition));
        return format;
    }

    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014-06-14  16:09:00"）
     *
     * @param time
     * @return
     */
    public static String timedate(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        String times = sdr.format(new Date(lcc));
        return times;

    }

    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日"）
     *
     * @param time
     * @return
     */
    public static String time2YMD(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        String times = sdr.format(new Date(lcc));
        return times;

    }

    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日"）
     *
     * @param time
     * @return
     */
    public static String time2MD(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("MM-dd");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        String times = sdr.format(new Date(lcc));
        return times;
    }

    public static String date2YMD(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy/MM/dd");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        String times = sdr.format(new Date(lcc));
        return times;
    }

    /**
     * 规范时间
     *
     * @param time
     * @return
     */
    public static String time2Format(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        String times = sdr.format(new Date(lcc));
        String today = getTodayDateTime();
        String year = today.split("-")[0];
        if (times.contains(year)) {
            String y = year + "-";
            return times.split(y)[1];
        } else {
            return times;
        }
    }

    /**
     * @param time
     * @return
     */
    public static String timeslashData(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        String times = sdr.format(new Date(lcc));
        return times;
    }

    public static String timeYMD(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        @SuppressWarnings("unused")

        long lcc = Long.valueOf(time);
        String times = sdr.format(new Date(lcc));
        return times;
    }

    /**
     * 获取结束时间与当前时间的倒计时
     *
     * @param time
     * @return
     */
    public static String getCountDownTime(String time) {
        String diff;

        Long start = System.currentTimeMillis();
        Long end = Long.valueOf(time);

        int diff_ = (int) (end - start);
        int m = diff_ / (60 * 1000);
        diff = String.valueOf(m);
        return diff;
    }

}
