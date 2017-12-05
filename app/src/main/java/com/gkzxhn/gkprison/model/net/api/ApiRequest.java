package com.gkzxhn.gkprison.model.net.api;

import com.gkzxhn.gkprison.model.net.bean.AwardPunishInfo;
import com.gkzxhn.gkprison.model.net.bean.Balances;
import com.gkzxhn.gkprison.model.net.bean.CategoriesInfo;
import com.gkzxhn.gkprison.model.net.bean.CommonResult;
import com.gkzxhn.gkprison.model.net.bean.FamilyServerBean;
import com.gkzxhn.gkprison.model.net.bean.Laws;
import com.gkzxhn.gkprison.model.net.bean.NewsResult;
import com.gkzxhn.gkprison.model.net.bean.PrisonerDetail;
import com.gkzxhn.gkprison.model.net.bean.PrisonerOrders;
import com.gkzxhn.gkprison.model.net.bean.PrisonerUserInfo;
import com.gkzxhn.gkprison.model.net.bean.SentenceChange;
import com.gkzxhn.gkprison.model.net.bean.VersionInfo;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * author:huangzhengneng
 * email:943852572@qq.com
 * date: 2016/8/2.
 * function:retrofit所需service
 */
public interface ApiRequest {

    /**
     * 获取用户对应囚犯信息
     * @return
     */
    @GET("families/{family_id}/prisoners")
    Observable<PrisonerUserInfo> getUserInfo(@HeaderMap Map<String, String> headers, @Path("family_id") String f_id);
    /**
     * 发送意见反馈
     * @param msg 反馈内容
     * @return
     */
    @POST("feedbacks")
    Observable<Object> sendOpinion(@HeaderMap Map<String, String> headers, @Body RequestBody msg);

    /**
     * 写信
     * @param msg 内容
     * @return
     */
    @POST("mails")
    Observable<Object> sendMessage(@HeaderMap Map<String, String> headers,
                                   @Body RequestBody msg);

    /**
     * 获取新闻
     * @param jail_id
     * @return  @GET("news")
    Observable<List<News>> getNews(@Query("jail_id") int jail_id);
     */
    @GET("jails/{jail_id}/news")
    @Headers("Content-Type: application/json")
    Observable<NewsResult> getNews(@Path("jail_id") int jail_id);

    /**
     * 获取剩余可会见次数
     * @param f_id
     * @return
     */
    @GET("families/{family_id}/balances")
    Observable<Balances> getBalance(@HeaderMap Map<String, String> headers, @Path("family_id") int f_id);

    /**
     * 发送远程会见申请
     * @param body
     * @return
     */
    @POST("meetings")
    Observable<ResponseBody> sendMeetingRequest(@HeaderMap Map<String, String> headers,
                                                @Body RequestBody body);

    /**
     * 获取订单信息
     * @param jail_id
     * @param token
     * @param body
     * @return
     */
    @POST("orders")
    Observable<ResponseBody> getOrderInfo(@HeaderMap Map<String, String> headers,
                                          @Query("jail_id") int jail_id,
                                          @Query("access_token") String token,
                                          @Body RequestBody body
    );

    /**
     * 获取商品
     * @param map
     * @return
     */
    @GET("items")
    Observable<ResponseBody> getCommodities(@HeaderMap Map<String, String> headers,
                                            @QueryMap Map<String, String> map
    );

    /**
     * 获取家属服务信息
     * @param token
     * @return
     */
    @GET("services")
    Observable<FamilyServerBean> getFamilyServerInfo(@HeaderMap Map<String, String> headers,
                                                     @Query("access_token") String token
    );

    /**
     * 获取法律法规列表
     * @param jail_id
     * @return
     */
    @GET("jails/{jail_id}/laws")
    Observable<Laws> getLawsAndRegulationsList(@Path("jail_id") int jail_id
    );

    /**
     * 申请退款
     * @param headers
     * @return
     */
    @POST("drawbacks")
    Observable<CommonResult> drawbacks(
            @HeaderMap Map<String, String> headers
    );

    /**
     * 获取最新版本
     * @return
     */
    @GET("versions/1")
    @Headers("Content-Type: application/json")
    Observable<VersionInfo> versions();

    /**
     * 获取商品分类
     * @param headers
     * @return
     */
    @GET("categories")
    Observable<CategoriesInfo> getCategories(@HeaderMap Map<String, String> headers);

    /**
     * 获取会见列表
     * @param headers
     * @param f_id
     * @return
     */
    @GET("families/{family_id}/meetings")
    @Headers("Content-Type: application/json")
    Observable<ResponseBody> getMeetings(@HeaderMap Map<String, String> headers, @Path("family_id") int f_id);

    /**
     * 提交奔溃日志
     * @param body
     * @return
     */
    @POST("loggers")
    @Headers("Content-Type: application/json")
    Observable<ResponseBody> submitLogs(@Body RequestBody body);

    /**
     * 下载
     * @param range
     * @param url
     * @return
     */
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Header("Range") String range, @Url String url);

    /**
     * 获取罪犯的订单列表
     * @return
     */
    @GET("families/{family_id}/prisoner_orders")
    Observable<PrisonerOrders> getPrisonerOrders(@HeaderMap Map<String, String> headers, @Path("family_id") int familyId);

    /**
     * 获取罪犯的刑期变动信息
     * @return
     * @param prisonerId
     */
    @GET("prisoners/{prisoner_id}/prisoner_terms")
    Observable<SentenceChange> getSentenceChange(@Path("prisoner_id") long prisonerId);


    /**
     * 获取用户对应囚犯详细信息
     * @return
     * @param prisoner_id
     */
    @GET("prisoners/{prisoner_id}/detail")
    Observable<PrisonerDetail> getPrisonerDetail(@Path("prisoner_id") long prisoner_id);

    /**
     * 获取用户对应囚犯奖惩信息
     * @return
     */
    @GET("prisoners/{prisoner_id}/prisoner_reward_punishment")
    Observable<AwardPunishInfo> getAwardPunish(@Path("prisoner_id") long prisoner_id);
}
