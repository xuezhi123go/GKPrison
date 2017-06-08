package com.gkzxhn.gkprison.presenter.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.base.PerActivity;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.dagger.contract.MainContract;
import com.gkzxhn.gkprison.model.dao.bean.Cart;
import com.gkzxhn.gkprison.model.dao.bean.CartDao;
import com.gkzxhn.gkprison.model.net.api.ApiRequest;
import com.gkzxhn.gkprison.model.net.api.wrap.MainWrap;
import com.gkzxhn.gkprison.model.net.bean.PrisonerUserInfo;
import com.gkzxhn.gkprison.model.net.bean.VersionInfo;
import com.gkzxhn.gkprison.ui.activity.MainActivity;
import com.gkzxhn.gkprison.utils.CustomUtils.RxUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.SystemUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.FaceRequest;
import com.iflytek.cloud.RequestListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.gkzxhn.gkprison.constant.Constants.fileName;
import static com.gkzxhn.gkprison.utils.CustomUtils.MainUtils.getXml;

/**
 * Author: Huang ZN
 * Date: 2016/12/28
 * Email:943852572@qq.com
 * Description:
 */
@PerActivity
public class MainPresenter implements MainContract.Presenter {

    private static final String TAG = MainPresenter.class.getName();
    private MainContract.View mainView;

    private OkHttpClient okHttpClient;
    private ApiRequest apiRequest;
    private Context mContext;
    private boolean isRegisterUser;

    private Subscription updateOrderSubscription;
    private Subscription getUserInfoSubscription;

    @Inject
    public MainPresenter(OkHttpClient okHttpClient, ApiRequest apiRequest, Context context){
        this.okHttpClient = okHttpClient;
        this.apiRequest = apiRequest;
        mContext = context;
    }


    @Override
    public void attachView(@NonNull MainContract.View view) {
        mainView = view;
    }

    @Override
    public void detachView() {
        mainView = null;
        RxUtils.unSubscribe(getUserInfoSubscription, updateOrderSubscription);
    }

    @Override
    public void checkStatus() {
        if (SystemUtil.isNetWorkUnAvailable()){
            // ToDo 没有网络显示默认布局 不进行下一步操作
            return;
        }
//        getMeetingInfo();
        StatusCode status = NIMClient.getStatus();
        Log.i(TAG, "nim status is ：" + status);
        isRegisterUser = (boolean) SPUtil.get(mContext, SPKeyConstants.IS_REGISTERED_USER, false);
        Log.i(TAG, "user type ：" + isRegisterUser);
        if (isRegisterUser){
            mainView.showProgress(mContext.getString(R.string.loading));
            // 获取用户信息
            int familyId = (int) SPUtil.get(mContext, SPKeyConstants.FAMILY_ID, -1);
            String token = (String) SPUtil.get(mContext, SPKeyConstants.ACCESS_TOKEN, "");

            Map<String, String> header = new HashMap<>();
            header.put("Authorization", token);
            android.util.Log.i(TAG, "checkStatus: prisonerUserInfo_ token  " + token);
            android.util.Log.i(TAG, "checkStatus: prisonerUserInfo_ family_id  " + familyId);
            getUserInfoSubscription = MainWrap.getPrisonerUserInfo(apiRequest, header,String.valueOf(familyId), new SimpleObserver<PrisonerUserInfo>(){
                @Override public void onNext(PrisonerUserInfo prisonerUserInfo) {
                    android.util.Log.i(TAG, "onNext: prisonerUserInfo   " + prisonerUserInfo);
                    savePrisonerInfo(prisonerUserInfo);
                    mainView.dismissProgress();
                    mainView.getUserInfoSuccess();
                    checkNewVersion();
                }

                @Override public void onError(Throwable e) {
                    Log.i(TAG, "get user info failed : " + e.getMessage());
                    // 获取用户信息失败  重新登录
                    mainView.dismissProgress();
                    ((MainActivity)mainView).addHomeFragment();
//                    mainView.reLoginNotGetUserInfo();
                }
            });

        }else {
            // 弹出监狱选择框
            int jail_id = (int) SPUtil.get(MyApplication.getContext(), SPKeyConstants.JAIL_ID, 0);
            if (jail_id == 0) {
                mainView.fastLoginWithoutAccount();
            }else {
                ((MainActivity)mainView).addHomeFragment();
            }
        }
        if(status == StatusCode.KICKOUT){
            mainView.accountKickout();// 其他设备登录
        }
    }

    /**
     * 获取会见列表信息
     */
    private void getMeetingInfo() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiRequest api = retrofit.create(ApiRequest.class);
        String token = (String) SPUtil.get(MyApplication.getContext(), SPKeyConstants.ACCESS_TOKEN, "");
        int family_id = (int) SPUtil.get(MyApplication.getContext(), SPKeyConstants.FAMILY_ID, -1);
        Map<String, String> header = new HashMap<>();
        header.put("authorization", token);
        api.getMeetings(header,family_id)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<ResponseBody>(){
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        android.util.Log.i(TAG, "onError: getMeetingInfo--- " + e.getMessage());
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String meetingTimeInfo = responseBody.string();
                            SPUtil.put(MyApplication.getContext(), SPKeyConstants.MEETINGS_TIME, meetingTimeInfo);
                            android.util.Log.i(TAG, "onNext: meetings_time_info == " + meetingTimeInfo);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        super.onNext(responseBody);
                    }
                });
    }


    private VersionInfo versionInfo;
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
                        MainPresenter.this.versionInfo = versionInfo;
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
        int current_version_name = SystemUtil.getVersionCode(MyApplication.getApplication());
        if(current_version_name < versionInfo.version_number){
            // 有新版本, 弹出更新对话框
            ((MainActivity)mainView).showUpdateDialog(versionInfo);
        }else {
            // 没有新版本
            android.util.Log.i(TAG, "parseVersionInfo: newsest_version  ... ");
        }
    }

    private RequestListener mRequestListener = new RequestListener() {

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {

            try {
                String result = new String(buffer, "utf-8");
                Log.d("FaceDemo", result);

                JSONObject object = new JSONObject(result);
                String type = object.optString("sst");
                if ("reg".equals(type)) {
                    register(object);
                }
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO: handle exception
            }
        }

        @Override
        public void onCompleted(SpeechError error) {

            if (error != null) {
                switch (error.getErrorCode()) {
                    case ErrorCode.MSP_ERROR_ALREADY_EXIST:
                        Log.i(TAG, "onCompleted: authid已经被注册 : " + error.getMessage());
                        break;

                    default:
                        Log.i(TAG, "onCompleted: " + error.getMessage());
                        break;
                }
            }
        }
    };

    private void register(JSONObject obj) throws JSONException {
        int ret = obj.getInt("ret");
        if (ret != 0) {
            Log.i(TAG, "注册失败");
            return;
        }
        if ("success".equals(obj.get("rst"))) {
            Log.i(TAG, "注册成功");
        } else {
            Log.i(TAG, "注册失败");
        }
    }

    @Override
    public void downloadAvatar(String path) {
        if (!isRegisterUser || SystemUtil.isNetWorkUnAvailable()){
            return;
        }
        Picasso.with(mContext).load(path).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                try {
                    File file = new File(fileName);
//                    if (file.exists()){
//                        boolean isDeleteSuccess = file.delete();
//                        Log.i(TAG, "delete exists avatar result: " + isDeleteSuccess);
//                    }
                    FileOutputStream fos =new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    Log.i(TAG, "avatar已下载至:" + fileName);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    //可根据流量及网络状况对图片进行压缩
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                    byte[] mImageData = baos.toByteArray();
                    // 设置用户标识，格式为6-18个字符（由字母、数字、下划线组成，不得以数字开头，不能包含空格）。
                    // 当不设置时，云端将使用用户设备的设备ID来标识终端用户。
                    FaceRequest mFaceRequest = new FaceRequest(((MainActivity) mainView));
                    String mAuthid = (String) SPUtil.get(MyApplication.getContext(), SPKeyConstants.USERNAME, "");
                    mFaceRequest.setParameter(SpeechConstant.AUTH_ID, mAuthid);
                    mFaceRequest.setParameter(SpeechConstant.WFR_SST, "reg");
                    mFaceRequest.sendRequest(mImageData, mRequestListener);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "avatar下载异常");
                }
            }

            @Override public void onBitmapFailed(Drawable errorDrawable) {
                Log.i(TAG, "avatar下载失败");
            }

            @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.i(TAG, "avatar准备加载");
            }
        });
    }
    /**
     * 保存囚犯信息
     * @param info
     */
    private void savePrisonerInfo(PrisonerUserInfo info) {
        putSP(SPKeyConstants.PRISON_TERM_STARTED_AT, info.getPrisoner().started_at);
        putSP(SPKeyConstants.PRISON_TERM_ENDED_AT, info.getPrisoner().ended_at);
        putSP(SPKeyConstants.GENDER, info.getPrisoner().gender);
        putSP(SPKeyConstants.PRISONER_NAME, info.getPrisoner().name);
//        putSP(SPKeyConstants.JAIL_ID, TextUtils.isEmpty(info.getPrisoner().getJail_id())?-1:Integer.valueOf(info.getPrisoner().getJail_id()));
        putSP(SPKeyConstants.PRISONER_NUMBER, info.getPrisoner().prisoner_number);
        putSP(SPKeyConstants.PRISONER_CRIMES, info.getPrisoner().crimes);
    }

    /**
     * 更新微信订单
     */
    public void doWXPayController(final String times, final CartDao cartDao) {
        if (times != null){
            final String url = "https://api.mch.weixin.qq.com/pay/orderquery";
            final String str = getXml();
            updateOrderSubscription = Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    RequestBody body = RequestBody.create(MediaType.parse("application/xml; charset=utf-8"), str);
                    Request request = new Request.Builder().url(url).post(body).build();
                    try {
                        Response response = okHttpClient.newCall(request).execute();
                        String result = response.body().string();
                        subscriber.onNext(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                        subscriber.onError(new Throwable("更新失败:" + e.getMessage()));
                    }
                }
            }).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SimpleObserver<String>(){
                        @Override public void onError(Throwable e) {
                            Log.i(TAG, e.getMessage());
                        }

                        @Override public void onNext(String s) {
                            Log.i(TAG, "update weixin pay order success : " + s);
                            String type = "微信支付";
                            /*String sql = "update CartInfo set isfinish = 1,payment_type = '"
                                    +type+"' where time = '" + times + "'";
                            db.execSQL(sql);
                            db.close();*/
                            Cart cart = cartDao.queryBuilder().where(CartDao.Properties.Time.eq(times)).build().unique();
                            if (cart != null) {
                                cart.setIsfinish(true);
                                cart.setPayment_type(type);
                                cartDao.update(cart);
                            }
                        }
                    });

        }
    }

    /**
     * 存SP
     * @param key
     * @param defaultValue
     */
    private void putSP(String key, Object defaultValue){
        SPUtil.put(mContext, key, defaultValue);
    }
}
