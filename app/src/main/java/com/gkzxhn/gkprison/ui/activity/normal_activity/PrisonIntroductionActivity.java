package com.gkzxhn.gkprison.ui.activity.normal_activity;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.NomalUtils.Log;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 监狱简介
 */
public class PrisonIntroductionActivity extends BaseActivityNew {

    @BindView(R.id.wv_news_detail) WebView wv_news_detail;
    @BindView(R.id.progress) ProgressBar progress;
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.rl_back) RelativeLayout rl_back;

    // http://10.93.1.10:3000/api/v1/news?access_token=d56e241a101d011c399211e9e24b0acd&jail_id=1

    @Override
    public int setLayoutResId() {
        return R.layout.activity_prison_introduction;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        tv_title.setText(R.string.prison_introduction);
        rl_back.setVisibility(View.VISIBLE);
        int id = (int) SPUtil.get(this, SPKeyConstants.JAIL_ID, -1);
        wv_news_detail.loadUrl(Constants.RESOURSE_HEAD + "/jails/" + id);
        Log.i("jail_id is :", id + "");
        WebSettings webSettings = wv_news_detail.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);// 开启Dom storage API 功能
        webSettings.setSupportZoom(true);
        progress.setVisibility(View.VISIBLE);
        progress.setProgressDrawable(getResources().getDrawable(R.drawable.load_webview_progress_bar));
        wv_news_detail.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progress.setProgress(newProgress);
                Log.i("loading web view progress ", newProgress + "");
                if(newProgress == 100){
                    progress.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
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
    public void onBackPressed() {
        if(wv_news_detail.canGoBack()){
            wv_news_detail.goBack();
        }else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.rl_back)
    public void onClick(){
        if(wv_news_detail.canGoBack()){
            wv_news_detail.goBack();
        }else {
            finish();
        }
    }
}