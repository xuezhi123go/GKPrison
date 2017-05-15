package com.gkzxhn.gkprison.model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.gkzxhn.gkprison.model.dao.bean.CartDao;
import com.gkzxhn.gkprison.model.dao.bean.DaoMaster;
import com.gkzxhn.gkprison.model.dao.bean.LineItemsDao;
import com.gkzxhn.gkprison.model.dao.bean.SysmsgDao;

/**
 * Created by 方 on 2017/5/15.
 */

public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db,CartDao.class,LineItemsDao.class,SysmsgDao.class);
    }
}
