package com.gkzxhn.gkprison.utils.NomalUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by huangzhengneng on 2016/1/21.
 */
public class StringUtils {

    private static final String TAG = StringUtils.class.getName();

    /**
     * 格式化时间
     * @param time  时间毫秒值
     * @param pattern 格式
     * @return 返回格式化后的时间
     */
    public static String formatTime(long time, String pattern){
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(date);
    }

    /**
     * 格式化时间  默认当前系统时间
     * @param pattern 格式
     * @return 返回格式化后的时间
     */
    public static String formatTime(String pattern){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(date);
    }

    /**
     * 将格式化的时间转化为毫秒值
     * @param time 格式化后的时间
     * @param pattern 格式
     * @return 毫秒值
     * @throws ParseException
     */
    public static long formatToMill(String time, String pattern) throws ParseException{
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.parse(time).getTime();
    }

    /**
     * 判断是否是今天
     * @param mills
     * @return
     */
    public static boolean isToday(long mills){
        String today = formatTime(System.currentTimeMillis(), "yyyy-MM-dd");
        String time = formatTime(mills, "yyyy-MM-dd");
        return today.equals(time);
    }

    /**
     * 判断是否是今年
     * @param mills
     * @return
     */
    public static boolean isCurrentYear(long mills){
        String today = formatTime(System.currentTimeMillis(), "yyyy");
        String time = formatTime(mills, "yyyy");
        return today.equals(time);
    }

    /**
     * 获取数据库对象
     * @param context
     * @return
     */
    public static SQLiteDatabase getSQLiteDB(Context context){
        String path = "/data/data/com.gkzxhn.gkprison/databases/chaoshi.db";
        Log.i(TAG, path);
        return SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
    }


    /**
     * 获得两个日期的相隔时间
     * @param start
     * @param end
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static StringBuffer getTimeStringBetween (String start, String end, String pattern)throws ParseException{
        long startMills = formatToMill(start, pattern);
        long endMills = formatToMill(end, pattern);
        long time = endMills - startMills;
        int year = 0;
        int month = 0;
        int day = 0;
        final long YEAR = 365 * 24 * 60 * 60 * 1000L;
        final long MONTH = 30 * 24 * 60 * 60 * 1000L;
        final long DAY = 24 * 60 * 60 * 1000L;
        year = (int) (time / YEAR);
        month = (int) ((time - (year * YEAR)) / MONTH);
        day = (int) ((time - (year * YEAR) - (month * MONTH)) / DAY);
        StringBuffer remain = new StringBuffer();
        if (year != 0) {
            remain.append(year).append("年");
        }
        if (month != 0 ) {
            remain.append(month).append("个月");
        }
        return remain;
    }

    /**
     * 根据int年月日得到String年月日
     * @param remainYear
     * @param remainMonth
     * @param remainDay
     * @return
     */
    @NonNull
    public static StringBuffer getYearMonthDay(int remainYear, int remainMonth, int remainDay) {
        StringBuffer remain = new StringBuffer();
        if (remainYear != 0) {
            remain.append(remainYear).append("年");
        }
        if (remainMonth != 0 ) {
            remain.append(remainMonth).append("个月");
        }
        if (remainDay != 0) {
            remain.append(remainDay).append("日");
        }
        if (remain.length() == 0){
            remain.append("undefind");
        }
        return remain;
    }

    /**
     * 到time还有多长时间
     * @param time
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static long apartTime(String time, String pattern) throws ParseException{
        long timeMills = formatToMill(time, pattern);
        return timeMills - System.currentTimeMillis();
    }

    public static String formatMills2Days(long timeMills) throws ParseException{
        if (timeMills > 0) {
            StringBuffer timeString = new StringBuffer();
            long day = timeMills / 24 / 60 / 60 / 1000;
            if (day > 0) {
                return timeString.append(day).append("天").toString();
            }else {
                long hours = timeMills / 60 / 60 / 1000;
                return timeString.append(hours).append("小时").toString();
            }
        }else {
            return "";
        }

    }
}
