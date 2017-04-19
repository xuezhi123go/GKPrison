package com.gkzxhn.gkprison.dagger.contract;

import com.gkzxhn.gkprison.base.BasePresenter;
import com.gkzxhn.gkprison.base.BaseView;

/**
 * Author: Huang ZN
 * Date: 2016/12/22
 * Email:943852572@qq.com
 * Description:
 */

public interface LoginContract {

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
         * 开始倒计时
         */
        void startCountDown();

        /**
         * 移除倒计时任务
         */
        void removeCountDown();

        /**
         * 进入下一个页面
         */
        void toNextPage();
    }

    interface Presenter extends BasePresenter<View> {

        /**
         * 登录
         * @param isCommonUser
         * @param str
         * @param json_str
         */
        void login(boolean isCommonUser, String str, String json_str);

        /**
         * 发送验证码
         * @param content
         */
        void sendVerifyCode(String content);

        /**
         * 检查输入框文本
         * @param editTexts
         * @return
         */
        boolean checkInputText(String... editTexts);
    }
}
