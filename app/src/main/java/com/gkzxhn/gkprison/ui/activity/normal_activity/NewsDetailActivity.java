package com.gkzxhn.gkprison.ui.activity.normal_activity;

import android.app.ProgressDialog;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.utils.CustomUtils.OkHttpUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.NomalUtils.Log;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.UIUtils;

import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 新闻详情页
 */
public class NewsDetailActivity extends BaseActivityNew {

    private static final String TAG = "NewsDetailActivity";
    @BindView(R.id.wv_news_detail) WebView wv_news_detail;
    @BindView(R.id.progress) ProgressBar progress;
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.rl_back) RelativeLayout rl_back;
    @BindView(R.id.et_comment) EditText et_comment;
    @BindView(R.id.ll_comment) LinearLayout ll_comment;
    private int type;
    private int id;// 新闻id
    private ProgressDialog dialog;

    @Override
    public int setLayoutResId() {
        return R.layout.activity_news_detail;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        tv_title.setText("");
        rl_back.setVisibility(View.VISIBLE);
        id = getIntent().getIntExtra("id",-1);
        // type=0 首页轮播图  type=1 新闻  默认为1
        type = getIntent().getIntExtra("type", 1);
        String webUrl;
        if(type == 1) {
            webUrl = Constants.RESOURSE_HEAD + "/news/" + id;
        }else {
            int index = getIntent().getIntExtra("index", 1);
            webUrl = "https://www.fushuile.com/app/" + index;
        }
        WebSettings webSettings = wv_news_detail.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);// 开启Dom storage API 功能
        webSettings.setSupportZoom(true);
        wv_news_detail.loadUrl(webUrl);
        progress.setVisibility(View.VISIBLE);
        progress.setProgressDrawable(getResources().getDrawable(R.drawable.load_webview_progress_bar));
        wv_news_detail.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if(type == 1)
                    ll_comment.setVisibility(View.VISIBLE);
                super.onPageFinished(view, url);
            }
        });
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
        UIUtils.dismissProgressDialog(dialog);
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        finishPage();
    }

    /**
     * 关闭页面
     */
    private void finishPage() {
        if(wv_news_detail.canGoBack()){
            wv_news_detail.goBack();
        }else {
            finish();
        }
    }

    @OnClick({R.id.bt_comment, R.id.rl_back})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_comment:
                String comment_content = et_comment.getText().toString().trim();
                if(TextUtils.isEmpty(comment_content)){
                    ToastUtil.showShortToast(getString(R.string.input_comment_content));
                    return;
                }
                dialog = UIUtils.showProgressDialog(this);
                OkHttpClient client = new OkHttpClient();
                String param = "{\"family_id\":" + SPUtil.get(this, "family_id", -1) + ",\"content\":\"" + comment_content + "\"}";
                Log.i(TAG, param);
                String token = (String) SPUtil.get(MyApplication.getContext(), SPKeyConstants.ACCESS_TOKEN, "");

                Request request = new Request.Builder()
                        .addHeader("authorization",token)
                        .url(Constants.URL_HEAD + "news/" + id + "/comments?access_token=" + SPUtil.get(this, "token", ""))
                        .post(OkHttpUtils.getRequestBody(param)).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override public void onFailure(Call call, final IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UIUtils.dismissProgressDialog(dialog);
                                ToastUtil.showShortToast(getString(R.string.comment_failed));
                                Log.i(TAG, e.getMessage());
                            }
                        });
                    }

                    @Override public void onResponse(Call call, Response response) {
                        try {
                            String result = response.body().string();
                            /**
                             * {"code":200,"msg":"Comment success"}
                             */
                            Log.i(TAG, result);
                            JSONObject jsonObject = new JSONObject(result);
                            int code = jsonObject.getInt("code");
                            commentResult(code == 200);
                        } catch (Exception e) {
                            e.printStackTrace();
                            commentResult(false);
                        }
                    }
                });
                break;
//            case R.id.tv_comments:
//                Intent intent = new Intent(this, CommentsDetailsActivity.class);
//                intent.putExtra("news_id", id);
//                startActivity(intent);
//                break;
            case R.id.rl_back:
                finishPage();
                break;
        }
    }

    /**
     * 评论结果
     * @param result
     */
    private void commentResult(final boolean result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UIUtils.dismissProgressDialog(dialog);
                ToastUtil.showShortToast(result ? getString(R.string.comment_success)
                        : getString(R.string.comment_failed));
                if (result) et_comment.setText("");
            }
        });
    }
}