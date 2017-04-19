package com.gkzxhn.gkprison.ui.pay;

import android.util.Log;

import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.model.net.api.PayService;
import com.gkzxhn.gkprison.utils.CustomUtils.OkHttpUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;
import com.gkzxhn.gkprison.utils.NomalUtils.MD5Utils;
import com.gkzxhn.gkprison.utils.NomalUtils.SignUtils;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author: Huang ZN
 * Date: 2017/1/9
 * Email:943852572@qq.com
 * Description:支付工具类  各类工具方法
 */

public class PayUtils {

    private static final String TAG = PayUtils.class.getSimpleName();

    /**
     * 商户私钥，pkcs8格式
      */
    public static final String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAL62E/1KH+LunsXU" +
            "9shOfFXhg6S7qd9e8b1TieCtVuZpK/maw69rv8oxoqTCXD/oUuuwszY7JIXVX8o0" +
            "vcnK30rxTM10i5YIk16VTvqvf5D3VKxYfDJlldkiaxlO79L5A8lg224jjAbjWkqm" +
            "kvIhaoAxXq1ythdBY1KujN9OJnjVAgMBAAECgYEAnihF35KvavVVOt9oYamlN1u0" +
            "XtM7B4GnnMlA2NEn9iFWVMPicQI8paQQK+77rgwvaEK7/MeDfHH95KVkl4rlLcMG" +
            "FoUUAgvrRFdS2Xv6RSaci3fvkai7MeHKQQ8j/+1dABjJcQF/OfMPHpCPrK4kxQ5Q" +
            "sCF132mjUpiwtpzV+ikCQQD1GO6Wx/fSV2+Ihaa9coPR57kI6xpDr48r9utUHVIw" +
            "w0sTblsGWwgs+to7SG10m6kOqb+vsCTayoFY1cQEA9vzAkEAxzHRrn1uOXzF+V/w" +
            "FcsQvZrC9oK4ed0Lanc36WiJf8a31X8w7N0PxXzQVHbatm3GrrII2q/0ASntxmfW" +
            "RxbyFwJBAOzHrk9KVhcF00Ev5Pqmc8TIORDtl80GAKm3fHchcHKdaJ0YAqXsMcTK" +
            "fyPAf8WkT7lTslR3NdOMyVLaCOjcFZMCQHRUsgJXmoHUTsJetxXjK/mvYmEY4qe4" +
            "4ivhSDP2KycGZOI4j9glGkrZo8lQSFb2MWxg6S7eR4BOfmC6z7dgvS0CQQDLrn0S" +
            "+lotPkFBMBCh38KVEEI9Pb71SkRL05kHR4+sesm4a4bh72mrMKbcCXYxJRPXwt7k" +
            "yQd7PvRoFL2mwp8f";

    /**
     * 获取app签名  微信支付所需
     * @param params
     * @return
     */
    public static String getAppSign(List<NameValuePair> params){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append("d75699d893882dea526ea05e9c7a4090");
        String appSign = MD5Utils.ecoder(sb.toString()).toUpperCase();
        Log.i(TAG, "app sign : " + appSign);
        return appSign;
    }

    /**
     * get the sign type we use. 获取签名方式
     * @return
     */
    public static String getSignType() {
        return "sign_type=\"RSA\"";
    }

    /**
     * 对订单做RSA 签名
     * @param content
     * @return
     */
    public static String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * 微信支付返回结果中json参数
     * @param jsonStr
     * @param key
     * @return
     */
    public static String getJsonStringParams(String jsonStr, String key) {
        String param = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject jsonObject1 = jsonObject.getJSONObject("signed_params");
            param = jsonObject1.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return param;
    }

    /**
     * 获取结果码
     * @param json
     * @return
     */
    public static int getResultCode(String json) {
        int a = -1;
        try {
            JSONObject jsonObject = new JSONObject(json);
            a = jsonObject.getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return a;
    }

    /**
     * 发送支付方式至服务器
     * @param map url路径参数
     * @param body post请求体
     * @param subscriber 回调
     * @return
     */
    public static Subscription sendPaymentType(Map<String, String> headers, Map<String, String> map,
                                        String body, SimpleObserver<ResponseBody> subscriber){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)  // Constants.URL_HEAD + "prepay?jail_id=" + jail_id + "&access_token="
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PayService payService = retrofit.create(PayService.class);
        return payService.sendPaymentType(headers, map, OkHttpUtils.getRequestBody(body))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
