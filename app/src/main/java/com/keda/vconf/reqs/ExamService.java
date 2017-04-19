package com.keda.vconf.reqs;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Author: Huang ZN
 * Date: 2016/10/27
 * Email:943852572@qq.com
 * Description:
 */

public interface ExamService {

    /**
     * 审核身份
     * @param body
     * @return
     */
    @POST("notifications")
    Observable<ResponseBody> examID(@Body RequestBody body);

}
