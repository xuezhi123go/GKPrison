package com.gkzxhn.gkprison.model.dao;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.model.dao.bean.DaoMaster;
import com.gkzxhn.gkprison.model.dao.bean.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by 方 on 2017/4/12.
 */

public class GreenDaoHelper {
    private static DaoMaster.DevOpenHelper mHelper;
    private static SQLiteDatabase db;
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;

    /**
     * 初始化greenDao，这个操作建议在Application初始化的时候添加；
     */
    public static void initDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(MyApplication.getContext(), "chaoshi.db", null){
            @Override
            public void onUpgrade(Database db, int oldVersion, int newVersion) {
                Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
                switch (oldVersion) {
                    case 1: //不能先删除表，否则数据都木了//
                        onCreate(db);
                        db.execSQL("ALTER TABLE 'cart' ADD 'barcode' TEXT;");
                        break;
                }
            }
        };
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }
    public static DaoSession getDaoSession() {
        return mDaoSession;
    }
    public static SQLiteDatabase getDb() {
        return db;
    }

}
