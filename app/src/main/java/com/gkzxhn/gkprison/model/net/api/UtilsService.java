package com.gkzxhn.gkprison.model.net.api;

import com.gkzxhn.gkprison.model.net.bean.PrisonList;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Author: Huang ZN
 * Date: 2016/12/27
 * Email:943852572@qq.com
 * Description:
 */

public interface UtilsService {

    /**
     * 根据关键字获取监狱列表
     * @param text
     * @return
     */
    @GET("jails/{text}")
    Observable<PrisonList> getPrisonList(
            @Path("text") String text
    );

}
