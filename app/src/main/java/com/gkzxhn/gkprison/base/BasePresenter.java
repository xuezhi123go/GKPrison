package com.gkzxhn.gkprison.base;

import android.support.annotation.NonNull;

/**
 * Author: Huang ZN
 * Date: 2016/12/21
 * Email:943852572@qq.com
 * Description:
 */

public interface BasePresenter<T extends BaseView> {

    void attachView(@NonNull T view);

    void detachView();
}
