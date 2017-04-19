package com.gkzxhn.gkprison.presenter.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.utils.RegexUtils;
import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.PerActivity;
import com.gkzxhn.gkprison.dagger.contract.RegisterContract;
import com.gkzxhn.gkprison.model.net.api.LoginService;
import com.gkzxhn.gkprison.model.net.api.wrap.LoginWrap;
import com.gkzxhn.gkprison.model.net.bean.Register;
import com.gkzxhn.gkprison.model.net.bean.Uuid_images_attributes;
import com.gkzxhn.gkprison.utils.CustomUtils.OkHttpUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.RxUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;
import com.gkzxhn.gkprison.utils.NomalUtils.ImageTools;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author: Huang ZN
 * Date: 2016/12/26
 * Email:943852572@qq.com
 * Description:
 */
@PerActivity
public class RegisterPresenter implements RegisterContract.Presenter {

    private static final String TAG = "RegisterPresenter";
    private RegisterContract.View registerView;

    private Subscription mVerifySubscription;
    private Subscription mCheckSubscription;
    private Subscription mRegisterSubscription;

    private Context mContext;
    private LoginService loginService;

    @Inject
    public RegisterPresenter(LoginService loginService, Context context){
        this.loginService = loginService;
        mContext = context;
    }

    @Override
    public void attachView(@NonNull RegisterContract.View view) {
        registerView = view;
        registerView.showMainUi();
    }

    @Override
    public void detachView() {
        RxUtils.unSubscribe(mVerifySubscription, mCheckSubscription,mRegisterSubscription );
        registerView = null;
    }

    @Override
    public void sendVerifyCode(String phone) {
        if (TextUtils.isEmpty(phone)){
            registerView.showToast(mContext.getString(R.string.null_phone));
            return;
        }
        if (!RegexUtils.isMobileExact(phone)){
            registerView.showToast(mContext.getString(R.string.unavailable_phone));
            return;
        }
        registerView.showProgress(mContext.getString(R.string.sending));
        registerView.startCountDown();
        String phone_str = "{\"registration\":{\"phone\":\"" + phone + "\"}}";

//TODO        String phone_str = "{\"apply\":{\"phone\":\"" + phone + "\"}}";
        mVerifySubscription = LoginWrap.getInstance().sendVerifyCode(loginService, OkHttpUtils.getRequestBody(phone_str), new SimpleObserver<ResponseBody>(){
            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "get verification code failed : " + e.getMessage());
                registerView.dismissProgress();
                registerView.removeCountDown();
                registerView.showToast(mContext.getString(R.string.verify_failed));
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                registerView.dismissProgress();
                try {
                    String result = responseBody.string();
                    if (result.contains(mContext.getString(R.string.code_200))){
                        registerView.showToast(mContext.getString(R.string.sended));
                        return;
                    }
                    Log.d(TAG, "get verification code success : " + result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                registerView.showToast(mContext.getString(R.string.verify_failed));
                registerView.removeCountDown();
            }
        });
    }

    /**
     *   newBitmap3,
     *   newBitmap1,
     *   newBitmap2,
     *   name,
     *   id_num,
     *   phone_num,
     *   relationship_with_prisoner,
     *   prisoner_number,
     *   prison_choose,
     *   identifying_code
     *   sex
     * @param avatar  头像位图
     * @param id_01  身份证正反面
     * @param id_02   不分顺序
     * @param contents 其它输入框内容
     */
    @Override
    public void register(final Map<String, Integer> map, final Bitmap avatar, final Bitmap id_01, final Bitmap id_02, final String... contents) {
        if (contents.length != 8){
            throw new IllegalStateException("please give eight strings for check");
        }
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                // 第一步 压缩图片
                List<Uuid_images_attributes> images_attributes = ImageTools.getZipPicture(id_01, id_02, avatar);
                // 第二部  获取Register bean转json字符串
                Register register = setRegisterBean(map, images_attributes, contents);
                if (register == null){
                    subscriber.onError(new Throwable(mContext.getString(R.string.not_open_prison)));
                    return;
                }
                String register_str = "{\"registration\":" + new Gson().toJson(register) + "}";

//         TODO       String register_str = "{\"apply\":" + new Gson().toJson(register) + "}";
                Log.i(TAG, "register info : " + register_str);
                subscriber.onNext(register_str);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<String>(){
                    @Override
                    public void onError(Throwable e) {
                        registerView.dismissProgress();
                        registerView.showToast(e.getMessage());
                    }

                    @Override
                    public void onNext(String result) {
                        register(result, contents[4]);
                    }
                });

    }

    /**
     * 注册
     * @param body
     */
    private void register(String body, final String prisoner_number){
        mRegisterSubscription = LoginWrap.getInstance().register(loginService, OkHttpUtils.getRequestBody(body), new SimpleObserver<ResponseBody>(){
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "register failed : " + e.getMessage());
                registerView.dismissProgress();
                registerView.showToast(mContext.getString(R.string.register_failed_retry));
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String result = responseBody.string();
                    Log.i(TAG, "register success : " + result);
                    registerView.dismissProgress();
                    if(result.contains(mContext.getString(R.string.code_200))){// success
                        registerView.showResultDialog(true, mContext.getString(R.string.register_success));
                        // 把囚号保存在本地
                        SPUtil.put(mContext, "prisoner_number", prisoner_number);
                    }else if(result.contains(mContext.getString(R.string.code_404))){
                        registerView.showToast(mContext.getString(R.string.verify_code_error));
                    }/*else if(result.contains(mContext.getString(R.string.code_501))){
                        JSONObject jsonObject = new JSONObject(result);
                        JSONObject errors = jsonObject.getJSONObject("errors");
                        JSONArray apply_create = errors.getJSONArray(result.contains("apply_create") ? "apply_create" : "phone");
                        registerView.showResultDialog(false, apply_create.getString(0));
                    }*/else if(result.contains(mContext.getString(R.string.code_400))) {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONObject errors = jsonObject.getJSONObject("errors");
                        String msg = jsonObject.getString("msg");
                        if (result.contains("create")) {
                            JSONArray apply_create = errors.getJSONArray("create");
                            registerView.showResultDialog(false, apply_create.getString(0));
                        }else if(result.contains("phone")){
                            JSONArray apply_create = errors.getJSONArray("phone");
                            registerView.showResultDialog(false, apply_create.getString(0));
                        }else {
                            registerView.showResultDialog(false, msg);
                        }
                    }else {
                        registerView.showResultDialog(false, mContext.getString(R.string.register_failed));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    registerView.dismissProgress();
                    registerView.showResultDialog(false, mContext.getString(R.string.register_failed));
                }
            }
        });
    }

    @Override
    public void checkVerifyCode(String phone_num, String code) {
        registerView.showProgress(mContext.getString(R.string.registering));

        String json_str = "{\"session\":{\"phone\":\"" + phone_num + "\",\"code\":\"" + code + "\"}}";
        mCheckSubscription = LoginWrap.getInstance().checkVerifyCode(loginService, OkHttpUtils.getRequestBody(json_str), new SimpleObserver<ResponseBody>(){
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "check code failed : " + e.getMessage());
                registerView.dismissProgress();
                registerView.showToast(mContext.getString(R.string.verify_failed));
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String result = responseBody.string();
                    Log.e(TAG, "check code success : " + result);
                    if(result.contains(mContext.getString(R.string.code_200))){
                        registerView.checkVerifyCodeSuccess();
                    }else {
                        registerView.dismissProgress();
                        registerView.showToast(mContext.getString(R.string.verify_code_error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    registerView.dismissProgress();
                    registerView.showToast(mContext.getString(R.string.exception));
                }
            }
        });
    }

    /**
     *   newBitmap3,
     *   newBitmap1,
     *   newBitmap2,
     *   name,
     *   id_num,
     *   phone_num,
     *   relationship_with_prisoner,
     *   prisoner_number,
     *   prison_choose,
     *   identifying_code
     * @param avatar  头像位图
     * @param id_01  身份证正反面
     * @param id_02   不分顺序
     * @param contents 其它输入框内容
     * @return true 为全通过验证  可进行下一步操作
     */
    @Override
    public boolean checkInput(Bitmap avatar, Bitmap id_01, Bitmap id_02, String... contents) {
        if (avatar == null){
            registerView.showToast(mContext.getString(R.string.choose_avatar));
            return false;
        }
        if (id_01 == null || id_02 == null){
            registerView.showToast(mContext.getString(R.string.upload_id_photo));
            return false;
        }
        if (contents.length != 7){
            throw new IllegalStateException("please give seven strings for check");
        }
        String name = contents[0];
        if (TextUtils.isEmpty(name)){
            registerView.showToast(mContext.getString(R.string.null_name));
            return false;
        }
        String id_num = contents[1];
        if (TextUtils.isEmpty(id_num)){
            registerView.showToast(mContext.getString(R.string.id_empty));
            return false;
        }
        if (!RegexUtils.isIDCard15(id_num) && !RegexUtils.isIDCard18(id_num)){
            registerView.showToast(mContext.getString(R.string.unexist_id_num));
            return false;
        }
        String phone_num = contents[2];
        if (TextUtils.isEmpty(phone_num)){
            registerView.showToast(mContext.getString(R.string.null_phone));
            return false;
        }
        if (!RegexUtils.isMobileExact(phone_num)){
            registerView.showToast(mContext.getString(R.string.unavailable_phone));
            return false;
        }
        String relationship = contents[3];
        if (TextUtils.isEmpty(relationship)){
            registerView.showToast(mContext.getString(R.string.input_relationship));
            return false;
        }
        String prisoner_number = contents[4];
        if (TextUtils.isEmpty(prisoner_number)){
            registerView.showToast(mContext.getString(R.string.null_prisoner_num));
            return false;
        }
        String prison = contents[5];
        if (TextUtils.isEmpty(prison)){
            registerView.showToast(mContext.getString(R.string.input_prison_name));
            return false;
        }
        String verify_code = contents[6];
        if (TextUtils.isEmpty(verify_code)){
            registerView.showToast(mContext.getString(R.string.input_verify_code));
            return false;
        }
        return true;
    }

    /**
     *   name,
     *   id_num,
     *   phone_num,
     *   relationship_with_prisoner,
     *   prisoner_number,
     *   prison_choose,
     *   identifying_code
     *   sex
     * 设置注册的bean对象
     * @return
     */
    private Register setRegisterBean(Map<String, Integer> prison_map,
         List<Uuid_images_attributes> uuid_images, String... contents) {
        Register register = new Register();
        register.setName(contents[0]);
        register.setUuid(contents[1]);
        register.setPhone(contents[2]);
        register.setRelationship(contents[3]);
        register.setPrisoner_number(contents[4]);
        register.setGender(contents[7]);
        if (prison_map.containsKey(contents[5])) {
            int jail_id = prison_map.get(contents[5]);
            register.setJail_id(jail_id);
            Log.i(TAG, "jail_id : " + jail_id);
        } else {
            return null;
        }
        register.setType_id(3);
        register.setUuid_images_attributes(uuid_images);
        return register;
    }
}
