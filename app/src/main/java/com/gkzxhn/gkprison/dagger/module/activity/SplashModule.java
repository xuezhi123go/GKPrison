package com.gkzxhn.gkprison.dagger.module.activity;

import com.gkzxhn.gkprison.ui.activity.SplashActivity;

import dagger.Module;

/**
 * Author: Huang ZN
 * Date: 2016/12/21
 * Email:943852572@qq.com
 * Description:
 */
@Module
public class SplashModule {

    private SplashActivity mActivity;

    public SplashModule(SplashActivity splashActivity){
        this.mActivity = splashActivity;
    }
}
