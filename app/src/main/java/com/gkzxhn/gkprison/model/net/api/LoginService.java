package com.gkzxhn.gkprison.model.net.api;

import com.gkzxhn.gkprison.model.net.bean.UserInfo;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * author:huangzhengneng
 * email:943852572@qq.com
 * date: 2016/8/4.
 * function:登录service
 */

public interface LoginService {

    /**
     * 获取验证码
     * @param body
     * @return
     */
    @POST("request_sms")
    Observable<ResponseBody> getVerificationCode(
            @Body RequestBody body
    );

    /**
     * 个人用户登录
     * @param body
     * @return
     */
    @Headers("Content-Type: application/json")
    @POST("login")
    Observable<UserInfo> loginPersonAccount(
            @Body RequestBody body
    );

    /**
     * 判断验证码
     * @param body
     * @return
     */
    @POST("verify_code")
    Observable<ResponseBody> judgeVerificationCode(
            @Body RequestBody body
    );

    /**
     * 注册
     * @param body
     * @return
     */
    @POST("registrations")
    Observable<ResponseBody> register(
            @Body RequestBody body
    );
}
