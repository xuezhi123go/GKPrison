package com.gkzxhn.gkprison.base;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Author: Huang ZN
 * Date: 2016/12/20
 * Email:943852572@qq.com
 * Description:自定义的与activity一样生命周期的注解
 */
@Scope @Retention(RUNTIME) public @interface PerActivity {
}
