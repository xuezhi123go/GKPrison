package com.gkzxhn.gkprison.dagger.contract;

import com.gkzxhn.gkprison.base.BasePresenter;
import com.gkzxhn.gkprison.base.BaseView;

/**
 * Author: Huang ZN
 * Date: 2016/12/28
 * Email:943852572@qq.com
 * Description:
 */

public interface MainContract {

    interface View extends BaseView {

        /**
         * 显示进度条对话框
         */
        void showProgress(String msg);

        /**
         * 隐藏进度条对话框
         */
        void dismissProgress();

        /**
         * toast
         * @param msg
         */
        void showToast(String msg);

        /**
         * 未获取到用户信息重新登录
         */
        void reLoginNotGetUserInfo();

        /**
         * 获取用户信息成功
         */
        void getUserInfoSuccess();

        /**
         * 无账号快捷登录
         */
        void fastLoginWithoutAccount();

        /**
         * 云信账号被挤下线
         */
        void accountKickout();

    }


    interface Presenter extends BasePresenter<View> {

        /**
         * 进入主页检查相关状态
         */
        void checkStatus();

        /**
         * 下载头像
         * @param path
         */
        void downloadAvatar(String path);
    }
}
