package com.gkzxhn.gkprison.base;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.gkzxhn.gkprison.dagger.componet.AppComponent;
import com.gkzxhn.gkprison.dagger.componet.DaggerAppComponent;
import com.gkzxhn.gkprison.dagger.module.AppModule;
import com.gkzxhn.gkprison.model.dao.GreenDaoHelper;
import com.gkzxhn.gkprison.utils.CustomUtils.KDInitUtil;
import com.gkzxhn.gkprison.utils.CustomUtils.NimInitUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.CrashHandler;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.gkzxhn.gkprison.widget.service.RecordService;

/**
 * Created by 方 on 2017/4/18.
 */

public class MyApplication extends MultiDexApplication {

    private static final String TAG = MyApplication.class.getSimpleName();
    public static MyApplication mOurApplication;// application实例
    private AppComponent mAppComponent;

    public static Context getContext() {
        return mOurApplication.getApplicationContext();
    }

    public static MyApplication getApplication() {
        return mOurApplication;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mOurApplication = this;
        GreenDaoHelper.initDatabase(); //初始化数据库
        initComponent();// 初始化组件
        NimInitUtil.initNim();// 云信SDK相关初始化及后续操作
        KDInitUtil.init();// 科达SDK相关初始化及后续操作
        ToastUtil.registerContext(this);
//        LeakCanary.install(this);
        CrashHandler.getInstance().init(mOurApplication);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            startService(new Intent(this, RecordService.class));
            Log.i(TAG, "record service already start");
        }
    }

    /**
     * 初始化组件
     */
    private void initComponent() {
        mAppComponent = DaggerAppComponent.builder().appModule(
                new AppModule(this)).build();
        mAppComponent.inject(this);
    }

    /**
     * 获取appComponent
     * @return
     */
    public AppComponent getAppComponent(){
        return mAppComponent == null ? null : mAppComponent;
    }

    private boolean ifAppear;

    //the App disappear into background
    public void appDisappear(){
        ifAppear = true;
    }

    //the App appear onto foreground
    public void appAppear(){
        ifAppear = false;
    }

    // if the app in to foreground
    public boolean ifAppear(){
        return ifAppear;
    }

}
