package com.gkzxhn.gkprison.model.net.api.wrap;

import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.model.net.api.ApiRequest;
import com.gkzxhn.gkprison.model.net.bean.CategoriesInfo;
import com.gkzxhn.gkprison.model.net.bean.CommonResult;
import com.gkzxhn.gkprison.model.net.bean.NewsResult;
import com.gkzxhn.gkprison.model.net.bean.PrisonerUserInfo;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author: Huang ZN
 * Date: 2016/12/28
 * Email:943852572@qq.com
 * Description:主页面相关网络操作简单封装
 */

public class MainWrap {

    /**
     * 获取囚犯信息
     * @param request
     * @param observer
     * @return
     */
    public static Subscription getPrisonerUserInfo(ApiRequest request,
                                                   Map<String, String> header,String f_id, Observer<PrisonerUserInfo> observer){
        return request.getUserInfo(header,f_id)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 申请退款
     * @param request
     * @param header
     * @param observer
     * @return
     */
    public static Subscription drawbacks(ApiRequest request,
                                                   Map<String, String> header, Observer<CommonResult> observer){
        return request.drawbacks(header)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 获取商品分类
     * @param request
     * @param header
     * @param observer
     * @return
     */
    public static Subscription getCategories(ApiRequest request,
                                         Map<String, String> header, Observer<CategoriesInfo> observer){
        return request.getCategories(header)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 主页的获取新闻
     * @param jail_id
     * @param observer
     * @return
     */
    public static Subscription getMainNews(int jail_id, Observer<NewsResult> observer){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiRequest apiRequest = retrofit.create(ApiRequest.class);
        String token = (String) SPUtil.get(MyApplication.getContext(), SPKeyConstants.ACCESS_TOKEN, "");
        Map<String, String> header = new HashMap<>();
        header.put("authorization", token);
        return apiRequest.getNews(jail_id)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 提交奔溃日志
     * @param body
     * @param observer
     * @return
     */
    public static Subscription submitLogs(RequestBody body, Observer<ResponseBody> observer){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiRequest apiRequest = retrofit.create(ApiRequest.class);
        return apiRequest.submitLogs(body)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(observer);
    }

    public static Subscription downloadFile(String range, String url, Observer<ResponseBody> observer){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        ApiRequest apiRequest = retrofit.create(ApiRequest.class);
        return apiRequest.downloadFile(range, url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(observer);
    }
}
