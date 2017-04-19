package com.gkzxhn.gkprison.dagger.contract;

import com.gkzxhn.gkprison.base.BasePresenter;
import com.gkzxhn.gkprison.base.BaseView;

/**
 * Author: Huang ZN
 * Date: 2016/12/21
 * Email:943852572@qq.com
 * Description:
 */

public interface SplashContract {

    interface View extends BaseView {
        void showMainUi();

        void toWelCome();

        void toInputPassWord();

        void toLogin();

        void toMain();

        void toDateMeetingList();
    }

    interface Presenter extends BasePresenter<View> {

        void initDB();

        void next();
    }
}
