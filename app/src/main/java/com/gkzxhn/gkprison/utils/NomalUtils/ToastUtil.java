package com.gkzxhn.gkprison.utils.NomalUtils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by zhengneng on 2016/3/13.
 * Toast统一管理类
 */
public class ToastUtil {

    public static boolean isShowToast = true;// 是否弹土司
    public static Context mContext;

    private ToastUtil(){

        /**
         * cannot be instantiated
         */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 注册上下文
     *      在application类里注册一次就不再需要穿context了
     * @param context
     */
    public static void registerContext(Context context){
        mContext = context.getApplicationContext();
    }

    /**
     * 短时间显示Toast  (3s)
     * @param msg
     */
    public static void showShortToast(CharSequence msg){
        if(isShowToast){
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 短时间显示Toast
     * @param msg
     */
    public static void showShortToast(int msg){
        if(isShowToast){
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 长时间显示Toast (5s)
     * @param msg
     */
    public static void showLongToast(CharSequence msg){
        if(isShowToast){
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 长时间显示Toast (5s)
     * @param msg
     */
    public static void showLongToast(int msg){
        if(isShowToast){
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 自定义Toast显示时常
     * @param msg
     * @param duration
     */
    public static void showToastDivTime(CharSequence msg, int duration){
        if(isShowToast){
            Toast.makeText(mContext, msg, duration).show();
        }
    }

    /**
     * 自定义Toast显示时常
     * @param msg
     * @param duration
     */
    public static void showToastDivTime(int msg, int duration){
        if(isShowToast){
            Toast.makeText(mContext, msg, duration).show();
        }
    }
}
