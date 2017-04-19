package com.gkzxhn.gkprison.model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhangjia on 16/5/10.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static SQLiteHelper mInstance;

    /**
     * 初始化
     * @param context
     */
    public static void init(Context context){
        getInstance(context).getReadableDatabase();
    }

    /**
     * 获取实例
     * @param context
     * @return
     */
    public static SQLiteHelper getInstance(Context context){
        if (mInstance == null)
            mInstance = new SQLiteHelper(context);
        return mInstance;
    }

    public SQLiteHelper(Context context){
        super(context, "chaoshi.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table CartInfo(id INTEGER PRIMARY KEY autoincrement,time VARCHAR2(60),out_trade_no VARCHAR2(60),isfinish BOOLEAN(30),total_money VARCHAR2(60),count INTEGER(60),remittance BOOLEAN(30),payment_type varchar(255))");
        db.execSQL("CREATE TABLE line_items(id INTEGER PRIMARY KEY autoincrement,Items_id INTEGER(60),cart_id INTEGER,qty INTEGER(60),total_money VARCHAR2(60),position INTEGER(60),price varchar[255],title varchar(255))");
//        db.execSQL("create table sysmsg(apply_date VARCHAR2,name VARCHAR2,result VARCHAR2,is_read VARCHAR2,meeting_date VARCHAR2, type_id INTEGER,reason VARCHAR2,receive_time VARCHAR2,user_id VARCHAR2)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
