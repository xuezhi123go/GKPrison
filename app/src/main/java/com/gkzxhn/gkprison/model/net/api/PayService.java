package com.gkzxhn.gkprison.model.net.api;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Author: Huang ZN
 * Date: 2017/1/9
 * Email:943852572@qq.com
 * Description:
 */

public interface PayService {

    /**
     * 发送支付方式至服务器
     * @param map
     * @param body
     * @return
     */
    @POST("prepay")
    Observable<ResponseBody> sendPaymentType(
            @HeaderMap Map<String, String> headers,
            @QueryMap Map<String, String> map,
            @Body RequestBody body);

    /**
     * 微信支付订单信息  通知服务器
     * @param body
     * @return
     */
    @PATCH("payment_status")
    Observable<ResponseBody> sendWXPayOrder(
            @HeaderMap Map<String, String> headers,
            @Body RequestBody body
    );
}
