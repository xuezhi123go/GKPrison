package com.gkzxhn.gkprison.dagger.componet.activity;

import com.gkzxhn.gkprison.base.PerActivity;
import com.gkzxhn.gkprison.dagger.componet.AppComponent;
import com.gkzxhn.gkprison.dagger.module.activity.ActivityModule;
import com.gkzxhn.gkprison.dagger.module.activity.SplashModule;
import com.gkzxhn.gkprison.ui.activity.SplashActivity;

import dagger.Component;

/**
 * Author: Huang ZN
 * Date: 2016/12/21
 * Email:943852572@qq.com
 * Description:
 */
@PerActivity
@Component(dependencies = AppComponent.class, modules = {
        ActivityModule.class, SplashModule.class})
public interface SplashComponent {

    void inject(SplashActivity mSplashActivity);
}
