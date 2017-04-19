package com.keda.vconf.reqs;

import com.gkzxhn.gkprison.constant.Constants;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author: Huang ZN
 * Date: 2016/10/27
 * Email:943852572@qq.com
 * Description: 审核
 */

public class ExamReq {

    /**
     * 审核
     * @param body
     * @param subscriber
     */
    public static void exam(RequestBody body, Subscriber<ResponseBody> subscriber){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ExamService exam = retrofit.create(ExamService.class);
        exam.examID(body)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
