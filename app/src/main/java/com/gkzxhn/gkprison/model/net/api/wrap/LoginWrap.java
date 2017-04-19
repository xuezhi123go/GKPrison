package com.gkzxhn.gkprison.model.net.api.wrap;

import com.gkzxhn.gkprison.model.net.api.LoginService;
import com.gkzxhn.gkprison.model.net.bean.UserInfo;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author: Huang ZN
 * Date: 2016/12/22
 * Email:943852572@qq.com
 * Description:
 */

public class LoginWrap {

    private static LoginWrap instance;

    public static LoginWrap getInstance(){
        if (instance == null)
            instance = new LoginWrap();
        return instance;
    }

    /**
     * 获取验证码
     * @param body json
     * @param observer
     * @return
     */
    public Subscription sendVerifyCode(LoginService loginService,
            RequestBody body, SimpleObserver<ResponseBody> observer){
        return loginService.getVerificationCode(body)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 普通个人用户登录
     * @param service
     * @param body
     * @param observer
     * @return
     */
    public Subscription loginPersonal(LoginService service,
            RequestBody body, SimpleObserver<UserInfo> observer){
        return service.loginPersonAccount(body)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 判断验证码是否正确
     * @param service
     * @param body
     * @param observer
     * @return
     */
    public Subscription checkVerifyCode(LoginService service,
         RequestBody body, SimpleObserver<ResponseBody> observer){
        return service.judgeVerificationCode(body)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 注册
     * @param service
     * @param body
     * @param observer
     * @return
     */
    public Subscription register(LoginService service,
        RequestBody body, SimpleObserver<ResponseBody> observer){
        return service.register(body)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
