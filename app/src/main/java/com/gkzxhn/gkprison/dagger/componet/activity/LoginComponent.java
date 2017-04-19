package com.gkzxhn.gkprison.dagger.componet.activity;

import com.gkzxhn.gkprison.base.PerActivity;
import com.gkzxhn.gkprison.dagger.componet.AppComponent;
import com.gkzxhn.gkprison.dagger.module.ApiModule;
import com.gkzxhn.gkprison.dagger.module.activity.ActivityModule;
import com.gkzxhn.gkprison.ui.activity.LoginActivity;

import dagger.Component;

/**
 * Author: Huang ZN
 * Date: 2016/12/22
 * Email:943852572@qq.com
 * Description:
 */

@PerActivity
@Component(dependencies = AppComponent.class, modules = {ActivityModule.class, ApiModule.class})
public interface LoginComponent {
    void inject(LoginActivity activity);
}
