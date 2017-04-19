package com.gkzxhn.gkprison.utils.CustomUtils;

import rx.Subscription;

/**
 * Author: Huang ZN
 * Date: 2017/1/12
 * Email:943852572@qq.com
 * Description:Rx相关工具类
 */

public class RxUtils {

    /**
     * 取消订阅
     * @param subscription
     */
    public static void unSubscribe(Subscription... subscription){
        if (subscription == null || subscription.length < 1){
            return;
        }
        for (Subscription s : subscription){
            if (s != null && !s.isUnsubscribed()){
                s.unsubscribe();
            }
        }
    }
}
