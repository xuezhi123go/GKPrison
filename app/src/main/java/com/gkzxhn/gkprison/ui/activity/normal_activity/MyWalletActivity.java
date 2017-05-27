package com.gkzxhn.gkprison.ui.activity.normal_activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.model.net.api.ApiRequest;
import com.gkzxhn.gkprison.model.net.api.wrap.MainWrap;
import com.gkzxhn.gkprison.model.net.bean.Balances;
import com.gkzxhn.gkprison.model.net.bean.CommonResult;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.UIUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 方 on 2017/3/31.
 */

public class MyWalletActivity extends BaseActivityNew {
    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.rl_back)
    RelativeLayout rl_back;
    @BindView(R.id.tv_remittance)
    TextView tv_remittance;
    @BindView(R.id.tv_my_balance)
    TextView tv_my_balance;
    private ProgressDialog mProgressDialog;
    private String balance;

    @Override
    public int setLayoutResId() {
        return R.layout.activity_my_wallet;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        tv_title.setText(R.string.my_balance);
        rl_back.setVisibility(View.VISIBLE);
        tv_remittance.setVisibility(View.VISIBLE);
        tv_remittance.setText(getString(R.string.apply_refund)); //申请退款
        getBalance();
//        NimInitUtil.setRemindAlarm(this, "");
    }

    /**
     * 获取余额
     */
    private void getBalance() {
        balance = (String) SPUtil.get(this, SPKeyConstants.USER_BALANCES, "");
        if (TextUtils.isEmpty(balance)) {
            getBalanceFromNet();
        }else {
            tv_my_balance.setText("¥ " + balance);
        }
    }

    private void getBalanceFromNet() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiRequest api = retrofit.create(ApiRequest.class);
        String token = (String) SPUtil.get(this, SPKeyConstants.ACCESS_TOKEN, "");
        int family_id = (int) SPUtil.get(this, SPKeyConstants.FAMILY_ID, -1);
        Map<String, String> header = new HashMap<>();
        header.put("authorization", token);
        api.getBalance(header,family_id)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Balances>() {
                    @Override public void onCompleted() {}

                    @Override public void onError(Throwable e) {
                        Log.i(TAG, "get balance failed : " + e.getMessage());
                        tv_my_balance.setText("¥ " + 0);
                    }

                    @Override public void onNext(Balances result) {
                        Log.i(TAG, "get balance success : " + result.toString());
                        int code = result.code;
                        if(code == 200) {
                            Balances.BalanceBean bean =  result.balance;
                            MyWalletActivity.this.balance = bean.balance;
                            SPUtil.put(MyWalletActivity.this, SPKeyConstants.USER_BALANCES, balance);
                            tv_my_balance.setText("¥ " + balance);
                        }else {
                            tv_my_balance.setText("¥ " + 0);
                        }
                    }
                });
    }

    @Override
    protected boolean isApplyStatusBarColor() {
        return true;
    }

    @Override
    protected boolean isApplyTranslucentStatus() {
        return true;
    }

    @OnClick({R.id.rl_back, R.id.tv_remittance})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_remittance:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("确定申请退款？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //TODO... 申请退款
                        if (TextUtils.isEmpty(balance)) {
                            ToastUtil.showShortToast("您的余额为零");
                            return;
                        }
                        applyDrawbacks();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                break;
        }
    }

    /**
     * 申请退款
     */
    private void applyDrawbacks() {
        mProgressDialog = UIUtils.showProgressDialog(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiRequest request = retrofit.create(ApiRequest.class);
        Map<String, String> header = new HashMap<>();
        String token= (String) SPUtil.get(this, SPKeyConstants.ACCESS_TOKEN, "");
        header.put("authorization", token);
        header.put("Content-Type:application/json", "Accept:application/json");
        MainWrap.drawbacks(request, header, new SimpleObserver<CommonResult>(){
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                android.util.Log.i(TAG, "onError: drawbacks=== " + e.getMessage());
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
                ToastUtil.showShortToast(getString(R.string.network_error));
            }

            @Override
            public void onNext(CommonResult commonResult) {
                int code = commonResult.code;
                if (code == 200) {
                    android.util.Log.i(TAG, "onNext: drawbacks  : " + commonResult.msg);
                    ToastUtil.showShortToast("退款成功");
                    SPUtil.put(MyWalletActivity.this, SPKeyConstants.USER_BALANCES, "0");
                    tv_my_balance.setText("¥ 0");
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                }else if(code == 400) {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                    ToastUtil.showShortToast("退款失败:" + commonResult.errors);
                }
            }
        });
    }
}
