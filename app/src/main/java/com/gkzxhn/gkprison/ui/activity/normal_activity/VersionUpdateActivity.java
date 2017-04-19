package com.gkzxhn.gkprison.ui.activity.normal_activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.model.net.api.ApiRequest;
import com.gkzxhn.gkprison.model.net.api.wrap.MainWrap;
import com.gkzxhn.gkprison.model.net.bean.VersionInfo;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;
import com.gkzxhn.gkprison.utils.NomalUtils.DownloadManager;
import com.gkzxhn.gkprison.utils.NomalUtils.Log;
import com.gkzxhn.gkprison.utils.NomalUtils.SystemUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 版本更新页面
 */
public class VersionUpdateActivity extends BaseActivityNew {

    private static final String TAG = "VersionUpdateActivity";
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.rl_back) RelativeLayout rl_back;
    @BindView(R.id.iv_check_update) ImageView iv_check_update;
    @BindView(R.id.bt_update) Button bt_update;// 检查更新&更新按钮
    @BindView(R.id.tv_version_code) TextView tv_version_code;// 当前版本号
    @BindView(R.id.tv_new_function) TextView tv_new_function;// 新功能tv
    @BindView(R.id.tv_new_version) TextView tv_new_version;// 新版本号
    @BindView(R.id.tv_new_function_contents) TextView tv_new_function_contents;// 新版本功能内容
    private RotateAnimation ra;
    private VersionInfo versionInfo;
    private TextView tv_progress;
    private ProgressBar pb_update;
//    private DownloadProgressBar dpv_update;
    private AlertDialog dialog;//升级对话框
    private boolean has_new_version = false;// 是否有新版本
    private boolean download_successed = false;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        }
    };

    private Runnable rotateTask = new Runnable() {
        @Override
        public void run() {
            ra = new RotateAnimation(0, -360 * 100, iv_check_update.getWidth()/2, iv_check_update.getHeight()/2);
            ra.setDuration(1500 * 100);
            LinearInterpolator linearInterpolator = new LinearInterpolator();
            ra.setInterpolator(linearInterpolator);// 匀速
            iv_check_update.startAnimation(ra);
        }
    };

    @Override
    public int setLayoutResId() {
        return R.layout.activity_version_update;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        tv_title.setText(R.string.vs_update);
        rl_back.setVisibility(View.VISIBLE);
        tv_version_code.setText(SystemUtil.getVersionName(getApplicationContext()));
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
        super.onDestroy();
    }

    @OnClick({R.id.bt_update, R.id.rl_back})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_update:
                if(!has_new_version) {
                    handler.post(rotateTask);
                    checkNewVersion();
                    bt_update.setClickable(false);// 不可点
                }else {
                    bt_update.setClickable(false);
                    showUpdateDialog();
                }
                break;
            case R.id.rl_back:
                finish();
                break;
        }
    }

    /**
     * 更新对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VersionUpdateActivity.this);
        builder.setCancelable(false);
        View update_view = View.inflate(VersionUpdateActivity.this, R.layout.update_dialog, null);
        tv_progress = (TextView) update_view.findViewById(R.id.tv_progress);
        pb_update = (ProgressBar) update_view.findViewById(R.id.pb_update);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //sd卡可用，用于存放下载的apk
            //1.下载
            String APK_URL = versionInfo.download;
            MainWrap.downloadFile(null, APK_URL, new SimpleObserver<ResponseBody>(){

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }

                @Override
                public void onNext(ResponseBody responseBody) {
                    String path = getExternalFilesDir(null) + File.separator + "yuwutong.apk";
                    File file = new File(path);
                    // 若文件已下载则直接安装
                    if(file.exists() && file.length() == responseBody.contentLength()) {
                        if (dialog != null) {
                            dialog.dismiss();
                            dialog = null;
                        }
                        bt_update.setClickable(true);
                        handler.postDelayed(install_apk_task, 500);
                        return;
                    }
                    boolean isSuccess = DownloadManager.writeResponseBodyToDisk(VersionUpdateActivity.this, responseBody, new DownloadManager.ProgressListener(){
                        @Override
                        public void upDateProgress(long fileSizeDownloaded, long fileSize) {
                            int progress = (int) (fileSizeDownloaded * 100 / fileSize);
                            pb_update.setVisibility(View.VISIBLE);
//                            tv_update.setText("正在更新: ");
                            pb_update.setMax(100);
                            pb_update.setProgress(progress);
                            tv_progress.setText(progress + "%");
                            Log.i("下载进度", fileSizeDownloaded + "----" + progress + "---" + fileSize);
                        }
                    });
                    if (isSuccess) {
                        DownloadManager.setProgressListener(null);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShortToast("下载成功");
                                bt_update.setClickable(true);
                                dialog.dismiss();
                            }
                        });
                        handler.postDelayed(install_apk_task, 500);
                    }else {
                        android.util.Log.i(TAG, "onNext: failed=== ");
                    }
                }
            });
        } else {
            //sd卡不可用
            Toast.makeText(VersionUpdateActivity.this, "sdcard不可用, 下载失败", Toast.LENGTH_SHORT).show();
        }
        builder.setView(update_view);
        dialog = builder.create();
        dialog.show();
        /**
         * Intent intent = new Intent();
         intent.setAction("android.intent.action.VIEW");
         Uri content_url = Uri.parse(url);
         intent.setData(content_url);
         startActivity(intent);
         */
    }

    /**
     * 安装apk任务
     */
    private Runnable install_apk_task = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(Uri.fromFile(new File(getExternalFilesDir(null) + File.separator + "yuwutong.apk")),
                    "application/vnd.android.package-archive");
            startActivity(intent);
        }
    };

    /**
     * 检查新版本
     */
    private void checkNewVersion() {
        //访问服务器检查是否有新版本
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiRequest api = retrofit.create(ApiRequest.class);
        api.versions()
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<VersionInfo>(){
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(VersionInfo versionInfo) {
                        super.onNext(versionInfo);
                        VersionUpdateActivity.this.versionInfo = versionInfo;
                        parseVersionInfo(versionInfo);
                    }
                });
    }

    /**
     * 解析版本信息
     * @param versionInfo
     */
    private void parseVersionInfo(VersionInfo versionInfo) {
        Log.i("版本信息", versionInfo.toString());
        int current_version_name = SystemUtil.getVersionCode(getApplicationContext());
        if(current_version_name < versionInfo.version_number){
            // 有新版本
            has_new_version = true;
            ra.cancel();
            bt_update.setClickable(true);
            bt_update.setText("点击更新");
            tv_new_version.setVisibility(View.VISIBLE);
            tv_new_function_contents.setVisibility(View.VISIBLE);
            tv_new_function.setVisibility(View.VISIBLE);
            tv_new_version.setText("新版本：" + versionInfo.version_code);
            tv_new_function.setText("新版本功能:");
            String contents = versionInfo.description;
            if(contents.contains("|")) {
                tv_new_function_contents.setText(contents.replace("|", "\n"));
            }else {
                tv_new_function_contents.setText(contents);
            }
        }else {
            // 没有新版本
            tv_new_function.setVisibility(View.VISIBLE);
            bt_update.setClickable(true);
            bt_update.setText("检查更新");
            tv_new_function.setText("已经是最新版本!");
            ra.cancel();
        }
    }
}
