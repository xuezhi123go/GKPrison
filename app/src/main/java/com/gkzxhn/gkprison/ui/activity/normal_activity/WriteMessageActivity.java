package com.gkzxhn.gkprison.ui.activity.normal_activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.model.net.api.ApiRequest;
import com.gkzxhn.gkprison.model.net.bean.Letter;
import com.gkzxhn.gkprison.utils.CustomUtils.OkHttpUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.RxUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;
import com.gkzxhn.gkprison.utils.NomalUtils.SystemUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.UIUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * created by huangzhengneng on 2016/2/1
 * 写信页面
 */
public class WriteMessageActivity extends BaseActivityNew {

    private static final String TAG = "WriteMessageActivity";
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.rl_back) RelativeLayout rl_back;
    @BindView(R.id.et_theme) EditText et_theme;
    @BindView(R.id.et_content) EditText et_content;
    private String theme;
    private String contents;
    private int jail_id;
    private int family_id = 0;
    private ProgressDialog progressDialog;
    private AlertDialog dialog;
    private Subscription msgSubscription;

    @Override
    public int setLayoutResId() {
        return R.layout.activity_write_message;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        tv_title.setText(R.string.write_msg);
        rl_back.setVisibility(View.VISIBLE);
        jail_id = (int) getSPValue(SPKeyConstants.JAIL_ID, 1);
        family_id = (int) getSPValue(SPKeyConstants.FAMILY_ID, 1);
    }

    @Override
    protected boolean isApplyStatusBarColor() {
        return true;
    }

    @Override
    protected boolean isApplyTranslucentStatus() {
        return true;
    }

    @OnClick({R.id.bt_commit_write_message, R.id.rl_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_commit_write_message:
                theme = et_theme.getText().toString();
                contents = et_content.getText().toString();
                if (theme.equals(getString(R.string.theme)) || TextUtils.isEmpty(theme)) {
                    ToastUtil.showShortToast(getString(R.string.input_theme));
                    return;
                } else if (TextUtils.isEmpty(contents)) {
                    ToastUtil.showShortToast(getString(R.string.input_content));
                    return;
                } else {
                    if (!SystemUtil.isNetWorkUnAvailable()) {
                        sendMessage();
                    } else {
                        ToastUtil.showShortToast(getString(R.string.net_broken));
                    }
                }
                break;
            case R.id.rl_back:
                contents = et_content.getText().toString().trim();
                theme = et_theme.getText().toString().trim();
                if (!TextUtils.isEmpty(contents) || !TextUtils.isEmpty(theme)) {
                    showConfirmDialog();
                } else {
                    WriteMessageActivity.this.finish();
                }
                break;
        }
    }

    /**
     * 显示确认对话框
     */
    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WriteMessageActivity.this);
        builder.setMessage("放弃写信？");
        builder.setPositiveButton("放弃", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                WriteMessageActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 提交
     */
    private void sendMessage() {
        progressDialog = UIUtils.showProgressDialog(this, getString(R.string.commit_wait));
        String message = getCommitBean();
        Log.i(TAG, message);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiRequest writeMessage = retrofit.create(ApiRequest.class);
        String token = (String) getSPValue(SPKeyConstants.ACCESS_TOKEN, "");
        android.util.Log.i(TAG, "sendMessage: token==== " + token);
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", token);
        header.put("Content-Type:application/json", "Accept:application/json");
        msgSubscription = writeMessage.sendMessage(header, OkHttpUtils.getRequestBody(message)).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new SimpleObserver<Object>() {
                @Override public void onError(Throwable e) {
                    Log.e(TAG, "send failed : " + e.getMessage());
                    UIUtils.dismissProgressDialog(progressDialog);
                    ToastUtil.showShortToast(getString(R.string.commit_failed_retry));
                }

                @Override public void onNext(Object result) {
                    Log.i(TAG, "send success : " + result.toString());
                    UIUtils.dismissProgressDialog(progressDialog);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShortToast(getString(R.string.commit_success));
                        }
                    }, 500);
                    finish();
                }
            });
    }

    /**
     * 获取提交实体
     * @return
     */
    private String getCommitBean() {
        Letter letter = new Letter();
        Letter.MessageBean bean = letter.new MessageBean();
        bean.setTitle(theme);
        bean.setContents(contents);
        bean.setJail_id(jail_id);
        bean.setFamily_id(family_id);
        letter.setMessage(bean);
        return new Gson().toJson(letter);
    }

    @Override
    public void onBackPressed() {
        if(progressDialog == null || !progressDialog.isShowing()){
            // 正在show时屏蔽返回键
            contents = et_content.getText().toString().trim();
            theme = et_theme.getText().toString().trim();
            if (!TextUtils.isEmpty(contents) || !TextUtils.isEmpty(theme)) {
                showConfirmDialog();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onDestroy() {
        UIUtils.dismissAlertDialog(dialog);
        UIUtils.dismissProgressDialog(progressDialog);
        RxUtils.unSubscribe(msgSubscription);
        super.onDestroy();
    }
}
