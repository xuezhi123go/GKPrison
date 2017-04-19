package com.gkzxhn.gkprison.utils.CustomUtils;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Author: Huang ZN
 * Date: 2016/12/22
 * Email:943852572@qq.com
 * Description:
 */

public class OkHttpUtils {

    /**
     * 获取 application/json; charset=utf-8 类型的RequestBody
     * @param body_str
     * @return
     */
    public static RequestBody getRequestBody(String body_str){
        return RequestBody.create(MediaType.parse(
                "application/json; charset=utf-8"), body_str);
    }

}
