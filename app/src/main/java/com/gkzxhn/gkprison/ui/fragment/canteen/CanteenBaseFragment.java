package com.gkzxhn.gkprison.ui.fragment.canteen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseFragmentNew;
import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.model.dao.GreenDaoHelper;
import com.gkzxhn.gkprison.model.dao.bean.Cart;
import com.gkzxhn.gkprison.model.dao.bean.CartDao;
import com.gkzxhn.gkprison.model.dao.bean.LineItems;
import com.gkzxhn.gkprison.model.dao.bean.LineItemsDao;
import com.gkzxhn.gkprison.model.net.api.ApiRequest;
import com.gkzxhn.gkprison.model.net.api.wrap.MainWrap;
import com.gkzxhn.gkprison.model.net.bean.AA;
import com.gkzxhn.gkprison.model.net.bean.CategoriesInfo;
import com.gkzxhn.gkprison.model.net.bean.Line_items_attributes;
import com.gkzxhn.gkprison.model.net.bean.Order;
import com.gkzxhn.gkprison.model.net.bean.Shoppinglist;
import com.gkzxhn.gkprison.ui.fragment.canteen.adapter.AllChooseAdapter;
import com.gkzxhn.gkprison.ui.pay.PayConstants;
import com.gkzxhn.gkprison.ui.pay.PaymentActivity;
import com.gkzxhn.gkprison.utils.CustomUtils.MainUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.OkHttpUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.RxUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.StringUtils;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.UIUtils;
import com.gkzxhn.gkprison.utils.event.ChangeEvent;
import com.gkzxhn.gkprison.utils.event.ClickEven1;
import com.gkzxhn.gkprison.utils.event.ClickEvent;
import com.google.gson.Gson;
import com.jauker.widget.BadgeView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * author:huangzhengneng
 * email:943852572@qq.com
 * date: 2016/8/2.
 * function:电子商务最底层fragment
 */
public class CanteenBaseFragment extends BaseFragmentNew implements AdapterView.OnItemClickListener{

    private static final String TAG = CanteenBaseFragment.class.getSimpleName();
    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

//    private SQLiteDatabase sqLiteDB = StringUtils.getSQLiteDB(getActivity());
    private CartDao mCartDao = GreenDaoHelper.getDaoSession().getCartDao();
    private LineItemsDao mLineItemsDao = GreenDaoHelper.getDaoSession().getLineItemsDao();

    @BindView(R.id.rl_all_class) RelativeLayout rl_all_class;// 全部分类布局
    @BindView(R.id.rl_sales) RelativeLayout rl_sales;// 销量优先布局
    @BindView(R.id.bt_settlement) Button bt_settlement;// 结算按钮
    @BindView(R.id.tv_all_class) TextView tv_all_class;// 全部分类文本
    @BindView(R.id.tv_sales) TextView tv_sales;// 销量优先文本
    @BindView(R.id.sp_all_class) Spinner sp_all_class;// 全部分类spinner
    @BindView(R.id.sp_sales) Spinner sp_sales;// 销量优先spinner
    @BindView(R.id.tv_total_money) TextView tv_total_money;// 购物车总金额
    @BindView(R.id.fl_sales_choose) FrameLayout fl_sales_choose;// 销量选择
    @BindView(R.id.iv_buy_car_icon) View iv_buy_car_icon;
    @BindView(R.id.fl_buy_car) FrameLayout fl_buy_car;//购物车详情页面
    @BindView(R.id.lv_shopping_car) ListView lv_shopping_car;// 购物车物品清单
    @BindView(R.id.fl_choose) FrameLayout fl_choose;
    @BindView(R.id.lv_all_choose) ListView lv_all_choose;
    @BindView(R.id.lv_sales_choose) ListView lv_sales_choose;
    @BindView(R.id.rl_clear) RelativeLayout rl_clear;// 清空购物车
    @BindView(R.id.tv_total_money_remarks) TextView tv_total_money_remarks;//配送费

    private float total;// 总金额
    private String totalMoneyStr;// 总金额字符串
    private long cart_id = 0;// 购物车id
    private List<Shoppinglist> commodities = new ArrayList<>();// 已选商品集合
    private AllClassificationFragment allClassFragment;
    private SalesPriorityFragment salesFragment;
    private Bundle data;// 需要传到商品展示fragment中的bundle
    private BadgeView badgeView;
    private List<Integer> lcount = new ArrayList<>();
    private int allcount;
    private String TradeNo;// 订单号
    private List<Line_items_attributes> line_items_attributes = new ArrayList<>();
    private String times;
    private BuyCarAdapter adapter;
    private ProgressDialog getOrderInfoDialog;// 获取订单的对话框
    private List<Integer> eventlist = new ArrayList<Integer>();//用于点击事件传值
    private int click = 1;
    private int clicksalse = 1;
    private int jail_id;// 监狱id
    private AllChooseAdapter mChooseAdapter;
    private Bundle mData;
    public boolean isInit = false;
    private String mBarcode;

    @Override
    protected void initUiAndListener(View view) {
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initBadgeView();
        initUi();
    }

    /**
     * 初始化相关ui状态
     */
    private void initUi() {
        sp_all_class.setEnabled(true);
        sp_all_class.setFocusable(true);
        sp_sales.setEnabled(false);
        sp_sales.setFocusable(false);
        if (isAdded()) {
        tv_all_class.setTextColor(getResources().getColor(R.color.theme));
        }
        sp_all_class.setBackgroundResource(R.drawable.spinner_down);
        rl_all_class.requestFocus();
    }

    /**
     * 初始化购物车右上角的badge view
     */
    private void initBadgeView() {
        badgeView = new BadgeView(getActivity());
        badgeView.setTargetView(iv_buy_car_icon);
        badgeView.setTextSize(6);
        badgeView.setShadowLayer(3, 0, 0, Color.parseColor("#f10000"));
        badgeView.setBadgeGravity(Gravity.TOP | Gravity.RIGHT);
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.fragment_canteen;
    }

    @OnClick({R.id.rl_clear, R.id.iv_buy_car_icon, R.id.fl_buy_car,
            R.id.fl_choose, R.id.fl_sales_choose, R.id.rl_all_class,
            R.id.rl_sales, R.id.bt_settlement})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.rl_clear:
                clearShoppingCar();
                break;
            case R.id.iv_buy_car_icon:
                int i = fl_buy_car.getVisibility();
                if (commodities.size() == 0) {
                    if (isAdded()) {
                        showToastMsgShort(getString(R.string.not_select_goods));
                    }
                } else {
                    fl_buy_car.setVisibility(i == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
                break;
            case R.id.fl_buy_car:
                fl_buy_car.setVisibility(View.GONE);
                break;
            case R.id.fl_choose:
                fl_choose.setVisibility(View.GONE);
                break;
            case R.id.fl_sales_choose:
                fl_sales_choose.setVisibility(View.GONE);
                break;
            case R.id.rl_all_class:
                tv_all_class.setText("全部分类");
                if (isAdded()) {
                tv_all_class.setTextColor(getResources().getColor(R.color.theme));
                tv_sales.setTextColor(getResources().getColor(R.color.tv_bg));
                }
                sp_all_class.setBackgroundResource(R.drawable.spinner_down);
                sp_sales.setBackgroundResource(R.drawable.spinner);
                fl_choose.setVisibility(fl_choose.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                if (fl_choose.getVisibility() == View.GONE) {
                    allClassFragment = new AllClassificationFragment();
                    data.putInt("leibie", 0);
                    allClassFragment.setArguments(data);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fl_commodity, allClassFragment).commit();
                }
                break;
            case R.id.rl_sales:
                if (isAdded()) {
                tv_sales.setTextColor(getResources().getColor(R.color.theme));
                tv_all_class.setTextColor(getResources().getColor(R.color.tv_bg));
                }
                sp_sales.setBackgroundResource(R.drawable.spinner_down);
                sp_all_class.setBackgroundResource(R.drawable.spinner);
                fl_sales_choose.setVisibility(fl_sales_choose.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                if (fl_sales_choose.getVisibility() == View.GONE) {
                    salesFragment = new SalesPriorityFragment();
                    salesFragment.setArguments(data);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fl_commodity, salesFragment).commit();
                }
                break;
            case R.id.bt_settlement:
                if (allcount != 0) {
                    getOrderInfo();
                } else {
                    if (isAdded()) {
                    ToastUtil.showShortToast(getString(R.string.please_select_goods));
                    }
                }
                break;
        }
    }

    @Override
    protected void initData() {
        TradeNo = MainUtils.getOutTradeNo();
        jail_id = (int) SPUtil.get(getActivity(), SPKeyConstants.JAIL_ID, 0);
        times = StringUtils.formatTime(System.currentTimeMillis(), TIME_PATTERN);
        insertAndQueryFromDB();
        int code = 0;
        if (mData != null) {
            code = mData.getInt("leibie", 0);
        }
        initDefaultPager(code);

        getCategoriesFromNet();
        // 设置类别选择适配器
        mChooseAdapter = new AllChooseAdapter(CanteenBaseFragment.this.getActivity());
        lv_sales_choose.setAdapter(mChooseAdapter);
        lv_all_choose.setAdapter(mChooseAdapter);
        lv_sales_choose.setOnItemClickListener(CanteenBaseFragment.this);
        lv_all_choose.setOnItemClickListener(CanteenBaseFragment.this);
        isInit = true;
    }

    /**
     * 从网络获取商品分类数据
     */
    public void getCategoriesFromNet() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiRequest request = retrofit.create(ApiRequest.class);
        Map<String, String> header = new HashMap<>();
        String token= (String) SPUtil.get(MyApplication.getContext(), SPKeyConstants.ACCESS_TOKEN, "");
        header.put("authorization", token);
        header.put("Content-Type:application/json", "Accept:application/json");
        MainWrap.getCategories(request, header, new SimpleObserver<CategoriesInfo>(){
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                android.util.Log.i(TAG, "onError: getCategoriesFromNet  : " + e.getMessage());
            }

            @Override
            public void onNext(CategoriesInfo categoriesInfo) {
                super.onNext(categoriesInfo);
                android.util.Log.i(TAG, "onNext: categoriesInfo-- " + categoriesInfo);
                List<CategoriesInfo.CategoriesBean> categories = categoriesInfo.categories;
                mChooseAdapter.setData(categories);
                mChooseAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * @param code 加载哪一类商品
     * 初始化默认显示的商品展示页面
     */
    private void initDefaultPager(int code) {
        if (5 == code) {
            tv_all_class.setText("充值卡");
        }
        data = new Bundle();
        data.putString("times", times);
        data.putInt("leibie", code); // 将时间及类别发送至商品展示fragment
        allClassFragment = new AllClassificationFragment();
        allClassFragment.setArguments(data);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_commodity, allClassFragment).commit();
    }

    /**
     * 通过全部分类等类别却换商品展示页面
     * @param code
     */
    private void switchAllClassPager(int code) {
        allClassFragment = new AllClassificationFragment();
        data.putInt("leibie", code);
        allClassFragment.setArguments(data);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_commodity, allClassFragment).commit();
        fl_choose.setVisibility(View.GONE);
    }

    /**
     * 通过销量优先等类别切换商品展示页面
     * @param code
     */
    private void switchSalesPager(int code) {
        salesFragment = new SalesPriorityFragment();
        data.putInt("leibie", code);
        salesFragment.setArguments(data);
        getActivity().getSupportFragmentManager().
                beginTransaction().replace(R.id.fl_commodity, salesFragment).commit();
        fl_sales_choose.setVisibility(View.GONE);
    }

    /**
     * 数据库操作订阅
     */
    private Subscription dbControllerSubscription;

    /**
     * 插入时间记录  根据时间查询购物车id并赋值给变量
     */
    private void insertAndQueryFromDB() {
        dbControllerSubscription = Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                Cart cart = new Cart();
                cart.setTime(times);
                cart.setIsfinish(false);
                cart.setRemittance(false);
                mCartDao.insert(cart);

                /*String insert_sql = "insert into CartInfo (time, isfinish, remittance) values ('" + times + "', 0, 0)";
                sqLiteDB.execSQL(insert_sql);*/
                Log.d(TAG, "times: " + times);
                List<Cart> cartList = mCartDao.queryBuilder().where(CartDao.Properties.Time.eq(times)).build().list();
                if (cartList != null) {
                    for (Cart cart1 : cartList) {
                        cart_id = cart1.getId();
                        subscriber.onNext(cart_id);
                    }
                }

                /*String query_sql = "select id from Cart where time = '" + times + "'";
                Cursor cursor = sqLiteDB.rawQuery(query_sql, null);
                while (cursor.moveToNext()) {
                    cart_id = cursor.getInt(cursor.getColumnIndex("id"));
                    subscriber.onNext(cart_id);
                }
                cursor.close();*/
            }
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long l) {
                        android.util.Log.i(TAG, "call: cart_id=== " + l);
                    }
                });
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (getOrderInfoDialog != null && getOrderInfoDialog.isShowing()){
            getOrderInfoDialog.dismiss();
        }
        RxUtils.unSubscribe(dbControllerSubscription, getOrderInfoSubscription,
                addGoodsSubscription, reduceGoodsSubscription, clearBuyCarSubscription);
        super.onDestroy();
    }

    private Subscription eventSubscription;// 事件订阅

    public void onEvent(ChangeEvent changeEvent){
        tv_total_money_remarks.setVisibility(changeEvent.mThisBarcode.length() > 1? View.VISIBLE : View.GONE);
    }

    public void onEvent(ClickEvent event) {
        // 从事件中获得参数值
        eventSubscription = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                commodities.clear();
                total = 0;
                lcount.clear();
                line_items_attributes.clear();
                allcount = 0;
                List<LineItems> lineItemsList = mLineItemsDao.queryBuilder().where(LineItemsDao.Properties.Cart_id.eq(cart_id)).distinct().build().list();
                if (lineItemsList != null) {
                    for (LineItems lineItems : lineItemsList) {
                        Shoppinglist shoppinglist = new Shoppinglist();
                        shoppinglist.setId(lineItems.getItems_id());
                        shoppinglist.setPrice(lineItems.getPrice());
                        shoppinglist.setQty(lineItems.getQty());
                        shoppinglist.setTitle(lineItems.getTitle());
                        commodities.add(shoppinglist);
                    }
                }

                /*String sql = "select distinct line_items.Items_id,line_items.qty,line_items.id,line_items.price,line_items.title from line_items,CartInfo where line_items.cart_id = " + cart_id;
                Cursor cursor = sqLiteDB.rawQuery(sql, null);
                Log.d(TAG, "query result count : " + cursor.getCount());
                total = 0;
                if (cursor.getCount() > 0){
                    while (cursor.moveToNext()) {
                        Shoppinglist shoppinglist = new Shoppinglist();
                        shoppinglist.setId(cursor.getInt(cursor.getColumnIndex("Items_id")));
                        shoppinglist.setPrice(cursor.getString(cursor.getColumnIndex("price")));
                        shoppinglist.setQty(cursor.getInt(cursor.getColumnIndex("qty")));
                        shoppinglist.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                        commodities.add(shoppinglist);
                    }
                }*/
                for (int i = 0; i < commodities.size(); i++) {
                    String t = commodities.get(i).getPrice();
                    float p = Float.parseFloat(t);
                    int n = commodities.get(i).getQty();
                    Line_items_attributes lineitemsattributes = new Line_items_attributes();
                    lineitemsattributes.setItem_id(commodities.get(i).getId());
                    lineitemsattributes.setQuantity(n);
                    line_items_attributes.add(lineitemsattributes);
                    total += p * n;
                    lcount.add(n);
                }
                //  total += 2;
                for (int i = 0; i < lcount.size(); i++) {
                    allcount += lcount.get(i);
                }
                if (lineItemsList != null) {
                    subscriber.onNext(lineItemsList.size());
                }

                /*subscriber.onNext(cursor.getCount());
                cursor.close();*/
            }
        }).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer code) {
                        if (code <= 0){
                            tv_total_money.setText("0.0");
                            badgeView.setVisibility(View.GONE);
                        }else {
                            badgeView.setVisibility(View.VISIBLE);
                            adapter = new BuyCarAdapter();
                            lv_shopping_car.setAdapter(adapter);
                        }
                        badgeView.setText(String.valueOf(allcount));
//                        if (allcount == 0) total -= 2;
                        DecimalFormat fnum = new DecimalFormat("####0.00");
                        totalMoneyStr = fnum.format(total);
                        tv_total_money.setText(totalMoneyStr);
                    }
                });
    }

    private Subscription getOrderInfoSubscription;

    /**
     * 获取订单信息
     */
    private void getOrderInfo() {
        getOrderInfoDialog = UIUtils.showProgressDialog(getActivity(), "");
        String orderBody = getOrderBody();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiRequest apiRequest = retrofit.create(ApiRequest.class);
        String token = (String) SPUtil.get(getActivity(), SPKeyConstants.ACCESS_TOKEN, "");
        Map<String, String> header = new HashMap<>();
        header.put("authorization", token);

        getOrderInfoSubscription = apiRequest.getOrderInfo(header,jail_id, token, OkHttpUtils.getRequestBody(orderBody))
                .subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ResponseBody, Boolean>() {
                    @Override
                    public Boolean call(ResponseBody responseBody) {
                    if (isAdded()) {
                        try {
                            String result = responseBody.string();
                            Log.i(TAG, "get order info success : " + result);
                            TradeNo = MainUtils.getResultTradeNo(result);
                            Cart cart = mCartDao.queryBuilder().where(CartDao.Properties.Time.eq(times)).build().unique();
                            mBarcode = cart.getBarcode();
                            if (cart != null) {
                                cart.setTotal_money(totalMoneyStr);
                                cart.setCount(allcount);
                                cart.setOut_trade_no(TradeNo);
                                mCartDao.update(cart);
                            }

                            /*String sql = "update CartInfo set total_money = '" + totalMoneyStr + "',count = "
                                    + allcount + ",out_trade_no ='" + TradeNo + "'   where time = '" + times + "'";
                            sqLiteDB.execSQL(sql);*/
                            return MainUtils.getResultCode(result) == 200;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            UIUtils.dismissProgressDialog(getOrderInfoDialog);
                        }
                        if (isAdded()) {
                        showToastMsgShort(getString(R.string.get_order_failed));
                        }
                    }
                        return false;
                    }
                })
                .subscribe(new SimpleObserver<Boolean>(){
                    @Override public void onError(Throwable e) {
                        UIUtils.dismissProgressDialog(getOrderInfoDialog);
                        if (isAdded()) {
                            showToastMsgShort(getString(R.string.get_order_failed));
                        }
                        Log.i(TAG, "get order info failed : " + e.getMessage());
                    }

                    @Override public void onNext(Boolean success) {
                        if (success) {
                            Intent intent = new Intent(getActivity(), PaymentActivity.class);
                            intent.putExtra(PayConstants.TOTAL_MONEY, totalMoneyStr);
                            intent.putExtra("TradeNo", TradeNo);
                            intent.putExtra("times", times);
                            intent.putExtra("cart_id", cart_id);
                            if (isAdded() && !TextUtils.isEmpty(mBarcode) && mBarcode.length() > 1) {
                                intent.putExtra("bussiness", getString(R.string._2));
                            }
                            startActivity(intent);
                        }
                    }
                });
    }

    /**
     * 获取订单请求体
     * @return
     */
    private String getOrderBody() {
        int family_id = (int) SPUtil.get(getActivity(), SPKeyConstants.FAMILY_ID, 1);
        Order order = new Order();
        order.setFamily_id(family_id);
        order.setLine_items_attributes(line_items_attributes);
        order.setJail_id(jail_id);
        order.setCreated_at(times);
        Float f = Float.parseFloat(totalMoneyStr);
        order.setAmount(f);
        Gson gson = new Gson();
        AA aa = new AA();
        aa.setOrder(order);
        return gson.toJson(aa);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){
            case R.id.lv_all_choose:
                changeUIbyclass(position);
//                switchAllClassPager(position);
                break;
            case R.id.lv_sales_choose:
                switchSalesPager(position);
                break;
        }
    }

    /**
     * 根据类别修改界面
     * @param position
     */
    public void changeUIbyclass(int position) {
        HashMap<Integer, Integer> position2Id = mChooseAdapter.getPosition2Id();
        HashMap<Integer, String> position2title = mChooseAdapter.getPosition2title();
        if (position2Id.size() == 0 || position2title.size() == 0) {
            //方便起见暂时直接写死
            switchAllClassPager(5);
            tv_all_class.setText("充值卡");
        }else {
            try {
                switchAllClassPager(position2Id.get(position));
                tv_all_class.setText(position2title.get(position));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加减商品数量订阅
     */
    private Subscription addGoodsSubscription;
    private Subscription reduceGoodsSubscription;

    /**
     * 设置数据
     * @param data
     */
    public void setData(Bundle data) {
        mData = data;
    }

    private class BuyCarAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return commodities.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final BuyCarAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.buycar_items, null);
                viewHolder = new BuyCarAdapter.ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.price = (TextView) convertView.findViewById(R.id.tv_price);
                viewHolder.add = (RelativeLayout) convertView.findViewById(R.id.rl_buycar_add);
                viewHolder.reduce = (RelativeLayout) convertView.findViewById(R.id.rl_buycar_reduce);
                viewHolder.num = (TextView) convertView.findViewById(R.id.tv_buycar_num);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (BuyCarAdapter.ViewHolder) convertView.getTag();
            }
            viewHolder.title.setText(commodities.get(position).getTitle());
            viewHolder.num.setText(String.valueOf(commodities.get(position).getQty()));
            viewHolder.price.setText(commodities.get(position).getPrice());
            viewHolder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 购物车中条目商品的添加
                    addGoodsSubscription = Observable.create(new Observable.OnSubscribe<Integer>() {
                        @Override
                        public void call(Subscriber<? super Integer> subscriber) {
                            String GoodsCount = viewHolder.num.getText().toString().trim();
                            int singleGoodsCount = Integer.parseInt(GoodsCount);
                            int id = commodities.get(position).getId();
                            // 更新数据库
                            LineItems lineItems = mLineItemsDao.queryBuilder().where(LineItemsDao.Properties.Items_id.eq(id),
                                    LineItemsDao.Properties.Cart_id.eq(cart_id)).build().unique();
                            if (lineItems != null) {
                                lineItems.setQty(singleGoodsCount + 1);
                                mLineItemsDao.update(lineItems);
                            }

                            /*String updateCountSql = "update line_items set qty =" + (singleGoodsCount + 1) + "  where Items_id =" + id + "  and cart_id =" + cart_id;
                            sqLiteDB.execSQL(updateCountSql);*/
                            String price = commodities.get(position).getPrice();
                            float p = Float.parseFloat(price);
                            total += p;
                            DecimalFormat fnum = new DecimalFormat("####0.00");
                            totalMoneyStr = fnum.format(total);
                            allcount += 1;
                            // 查询数量
                            int qty = lineItems == null ? 0 : lineItems.getQty();

                            /*String sql1 = "select qty from line_items where Items_id = " + id +
                                    " and cart_id =" + cart_id;
                            Cursor cursor = sqLiteDB.rawQuery(sql1, null);
                            int qty = 0;
                            while (cursor.moveToNext()) {
                                qty = cursor.getInt(cursor.getColumnIndex("qty"));
                            }
                            cursor.close();*/
                            commodities.get(position).setQty(singleGoodsCount + 1);
                            int d = commodities.get(position).getId();
                            eventlist.add(d);
                            eventlist.add(qty);
                            subscriber.onNext(qty);
                        }
                    }).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Integer>() {
                                @Override
                                public void call(Integer code) {
                                    tv_total_money.setText(totalMoneyStr);
                                    badgeView.setText(String.valueOf(allcount));
                                    viewHolder.num.setText(String.valueOf(code));
                                    EventBus.getDefault().post(new ClickEven1(0, eventlist)); // 通知商品展示fragment修改数量
                                    eventlist.clear();
                                }
                            });
                }
            });
            viewHolder.reduce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 购物车中条目商品的减法
                    reduceGoodsSubscription = Observable.create(new Observable.OnSubscribe<Integer>() {
                        @Override
                        public void call(Subscriber<? super Integer> subscriber) {
                            String t = viewHolder.num.getText().toString();
                            int i = Integer.parseInt(t);
                            int id = commodities.get(position).getId();
                            LineItems lineItems = mLineItemsDao.queryBuilder().where(LineItemsDao.Properties.Items_id.eq(id),
                                    LineItemsDao.Properties.Cart_id.eq(cart_id)).build().unique();
                            if (i > 1){
                                // 如果条目的数量大于1个  就更新
                                if (lineItems != null) {
                                    lineItems.setQty(i - 1);
                                    mLineItemsDao.update(lineItems);
                                }

                                /*String sql = "update line_items set qty=" + (i - 1) + " where Items_id =" + id + "  and cart_id=" + cart_id;
                                sqLiteDB.execSQL(sql);*/
                            }
                            String price = commodities.get(position).getPrice();
                            float p = Float.parseFloat(price);
                            total -= p;
                            DecimalFormat fnum = new DecimalFormat("####0.00");
                            totalMoneyStr = fnum.format(total);
                            allcount -= 1;
                            int qty = 0;
                            if (i == 1){
                                // 如果条目数量等于1再减就删除此条目了
                                if (lineItems != null) {
                                    mLineItemsDao.delete(lineItems);
                                }

                                /*String sql = "delete from line_items where Items_id =" + id + "  and cart_id =" + cart_id;
                                sqLiteDB.execSQL(sql);*/
                            }else {
                                // 大于1的查询数量
                                if (lineItems != null) {
                                    qty = lineItems.getQty();
                                }

                                /*String sql1 = "select qty from line_items where Items_id = " + id + "  and cart_id = " + cart_id;
                                Cursor cursor = sqLiteDB.rawQuery(sql1, null);
                                while (cursor.moveToNext()) {
                                    qty = cursor.getInt(cursor.getColumnIndex("qty"));
                                }
                                cursor.close();*/
                                commodities.get(position).setQty(i - 1);
                            }
                            int d = commodities.get(position).getId();
                            eventlist.add(d);
                            eventlist.add(qty);
                            if (i == 1){
                                // 等于1的从已选商品集合中删除此条目
                                commodities.remove(position);
                            }
                            subscriber.onNext(qty);
                        }
                    }).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Integer>() {
                                @Override
                                public void call(Integer code) {
                                    tv_total_money.setText(totalMoneyStr);
                                    badgeView.setText(String.valueOf(allcount));
                                    viewHolder.num.setText(String.valueOf(code));
                                    if (commodities.size() == 0) {
                                        fl_buy_car.setVisibility(View.GONE);// 已选商品为空就隐藏购物车
                                    }
                                    EventBus.getDefault().post(new ClickEven1(0, eventlist));// 通知商品展示页面更新数量
                                    eventlist.clear();
                                    adapter.notifyDataSetChanged();
                                }
                            });
                }
            });
            return convertView;
        }

        private class ViewHolder {
            TextView title;
            TextView price;
            RelativeLayout add;
            RelativeLayout reduce;
            TextView num;
        }
    }

    /**
     * 清空购物车订阅
     */
    private Subscription clearBuyCarSubscription;

    /**
     * 清空购物车相关操作
     */
    private void clearShoppingCar() {
        clearBuyCarSubscription = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                List<LineItems> lineItemsList = mLineItemsDao.queryBuilder().where(LineItemsDao.Properties.Cart_id.eq(cart_id)).build().list();
                if (lineItemsList != null) {
                    for (LineItems lineItems : lineItemsList) {
                        mLineItemsDao.delete(lineItems);
                    }
                }

                /*String sql = "delete from line_items where cart_id = " + cart_id;
                sqLiteDB.execSQL(sql);*/
                allcount = 0; // 购物车商品数量清0
                totalMoneyStr = "0.00";// 购物车商品金额清0
                // 通知商品列表fragment清空
                for (int i = 0; i < commodities.size(); i++) {
                    eventlist.add(commodities.get(i).getPosition());
                }
                commodities.clear();
                subscriber.onNext(0);
            }
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer code) {
                        badgeView.setText(String.valueOf(allcount));
                        tv_total_money.setText(totalMoneyStr);
                        fl_buy_car.setVisibility(View.GONE);
                        EventBus.getDefault().post(new ClickEven1(1, eventlist));
                        eventlist.clear();
                    }
                });
    }
}
