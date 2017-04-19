package com.gkzxhn.gkprison.ui.pay;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.constant.WeixinConstants;
import com.gkzxhn.gkprison.model.dao.GreenDaoHelper;
import com.gkzxhn.gkprison.model.dao.bean.Cart;
import com.gkzxhn.gkprison.model.dao.bean.CartDao;
import com.gkzxhn.gkprison.model.net.bean.PayResult;
import com.gkzxhn.gkprison.ui.activity.MainActivity;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;
import com.gkzxhn.gkprison.utils.NomalUtils.RSAUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.UIUtils;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author: Huang ZN
 * Date: 2016/12/28
 * Email:943852572@qq.com
 * Description:选择支付方式
 */
public class PaymentActivity extends BaseActivityNew {

    private static final String TAG = PaymentActivity.class.getSimpleName();
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.rl_back) RelativeLayout rl_back;

    @BindView(R.id.lv_pay_way) ListView lv_pay_way;
    @BindView(R.id.bt_pay) Button bt_pay;
    @BindView(R.id.tv_count_money) TextView tv_count_money;
    private String countMoney;// 总金额
    private PayMentAdapter payMentAdapter;
    public static String TradeNo;// 订单号
    public static String times;// 时间
    private int jail_id;// 监狱id

    private AlertDialog warningDialog;
    private ProgressDialog transitionDialog;

    private PayReq req = new PayReq();
//    private SQLiteDatabase db;

    private String apply = "";
    private String token;
    private String payment_type = "";
    private String prepay_id = "";
    private String mode = "01";
    private String app_id = "";
    public static String mch_id = "";
    private String nonce_str = "";
    private String timeStamp = "";
    private CartDao mCartDao;

    @Override
    public int setLayoutResId() {
        return R.layout.activity_payment;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
//        db = StringUtils.getSQLiteDB(this);
        mCartDao = GreenDaoHelper.getDaoSession().getCartDao();
        tv_title.setText(R.string.pay_order);
        rl_back.setVisibility(View.VISIBLE);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        TradeNo = getIntent().getStringExtra(PayConstants.TRADE_NO);
        Log.d(TAG, "trade number: " + TradeNo);
        countMoney = getIntent().getStringExtra(PayConstants.TOTAL_MONEY);
        times = getIntent().getStringExtra(PayConstants.TIMES);
        int cart_id = getIntent().getIntExtra(PayConstants.CART_ID, 0);
        String saletype = getIntent().getStringExtra(PayConstants.SALE_TYPE);
        String bussinesstype = getIntent().getStringExtra(PayConstants.BUSSINESS);
        if (!TextUtils.isEmpty(bussinesstype)) {
            String regEx="[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(bussinesstype);
            String value = m.replaceAll("").trim();
            float business = Float.parseFloat(value);
            float count = Float.parseFloat(countMoney);
            count += business;
            countMoney = String.valueOf(count);
        }
        token = (String) SPUtil.get(this, SPKeyConstants.ACCESS_TOKEN, "");
        jail_id = (int) SPUtil.get(this, SPKeyConstants.JAIL_ID, 0);
        tv_count_money.setText(countMoney);
        payMentAdapter = new PayMentAdapter(this);
        lv_pay_way.setAdapter(payMentAdapter);
        lv_pay_way.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                payMentAdapter.setOnItemClickListener(position);
            }
        });
    }

    @OnClick({R.id.bt_pay, R.id.rl_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_pay:
                bt_pay.setEnabled(false);
                transitionDialog = UIUtils.showProgressDialog(this);
                if (payMentAdapter.getWhichChecked() == 0) {
                    payment_type = PayConstants.ALI_PAY;
                    send_payment_type();
                } else if (payMentAdapter.getWhichChecked() == 1) {
                    payment_type = PayConstants.WEIXIN_PAY;
                    send_payment_type();
                }
                break;
            case R.id.rl_back:
                setBackPressed();
                break;
        }
    }

    /**
     * 返回
     */
    private void setBackPressed(){
        if (warningDialog != null && warningDialog.isShowing()){
            warningDialog.dismiss();
            return;
        }
        if (transitionDialog != null && transitionDialog.isShowing()){
            transitionDialog.dismiss();
            return;
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        setBackPressed();
    }

    @Override
    protected void onDestroy() {
        /*if (db != null) {
            db.close();
            db = null;
        }*/
        if (warningDialog != null){
            if (warningDialog.isShowing())
                warningDialog.dismiss();
            warningDialog = null;
        }
        if (transitionDialog != null){
            if (transitionDialog.isShowing())
                transitionDialog.dismiss();
            transitionDialog = null;
        }
        super.onDestroy();
    }

    @Override
    protected void initInjector() {}

    @Override
    protected boolean isApplyStatusBarColor() {
        return true;
    }

    @Override
    protected boolean isApplyTranslucentStatus() {
        return true;
    }

    private void send_payment_type() {
        String str = "{\"trade_no\":\"" + TradeNo + "\",\"payment_type\":\"" + payment_type + "\"}";
        Log.i(TAG, "trade_no and payment type info: " + str);
        Map<String, String> map = new HashMap<>();
        map.put("jail_id", jail_id + "");
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", token);
        PayUtils.sendPaymentType(header, map, str, new SimpleObserver<ResponseBody>(){
            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "send payment failed: " + e.getMessage());
                UIUtils.dismissProgressDialog(transitionDialog);
                showToast(getString(R.string.pay_failed));
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                UIUtils.dismissProgressDialog(transitionDialog);
                try {
                    String result = responseBody.string();
                    Log.i(TAG, "send payment success : " + result);
                    int code = PayUtils.getResultCode(result);
                    if (code == 200) {
                        if (payment_type.equals(PayConstants.ALI_PAY)) {
                            alipay();// 支付宝支付
                        } else if (payment_type.equals(PayConstants.WEIXIN_PAY)) {
                            parseParams(result);// 解析返回的json参数
                            weiXinPay();// 微信支付
                        } else if (payment_type.equals(PayConstants.UNION_PAY)) {
                            // 银联支付
                            UPPayAssistEx.startPay(PaymentActivity.this, null, null, "", mode);
                        }else {
                            showToast(getString(R.string.pay_failed));
                        }
                    }else {
                        showToast(getString(R.string.pay_failed));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    showToast(getString(R.string.pay_failed));
                }
            }
        });
    }

    /**
     * 解析微信支付参数
     * @param result
     */
    private void parseParams(String result) {
        prepay_id = PayUtils.getJsonStringParams(result, "prepayId");
        app_id = PayUtils.getJsonStringParams(result, "appId");
        mch_id = PayUtils.getJsonStringParams(result, "partnerId");
        nonce_str = PayUtils.getJsonStringParams(result, "nonceStr");
        String sign = PayUtils.getJsonStringParams(result, "package");
        timeStamp = PayUtils.getJsonStringParams(result, "timeStamp");
    }

    /**
     * toast
     * @param msg
     */
    private void showToast(String msg){
        ToastUtil.showShortToast(msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }
        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            // 支付成功后，extra中如果存在result_data，取出校验
            // result_data结构见c）result_data参数说明
            if (data.hasExtra("result_data")) {
                String result = data.getExtras().getString("result_data");
                try {
                    JSONObject resultJson = new JSONObject(result);
                    String sign = resultJson.getString("sign");
                    String dataOrg = resultJson.getString("data");
                    // 验签证书同后台验签证书
                    // 此处的verify，商户需送去商户后台做验签
                    boolean ret = RSAUtil.verify(dataOrg, sign, mode);
                    if (ret) {
                        // 验证通过后，显示支付结果
                        msg = "支付成功！";
                    } else {
                        // 验证不通过后的处理
                        // 建议通过商户后台查询支付结果
                        msg = "支付失败！";
                    }
                } catch (JSONException e) {
                }
            } else {
                // 未收到签名信息
                // 建议通过商户后台查询支付结果
                msg = getString(R.string.pay_success);
            }
        } else if (str.equalsIgnoreCase("fail")) {
            msg = getString(R.string.pay_failed);
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = getString(R.string.pay_cancel);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("支付结果通知");
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
        builder.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void alipay() {
        if (checkBusinessesInfo()) return;
        // 订单
        String orderInfo = getOrderInfo("测试的商品", "该测试商品的详细描述", countMoney);
        String sign = PayUtils.sign(orderInfo);// 对订单做RSA 签名
        Log.d(TAG, "sign: " + sign);
        try {
            sign = URLEncoder.encode(sign, "UTF-8");// 仅需对sign 做URL编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + PayUtils.getSignType();
        Log.i(TAG, "alipay order info: " + payInfo);
        if (!checkAliPayInstalled(this)) {
            bt_pay.setEnabled(true);
            ToastUtil.showShortToast("您还未安装支付宝");
            return;
        }
        Observable.create(new Observable.OnSubscribe<PayResult>() {
            @Override
            public void call(Subscriber<? super PayResult> subscriber) {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PaymentActivity.this);
                String version = alipay.getVersion();
                android.util.Log.i(TAG, "call: alipayVersion=== " + version);
                // 调用支付接口，获取支付结果
                subscriber.onNext(new PayResult(alipay.pay(payInfo)));
            }
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<PayResult>(){
                    @Override public void onError(Throwable e) {
                        Log.i(TAG, "build PayTask failed : " + e.getMessage());
                        bt_pay.setEnabled(true);
                        showToast(getString(R.string.pay_failed));
                    }

                    @Override public void onNext(PayResult result) {
                        Log.i(TAG, "build PayTask success : " + result);
                        bt_pay.setEnabled(true);
                        // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                        String resultInfo = result.getResult();
                        String resultStatus = result.getResultStatus();
                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                        if (TextUtils.equals(resultStatus, "9000")) {
                            showToast(getString(R.string.pay_success));
                            String type = getString(R.string.zhifubao);
                            Cart cart = mCartDao.queryBuilder().where(CartDao.Properties.Time.eq(times)).build().unique();
                            if (cart != null) {
                                cart.setIsfinish(true);
                                cart.setPayment_type(type);
                                mCartDao.update(cart);
                            }

                            /*String sql = "update CartInfo set isfinish = 1,payment_type = '" + type + "' where time = '" + times + "'";
                            db.execSQL(sql);*/
//                            // 支付成功通知会见页面更新数据
//                            EventBus.getDefault().post(new RechargeSuccessEvent());
                            Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            PaymentActivity.this.startActivity(intent);
                            finish();
                        } else {
                            // 判断resultStatus 为非“9000”则代表可能支付失败
                            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            if (TextUtils.equals(resultStatus, "8000")) {
                                showToast(getString(R.string.pay_result_confirming));
                            } else {
                                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                showToast(getString(R.string.pay_failed));
                            }
                        }
                    }
                });
    }

    /**
     * 检测是否安装了支付宝
     * @param context
     * @return
     */
    public boolean checkAliPayInstalled(Context context) {

        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }

    /**
     * 检查商户信息
     * @return
     */
    private boolean checkBusinessesInfo() {
        if (TextUtils.isEmpty(PayConstants.PARTNER) || TextUtils.isEmpty(PayUtils.RSA_PRIVATE)
                || TextUtils.isEmpty(PayConstants.SELLER)) {
            warningDialog = UIUtils.showAlertDialog(this, "需要配置PARTNER | RSA_PRIVATE| SELLER",
                    new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    warningDialog.dismiss();
                    finish();
                }
            }, null);
            return true;
        }
        return false;
    }

    /**
     * 支付宝支付所需
     * create the order info. 创建订单信息
     */
    public String getOrderInfo(String subject, String body, String price) {
        String orderInfo = "partner=" + "\"" + PayConstants.PARTNER + "\"";// 签约合作者身份ID
        orderInfo += "&seller_id=" + "\"" + PayConstants.SELLER + "\"";// 签约卖家支付宝账号
        orderInfo += "&out_trade_no=" + "\"" + TradeNo + "\"";// 商户网站唯一订单号
        orderInfo += "&subject=" + "\"" + subject + "\"";// 商品名称
        orderInfo += "&body=" + "\"" + body + "\"";// 商品详情
        orderInfo += "&total_fee=" + "\"" + price + "\"";// 商品金额
        orderInfo += "&notify_url=" + "\"" + "https://www.fushuile.com/api/v1/payment" + "\"";// 服务器异步通知页面路径
        orderInfo += "&service=\"mobile.securitypay.pay\"";// 服务接口名称， 固定值
        orderInfo += "&payment_type=\"1\"";// 支付类型， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";// 参数编码， 固定值
        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";
        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";
        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";
        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";
        Log.d(TAG, "orderInfo: " + orderInfo);
        return orderInfo;
    }

    /**
     * 微信支付
     */
    private void weiXinPay() {
        String mPackage = "Sign=WXPay";
        IWXAPI api = WXAPIFactory.createWXAPI(this, WeixinConstants.APP_ID, true);
        req.appId = app_id;
        req.nonceStr = nonce_str;
        req.packageValue = mPackage;
        req.partnerId = mch_id;
        req.prepayId = prepay_id;
        req.timeStamp = timeStamp;

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        //signParams.add(new BasicNameValuePair("sign", sign));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
        req.sign = PayUtils.getAppSign(signParams);
        if (transitionDialog.isShowing()) {
            transitionDialog.dismiss();
        }
        api.registerApp(WeixinConstants.APP_ID);
        api.sendReq(req);
        bt_pay.setEnabled(true);
    }
}
