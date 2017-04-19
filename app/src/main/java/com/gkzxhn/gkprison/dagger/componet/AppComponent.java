package com.gkzxhn.gkprison.dagger.componet;

import android.content.Context;

import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.dagger.module.AppModule;
import com.gkzxhn.gkprison.dagger.module.DBModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Author: Huang ZN
 * Date: 2016/12/20
 * Email:943852572@qq.com
 * Description:AppComponent
 */

@Singleton
@Component(modules = {AppModule.class, DBModule.class})
public interface AppComponent {

    Context getContext();

    void inject(MyApplication mApplication);

    void inject(BaseActivityNew mBaseActivity);
}
