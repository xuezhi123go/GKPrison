package com.gkzxhn.gkprison.utils.NomalUtils;

/**
 * Created by zhengneng on 2016/3/13.
 * Log日志统一管理类
 */
public class Log {

    public static boolean isDebug = true;// 是否需要打印Log，在application的onCreate方法里初始化
    private static final String TAG = "GKPrison";// 默认TAG

    private Log(){

        /**
         * cannot be instantiated
         */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 默认tag
     */
    public static void i(String msg){
        if(isDebug)
            android.util.Log.i(TAG, msg);
    }

    public static void d(String msg){
        if (isDebug)
            android.util.Log.d(TAG, msg);
    }

    public static void e(String msg){
        if(isDebug)
            android.util.Log.e(TAG, msg);
    }

    public static void v(String msg){
        if (isDebug)
            android.util.Log.v(TAG, msg);
    }

    public static void w(String msg){
        if (isDebug)
            android.util.Log.w(TAG, msg);
    }

    /**
     * 传入tag
     */
    public static void i(String tag, String msg){
        if (isDebug)
            android.util.Log.i(tag, msg);
    }

    public static void d(String tag, String msg){
        if (isDebug)
            android.util.Log.d(tag, msg);
    }

    public static void e(String tag, String msg){
        if (isDebug)
            android.util.Log.e(tag, msg);
    }

    public static void v(String tag, String msg){
        if (isDebug)
            android.util.Log.v(tag, msg);
    }

    public static void w(String tag, String msg){
        if (isDebug)
            android.util.Log.w(tag, msg);
    }

}
