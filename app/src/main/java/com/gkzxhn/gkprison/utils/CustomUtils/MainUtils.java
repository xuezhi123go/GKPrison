package com.gkzxhn.gkprison.utils.CustomUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.constant.WeixinConstants;
import com.gkzxhn.gkprison.model.net.bean.Commodity;
import com.gkzxhn.gkprison.ui.activity.LoginActivity;
import com.gkzxhn.gkprison.ui.pay.PaymentActivity;
import com.gkzxhn.gkprison.utils.NomalUtils.Log;
import com.gkzxhn.gkprison.utils.NomalUtils.MD5Utils;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.keda.sky.app.TruetouchGlobal;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Author: Huang ZN
 * Date: 2016/12/28
 * Email:943852572@qq.com
 * Description:主页所需工具方法类
 */

public class MainUtils {

    public static final int[] OPTIONS_IVS_PRESS = {
            R.drawable.prison_introduction_press,
            R.drawable.laws_press, R.drawable.prison_open_press,
            R.drawable.visit_service_press, R.drawable.family_service_press,
            R.drawable.sms_press};
    public static final int[] OPTIONS_IVS = {
            R.drawable.prison_introduction,
            R.drawable.laws, R.drawable.prison_open,
            R.drawable.visit_service,
            R.drawable.family_service, R.drawable.sms};

    public static final String[] keys = {"001", "002", "003", "004", "005", "006", "007", "008", "009", "010",
            "011", "012", "013", "014", "015", "016", "017", "018", "019", "020","021", "022", "023", "024",
            "025", "026", "027", "028", "029", "030", "031", "032", "033", "034", "035", "036", "037", "038", "039", "040"};


    /**
     * 获取发至微信的xml
     * @return
     */
    public static String getXml(){
        String nonce_str = getRandomString();
        List<NameValuePair> list = new LinkedList<NameValuePair>();
        list.add(new BasicNameValuePair("appid", WeixinConstants.APP_ID));
        list.add(new BasicNameValuePair("mch_id", PaymentActivity.mch_id));
        list.add(new BasicNameValuePair("nonce_str",nonce_str));
        list.add(new BasicNameValuePair("out_trade_no", PaymentActivity.TradeNo));
        String sign = genAppSign(list);
        StringBuffer  xml = new StringBuffer();
        xml.append("<xml>");
        xml.append("<appid>");
        xml.append(WeixinConstants.APP_ID);
        xml.append("</appid>");
        xml.append("<mch_id>");
        xml.append(PaymentActivity.mch_id);
        xml.append("</mch_id>");
        xml.append("<nonce_str>");
        xml.append(nonce_str);
        xml.append("</nonce_str>");
        xml.append("<out_trade_no>");
        xml.append(PaymentActivity.TradeNo);
        xml.append("</out_trade_no>");
        xml.append("<sign>");
        xml.append(sign);
        xml.append("</sign>");
        xml.append("</xml>");
        return xml.toString();
    }

    /**
     * 获得32位随机字符串
     * @return
     */
    private static String getRandomString() {
        String suiji = "";
        int len = 32;
        char[] chars = new char[len];
        Random random = new Random();
        for (int i = 0;i < len;i++){
            if (random.nextBoolean()){
                chars[i] = (char)(random.nextInt(25) + 97);
            }else {
                chars[i] = (char)(random.nextInt(9) + 48);
            }
        }
        suiji = new String(chars);
        return suiji;
    }

    /**
     * 获取微信签名
     * @param params
     * @return
     */
    private static String genAppSign(List<NameValuePair> params) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        Log.d("sa", sb.toString());
        sb.append("key=");
        sb.append("d75699d893882dea526ea05e9c7a4090");
        Log.d("dd", sb.toString());
        //  sb.append("sign str\n" + sb.toString() + "\n\n");
        String appSign = MD5Utils.ecoder(sb.toString()).toUpperCase();
        Log.d("orion1", appSign);
        return appSign;
    }

    /**
     * 显示确认对话框
     */
    public static AlertDialog showConfirmDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View logout_dialog_view = View.inflate(context, R.layout.msg_ok_cancel_dialog, null);
        builder.setView(logout_dialog_view);
        TextView tv_cancel = (TextView) logout_dialog_view.findViewById(R.id.tv_cancel);
        final AlertDialog dialog = builder.create();
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView tv_ok = (TextView) logout_dialog_view.findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.startActivityClearTask(context);
                SPUtil.clear(context);
                NIMClient.getService(AuthService.class).logout();
                TruetouchGlobal.logOff();
            }
        });
        dialog.show();
        return dialog;
    }

    /**
     * 获取输出订单号
     * @return
     */
    public static String getOutTradeNo() {
        String key = StringUtils.formatTime("MMddHHmmss");
        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * 获取结果码  从json字符串中解析
     * @param result
     * @return
     */
    public static int getResultCode(String result) {
        int a = 0;
        try {
            JSONObject jsonObject = new JSONObject(result);
            a = jsonObject.getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return a;
    }

    /**
     * 获取订单号  从json字符串中解析
     * @param s
     * @return
     */
    public static String getResultTradeNo(String s) {
        String str = "";
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONObject jsonObject1 = jsonObject.getJSONObject("order");
            str = jsonObject1.getString("trade_no");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 解析商品列表
     *
     * @param s
     * @return
     */
    public static List<Commodity> analysisCommodityList(String s) {
        return new Gson().fromJson(s, new TypeToken<List<Commodity>>(){}.getType());

        /*List<Commodity> commodities = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(s);
            for (int i = 0; i < jsonArray.length(); i++) {
                Commodity commodity = new Commodity();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                commodity.setId(jsonObject.getInt("id"));
                commodity.setTitle(jsonObject.getString("title"));
                commodity.setDescription(jsonObject.getString("description"));
                commodity.setAvatar_url(jsonObject.getString("avatar_url"));
                commodity.setPrice(jsonObject.getString("price"));
                commodity.setCategory_id(jsonObject.getInt("category_id"));
                commodities.add(commodity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return commodities;*/
    }

    /**
     * 解析json里的code
     * @param json
     * @return
     */
    public static int getJsonCode(String json){
        int code = -1;
        try {
            JSONObject jsonObject = new JSONObject(json);
            code = jsonObject.getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 解析json里的balance
     * @param json
     * @return
     */
    public static String getJsonBalance(String json){
        String balance = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            balance = jsonObject.getString("balance");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return balance;
    }

    /**
     * 解析json里的cost
     * @param json
     * @return
     */
    public static String getJsonCost(String json){
        String cost = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            cost = jsonObject.getString("cost");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cost;
    }

    /**
     * 解析会见申请失败结果
     * @param result
     * @return
     */
    public static String getApplyFailedResult(String result){
        String errors = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            errors = jsonObject.getString("errors");
//            if(jsonArray.length() == 1) {
//                reason = jsonArray.getString(0);
//            }else {
//                reason = jsonArray.getString(0) + "," + jsonArray.getString(1);
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return errors;
    }
}
