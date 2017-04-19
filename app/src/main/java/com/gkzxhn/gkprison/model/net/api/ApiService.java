package com.gkzxhn.gkprison.model.net.api;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import rx.Observable;

/**
 * author:huangzhengneng
 * email:943852572@qq.com
 * date: 2016/8/4.
 * function:监狱端请求service
 */

public interface ApiService {

    /**
     * 获取会见列表
     * @param username
     * @param date
     * @return
     *//*
    @GET("applies")
    Observable<List<MeetingInfo>> getMeetingList(
            @HeaderMap Map<String, String> headers,
            @Query("accid") String username,
            @Query("app_date") String date);
*/
    /**
     * 取消视频会见
     * @param id
     * @return
     */

    @PATCH("applies/{id}")
    Observable<ResponseBody> cancelMeeting(
            @HeaderMap Map<String, String> headers,
            @Path("id") int id,
            @Body RequestBody body);

    /**
     * 获取会见详情信息
     * @param id
     * @param token
     * @return
     *//*
    @GET("families/{family_id}")
    Observable<FamilyMeetingInfo> getMeetingDetailInfo(
            @HeaderMap Map<String, String> headers,
            @Path("family_id") int id,
            @Query("access_token") String token);*/
}
