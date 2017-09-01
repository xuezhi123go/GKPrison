package com.gkzxhn.gkprison.presenter.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gkzxhn.gkprison.base.BasePresenter;
import com.gkzxhn.gkprison.base.PerActivity;
import com.gkzxhn.gkprison.model.net.api.ApiRequest;
import com.gkzxhn.gkprison.model.net.api.wrap.MainWrap;
import com.gkzxhn.gkprison.model.net.bean.PrisonerOrders;
import com.gkzxhn.gkprison.ui.activity.normal_activity.ShoppingRecordActivity;
import com.gkzxhn.gkprison.utils.CustomUtils.RxUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.Subscription;

/**
 * Created by 方 on 2017/9/1.
 */

@PerActivity
public class ShoppingPresenter implements BasePresenter<ShoppingRecordActivity> {

    private static final String TAG = ShoppingPresenter.class.getSimpleName();
    private ApiRequest mApiRequest;
    private Context mContext;
    private ShoppingRecordActivity mView;
    private Subscription mSubscription;

    @Inject
    public ShoppingPresenter(ApiRequest apiRequest, Context context) {
        mApiRequest = apiRequest;
        mContext = context;
    }

    public void getShoppingRecords(int familyId){

        // 获取用户信息
        String token = (String) SPUtil.get(mContext, SPKeyConstants.ACCESS_TOKEN, "");
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", token);
        mSubscription = MainWrap.getPrisonerOrders(mApiRequest, header, familyId, new SimpleObserver<PrisonerOrders>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.i(TAG, "onError: " + e.getMessage());
                ToastUtil.showShortToast("获取数据失败");
            }

            @Override
            public void onNext(PrisonerOrders prisonerOrders) {
                super.onNext(prisonerOrders);
                Log.i(TAG, "onNext: prisonerOrders :  " + prisonerOrders);
                mView.addPrisonerRecord(prisonerOrders.prisoner.prisoner_orders);
            }
        });
    }

    @Override
    public void attachView(@NonNull ShoppingRecordActivity view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        RxUtils.unSubscribe(mSubscription);
    }
}
