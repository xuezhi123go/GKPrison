package com.gkzxhn.gkprison.utils.NomalUtils;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by zhengneng on 2016/3/13.
 * SharedPreferences封装工具类
 */
public class SPUtil {

    /**
     * sp保存的文件名
     */
    private static final String FILE_NAME = "config";
    private static final String FILE_NAME_2 = "pres_status";

    /**
     * 保存数据的方法，可传入String,int,boolean,float,long
     *        其它类型均以字符串形式保存其toString()的结果
     * @param context
     * @param key
     * @param object
     */
    public static void put(Context context, String key, Object object){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (null == object) {
            return;
        }
        if(object instanceof  String){
            editor.putString(key, (String) object);
        }else if(object instanceof Integer){
            editor.putInt(key, (Integer) object);
        }else if(object instanceof Boolean){
            editor.putBoolean(key, (Boolean) object);
        }else if(object instanceof Float){
            editor.putFloat(key, (Float) object);
        }else if(object instanceof Long){
            editor.putLong(key, (Long) object);
        }else {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 保存数据的方法，可传入String,int,boolean,float,long
     *        其它类型均以字符串形式保存其toString()的结果
     * @param context
     * @param key
     * @param object
     */
    public static void putCanNotClear(Context context, String key, Object object){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME_2,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if(object instanceof  String){
            editor.putString(key, (String) object);
        }else if(object instanceof Integer){
            editor.putInt(key, (Integer) object);
        }else if(object instanceof Boolean){
            editor.putBoolean(key, (Boolean) object);
        }else if(object instanceof Float){
            editor.putFloat(key, (Float) object);
        }else if(object instanceof Long){
            editor.putLong(key, (Long) object);
        }else {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 获取对应的sp值
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(Context context, String key, Object defaultObject){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        if(defaultObject instanceof String){
            return sp.getString(key, (String) defaultObject);
        }else if(defaultObject instanceof Integer){
            return sp.getInt(key, (Integer) defaultObject);
        }else if(defaultObject instanceof Float){
            return sp.getFloat(key, (Float) defaultObject);
        }else if(defaultObject instanceof Long){
            return sp.getLong(key, (Long) defaultObject);
        }else if(defaultObject instanceof Boolean){
            return sp.getBoolean(key, (Boolean) defaultObject);
        }
        return null;
    }

    /**
            * 获取对应的sp值
    * @param context
    * @param key
    * @param defaultObject
    * @return
            */
    public static Object getCanNotClear(Context context, String key, Object defaultObject){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME_2,
                Context.MODE_PRIVATE);
        if(defaultObject instanceof String){
            return sp.getString(key, (String) defaultObject);
        }else if(defaultObject instanceof Integer){
            return sp.getInt(key, (Integer) defaultObject);
        }else if(defaultObject instanceof Float){
            return sp.getFloat(key, (Float) defaultObject);
        }else if(defaultObject instanceof Long){
            return sp.getLong(key, (Long) defaultObject);
        }else if(defaultObject instanceof Boolean){
            return sp.getBoolean(key, (Boolean) defaultObject);
        }
        return null;
    }

    /**
     * 移除某个key所对应的值
     * @param context
     * @param key
     */
    public static void remove(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     * @param context
     */
    public static void clear(Context context){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key值是否存在
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 获取所有已存在的sp键值对
     * @param context
     * @return
     */
    public static Map<String, ?> getAllSp(Context context){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author hzn
     */
    private static class SharedPreferencesCompat{

        private static final Method sApplyMethod = findApplyMethod();


        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            Class clz = SharedPreferences.Editor.class;
            try {
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 如果找到则使用apply执行, 否则用commit
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor){
            try {
                if(sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            editor.commit();
        }
    }

}
