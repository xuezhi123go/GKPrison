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
import com.gkzxhn.gkprison.model.net.bean.Opinion;
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
import butterknife.OnTextChanged;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * created by hzn 2016/1/16
 * 意见反馈
 */
public class OpinionFeedbackActivity extends BaseActivityNew {

    private static final String TAG = "OpinionFeedbackActivity";
    @BindView(R.id.et_content) EditText et_content;
    @BindView(R.id.surplus_count) TextView surplus_count;
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.rl_back) RelativeLayout rl_back;
    private String opinion_content;
    private ProgressDialog commit_dialog;
    private Subscription feedbackSubscription;

    @Override
    public int setLayoutResId() {
        return R.layout.activity_opinion_feedback;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        tv_title.setText(getString(R.string.opinion_feedback));
        rl_back.setVisibility(View.VISIBLE);
    }

    @OnTextChanged(R.id.et_content)
    public void onTextChanged(CharSequence s){
        if (s.length() >= 0 && s.length() <= 255) {
            surplus_count.setText("" + (255 - s.length()));
            surplus_count.setTextColor(getResources().getColor(R.color.tv_green));
        } else {
            surplus_count.setText("" + (255 - s.length()));
            surplus_count.setTextColor(getResources().getColor(R.color.tv_red));
        }
    }

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
        RxUtils.unSubscribe(feedbackSubscription);
        UIUtils.dismissProgressDialog(commit_dialog);
        super.onDestroy();
    }


    @OnClick({R.id.bt_commit_opinions, R.id.rl_back})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_commit_opinions:
                opinion_content = et_content.getText().toString().trim();
                if(!TextUtils.isEmpty(opinion_content) && !(opinion_content.length() > 255)){
                    if(!SystemUtil.isNetWorkUnAvailable()) {
                        sendOpinionsToServer(opinion_content);
                    }else {
                        ToastUtil.showShortToast(getString(R.string.net_broken));
                    }
                }else {
                    if(opinion_content.length() > 255) {
                        ToastUtil.showShortToast(getString(R.string.out_of_length));
                    }else {
                        ToastUtil.showShortToast(getString(R.string.input_content));
                    }
                }
                break;
            case R.id.rl_back:
               finishPage();
                break;
        }
    }

    /**
     * 发送意见反馈内容至服务端
     */
    private void sendOpinionsToServer(String mopinion_content) {
        commit_dialog = UIUtils.showProgressDialog(this, getString(R.string.commit_wait));
        Opinion opinion = new Opinion();
        Opinion.OpinionBean bean = opinion.new OpinionBean();
        bean.setContents(mopinion_content);
        opinion.setFeedback(bean);
        Gson gson = new Gson();
        String sendOpinion = gson.toJson(opinion);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiRequest feed = retrofit.create(ApiRequest.class);
        String token = (String) getSPValue(SPKeyConstants.ACCESS_TOKEN, "");
        Map<String, String> header = new HashMap<>();
        header.put("authorization", token);
        header.put("Content-Type:application/json", "Accept:application/json");
        feedbackSubscription = feed.sendOpinion(header, OkHttpUtils.getRequestBody(sendOpinion))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Object>() {
                    @Override public void onError(Throwable e) {
                        Log.i(TAG, "send opinion failed : " + e.getMessage());
                        UIUtils.dismissProgressDialog(commit_dialog);
                        ToastUtil.showShortToast(getString(R.string.commit_failed_retry));
                    }

                    @Override public void onNext(Object o) {
                        Log.i(TAG, "send opinion success : " + o.toString());
                        UIUtils.dismissProgressDialog(commit_dialog);
                        ToastUtil.showShortToast(getString(R.string.commit_success_));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                OpinionFeedbackActivity.this.finish();
                            }
                        }, 1500);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finishPage();
    }

    /**
     * 关闭页面
     */
    private void finishPage() {
        if (commit_dialog != null && commit_dialog.isShowing())
            return;
        opinion_content = et_content.getText().toString().trim();
        if(!TextUtils.isEmpty(opinion_content)){
            AlertDialog.Builder builder = new AlertDialog.Builder(OpinionFeedbackActivity.this);
            builder.setMessage("放弃反馈？");
            builder.setPositiveButton("放弃", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    OpinionFeedbackActivity.this.finish();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else {
            OpinionFeedbackActivity.this.finish();
        }
    }
}
