package com.gkzxhn.gkprison.ui.activity.normal_activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.model.net.api.ApiRequest;
import com.gkzxhn.gkprison.model.net.bean.AA;
import com.gkzxhn.gkprison.model.net.bean.Line_items_attributes;
import com.gkzxhn.gkprison.model.net.bean.Order;
import com.gkzxhn.gkprison.ui.pay.PaymentActivity;
import com.gkzxhn.gkprison.utils.CustomUtils.MainUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.OkHttpUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.RxUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.StringUtils;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.UIUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 汇款页面
 */
public class ReChargeActivity extends BaseActivityNew implements RadioGroup.OnCheckedChangeListener {

    private static final String TAG = ReChargeActivity.class.getSimpleName();
    @BindView(R.id.rg_recharge) RadioGroup rg_recharge;
    @BindView(R.id.rl_back) RelativeLayout rl_back;
    @BindView(R.id.tv_title) TextView tv_title;

    private String money = "50";
    private String TradeNo = "";
    private List<Line_items_attributes> line_items_attributes = new ArrayList<>();
    private String times = "";
    private int jail_id;

    private ProgressDialog getOrderNoDialog;
    private Subscription getOrderNoSub;

    @Override
    public int setLayoutResId() {
        return R.layout.activity_re_charge;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        tv_title.setText(getString(R.string.recharge));
        rl_back.setVisibility(View.VISIBLE);
        jail_id = (int) SPUtil.get(this, SPKeyConstants.JAIL_ID, 0);
        rg_recharge.setOnCheckedChangeListener(this);
    }

    @OnClick({R.id.btn_recharge, R.id.rl_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_recharge:
                times = StringUtils.formatTime("yyyy-MM-dd HH:mm:ss");
                getOrderFromServer();
                break;
            case R.id.rl_back:
                finish();
                break;
        }
    }

    @Override protected void initInjector() {}

    @Override
    protected boolean isApplyStatusBarColor() {
        return true;
    }

    @Override
    protected boolean isApplyTranslucentStatus() {
        return true;
    }

    @Override
    protected void onDestroy() {
        UIUtils.dismissProgressDialog(getOrderNoDialog);
        RxUtils.unSubscribe(getOrderNoSub);
        super.onDestroy();
    }

    /**
     * 从服务端获取充值订单号
     */
    private void getOrderFromServer() {
        getOrderNoDialog = UIUtils.showProgressDialog(this, "");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiRequest apiRequest = retrofit.create(ApiRequest.class);
        String token = (String) SPUtil.get(ReChargeActivity.this, SPKeyConstants.ACCESS_TOKEN, "");
        Map<String, String> header = new HashMap<>();
        header.put("authorization", token);
        String requestBody = getRequestBody();
        getOrderNoSub = apiRequest.getOrderInfo(header,jail_id, token, OkHttpUtils.getRequestBody(requestBody))
                .subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<ResponseBody>(){
                    @Override public void onError(Throwable e) {
                        UIUtils.dismissProgressDialog(getOrderNoDialog);
                        ToastUtil.showShortToast(getString(R.string.get_order_failed));
                        Log.i(TAG, "get order number failed: " + e.getMessage());
                    }

                    @Override public void onNext(ResponseBody responseBody) {
                        UIUtils.dismissProgressDialog(getOrderNoDialog);
                        try {
                            String result = responseBody.string();
                            TradeNo = MainUtils.getResultTradeNo(result);
                            int resultCode = MainUtils.getResultCode(result);
                            if (resultCode == 200) {
                                Intent intent = new Intent(ReChargeActivity.this, PaymentActivity.class);
                                intent.putExtra("totalmoney", money);
                                intent.putExtra("times", times);
                                intent.putExtra("TradeNo", TradeNo);
                                intent.putExtra("saletype", "视频充值");
                                startActivity(intent);
                            }
                        } catch (IOException e) {
                            ToastUtil.showShortToast(getString(R.string.get_order_failed));
                            Log.i(TAG, "get order number exception: " + e.getMessage());
                        }
                    }
                });
    }

    /**
     * 获取请求体
     * @return
     */
    private String getRequestBody() {
        int family_id = (int) SPUtil.get(this, SPKeyConstants.FAMILY_ID, 1);
        Order order = new Order();
        order.setFamily_id(family_id);
        Line_items_attributes lineitemsattributes = new Line_items_attributes();
        lineitemsattributes.setItem_id(9988);
        lineitemsattributes.setQuantity(1);
        line_items_attributes.add(lineitemsattributes);
        order.setLine_items_attributes(line_items_attributes);
        order.setJail_id(jail_id);
        order.setCreated_at(times);
        order.setAmount(Float.parseFloat(money));
        AA aa = new AA();
        aa.setOrder(order);
        return new Gson().toJson(aa);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_five:
                money = "50";
                break;
            case R.id.rb_twenty:
                money = "100";
                break;
            case R.id.rb_fifty:
                money = "200";
                break;
            case R.id.rb_hundred:
                money = "500";
                break;
        }
    }
}
