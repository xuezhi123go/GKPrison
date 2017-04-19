package com.gkzxhn.gkprison.dagger.contract;

import android.graphics.Bitmap;

import com.gkzxhn.gkprison.base.BasePresenter;
import com.gkzxhn.gkprison.base.BaseView;

import java.util.Map;

/**
 * Author: Huang ZN
 * Date: 2016/12/26
 * Email:943852572@qq.com
 * Description:
 */

public interface RegisterContract {

    interface View extends BaseView {
        /**
         * 显示主ui
         */
        void showMainUi();

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
         * 判断验证码成功  即验证码正确
         */
        void checkVerifyCodeSuccess();

        /**
         * 显示操作结果对话框
         * @param isSuccess 是否操作成功
         * @param msg
         */
        void showResultDialog(boolean isSuccess, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        /**
         * 发送验证码
         * @param editText
         */
        void sendVerifyCode(String editText);

        /**
         * 注册
         * @param map  监狱名称id键值对map
         * @param avatar  头像位图
         * @param id_01  身份证正反面
         * @param id_02   不分顺序
         * @param contents 其它输入框内容
         */
        void register(Map<String, Integer> map, Bitmap avatar, Bitmap id_01, Bitmap id_02, String... contents);

        /**
         * 判断验证码是否正确
         * @param phone_num
         * @param code
         */
        void checkVerifyCode(String phone_num, String code);

        /**
         * 检查完整性
         * @param avatar  头像位图
         * @param id_01  身份证正反面
         * @param id_02   不分顺序
         * @param contents 其它输入框内容
         */
        boolean checkInput(Bitmap avatar, Bitmap id_01, Bitmap id_02, String... contents);
    }
}
