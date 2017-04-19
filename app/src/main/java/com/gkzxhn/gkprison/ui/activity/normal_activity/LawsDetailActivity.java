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
import com.gkzxhn.gkprison.utils.NomalUtils.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author: Huang ZN
 * Date: 2016/12/21
 * Email:943852572@qq.com
 * Description:法律法规详情页面
 */
public class LawsDetailActivity extends BaseActivityNew {

    @BindView(R.id.wv_news_detail) WebView wv_news_detail;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.rl_back) RelativeLayout rl_back;

    @Override
    public int setLayoutResId() {
        return R.layout.activity_laws_detail;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        tv_title.setText("");
        rl_back.setVisibility(View.VISIBLE);
        int id = getIntent().getIntExtra("id", 1);
        wv_news_detail.loadUrl(Constants.RESOURSE_HEAD + "/laws/" + id + "");
        WebSettings webSettings = wv_news_detail.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);// 开启Dom storage API 功能
        webSettings.setSupportZoom(true);
        progressBar.setVisibility(View.VISIBLE);
        wv_news_detail.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                Log.i("loading web view progress ", newProgress + "");
                if(newProgress == 100){
                    progressBar.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
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
        finish();
    }
}