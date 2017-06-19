package com.gkzxhn.gkprison.ui.fragment.home;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseFragmentNew;
import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.model.net.api.ApiRequest;
import com.gkzxhn.gkprison.model.net.bean.NewsResult;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;
import com.gkzxhn.gkprison.utils.NomalUtils.FileUtils;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.UIUtils;
import com.gkzxhn.gkprison.widget.view.FullLinearLayout;
import com.gkzxhn.gkprison.widget.view.RollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.gkzxhn.gkprison.utils.CustomUtils.MainUtils.keys;


/**
 * author:huangzhengneng
 * email:943852572@qq.com
 * date: 2016/8/2.
 * function:
 */

public class HomeFragment extends BaseFragmentNew {

    private static final String TAG = "HomeFragment";

    @BindView(R.id.rl_carousel) RelativeLayout rl_carousel;
    @BindView(R.id.dots_ll) LinearLayout dots_ll;
    @BindView(R.id.ll_title_dot) LinearLayout ll_title_dot;
    @BindView(R.id.top_news_title) TextView top_news_title;
    @BindView(R.id.top_news_viewpager) LinearLayout top_news_viewpager;
    @BindView(R.id.gv_home_options) GridView gv_home_options;
    @BindView(R.id.tv_focus_attention) TextView tv_focus_attention;
    @BindView(R.id.srl_refresh) SwipeRefreshLayout srl_refresh;
    @BindView(R.id.focus_news_list) RecyclerView focusNewsRecyclerView;

    private FocusNewsAdapter focusNewsAdapter;

    private final List<String> list_news_title = new ArrayList<>();
    private int jail_id;
    private String token;// 当前登录用户的token
    private ProgressDialog loadDataDialog;
    private List<NewsResult.News> focus_news_list;// 焦点新闻集合
    private List<NewsResult.News> allNews;// 所有的新闻集合

    /**
     * 轮播图导航点集合
     */
    private List<View> dotList = new ArrayList<>();

    @Override
    protected void initUiAndListener(View view) {
        ButterKnife.bind(this, view);
        srl_refresh.setColorSchemeResources(R.color.theme);
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initData() {
        jail_id = (int) SPUtil.get(getActivity(), SPKeyConstants.JAIL_ID, 0);
        showLoadingDialog(); // 初次进来加载对话框
        getFocusNews();// 获取焦点新闻
        gv_home_options.setAdapter(new MyOptionsAdapter(getActivity()));
        if((boolean) SPUtil.get(getActivity(), SPKeyConstants.IS_REGISTERED_USER, false)) {
            token = (String) SPUtil.get(getActivity(), "token", "");
        }
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFocusNews();// 获取焦点新闻
            }
        });
    }

    /**
     * 加载数据进度对话框
     */
    private void showLoadingDialog() {
        if(loadDataDialog == null) {
            try {
                loadDataDialog = UIUtils.showProgressDialog(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            loadDataDialog.show();
        }
    }

    /**
     * 获取焦点新闻
     */
    private void getFocusNews() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiRequest api = retrofit.create(ApiRequest.class);
        String token = (String) SPUtil.get(getActivity(), SPKeyConstants.ACCESS_TOKEN, "");
//        Map<String, String> header = new HashMap<>();
//        header.put("Authorization", token);
        Log.i(TAG, "onNext: jail_id   :  " + jail_id);
        api.getNews(jail_id).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<NewsResult>() {
                    @Override public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                        setCacheNews();// 若请求失败  则显示缓存新闻
                        if(srl_refresh.isRefreshing()){
                            srl_refresh.setRefreshing(false);
                        }
                        // 防止少部分异常情况下dismiss dialog出现not attach window manager
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            if (getActivity().isFinishing() || getActivity().isDestroyed()) {
                                return;
                            }
                        }
                        UIUtils.dismissProgressDialog(loadDataDialog);
                    }

                    @Override public void onNext(NewsResult newsResult) {
                        Log.i(TAG, "onNext: jail_id   :  " + jail_id);
                        focus_news_list = new ArrayList<>();
                        allNews = new ArrayList<>();
                        List<NewsResult.News> newses = newsResult.news;
                        for (NewsResult.News news : newses) {
                            if(news.getIsFocus()){
                                focus_news_list.add(news);
                            }
                            allNews.add(news);
                        }
                        Log.i(TAG, "焦点新闻数目:" + focus_news_list.size() +
                                "，总新闻数：" + allNews.size());
                        putNewsToCache();// 设置缓存
                        setRoll();// 设置轮播
                        fillNewsData();// 填充新闻数据
                    }
                });
    }

    /**
     * 设置轮播
     */
    private void setRoll() {
        list_news_title.clear();
        List<String> img_url_list = new ArrayList<>();
        /*if (allNews.size() > 2) {
            list_news_title.add("");list_news_title.add("");list_news_title.add("");
            img_url_list.add("");img_url_list.add("");img_url_list.add("");
        } else if(allNews.size() == 1){
            list_news_title.add("");img_url_list.add("");
        } else if(allNews.size() == 2){
            list_news_title.add("");list_news_title.add("");
            img_url_list.add("");img_url_list.add("");
        }*/
        img_url_list.add("");
        img_url_list.add("");
        img_url_list.add("");
        list_news_title.add("");list_news_title.add("");list_news_title.add("");
        initDot();// 初始化轮播图底部小圆圈
        RollViewPager vp_carousel = new RollViewPager(getActivity(), dotList, new RollViewPager.OnViewClickListener() {
            @Override
            public void viewClick(int position) {
                /*if(allNews.size() > 0) {
                    int i = allNews.get(position).getId();
                    Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                    intent.putExtra("id", i);
                    intent.putExtra("type", 0);// 0是轮播图   1是新闻
                    intent.putExtra("index", position + 1);
                    getActivity().startActivity(intent);
                }else {
                    showToastMsgShort("抱歉，没有数据...");
                }*/
            }
        });
        vp_carousel.initTitle(list_news_title, top_news_title);
        vp_carousel.initImgUrl(img_url_list);
        vp_carousel.startRoll();
        top_news_viewpager.removeAllViews();
        top_news_viewpager.addView(vp_carousel);
    }

    /**
     * 设置缓存焦点新闻
     */
    private void setCacheNews() {
        focus_news_list = new ArrayList<>();
        allNews = new ArrayList<>();
        for (String key : keys){
            NewsResult.News news = FileUtils.getCacheNews(MyApplication.getContext(), key);
            if (news != null){
                allNews.add(news);
                if(news.getIsFocus()){
                    focus_news_list.add(news);
                }
            }
        }
        fillNewsData();
    }

    /**
     * 填充焦新闻数据
     */
    private void fillNewsData() {
        if (focusNewsAdapter == null){
            focusNewsAdapter = new FocusNewsAdapter(focus_news_list, getActivity());
            focusNewsRecyclerView.setLayoutManager(new FullLinearLayout(getActivity(), LinearLayoutManager.VERTICAL, false));
            focusNewsRecyclerView.setAdapter(focusNewsAdapter);
            Log.i(TAG, focusNewsAdapter.getItemCount() + "-------------" + focusNewsRecyclerView.getVisibility());
        }else {
            focusNewsAdapter.setData(focus_news_list);
            focusNewsAdapter.notifyDataSetChanged();
        }
        try {
            UIUtils.dismissProgressDialog(loadDataDialog);// 消掉加载对话框进度条
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(srl_refresh.isRefreshing()){
            srl_refresh.setRefreshing(false);
        }
    }

    /**
     * 缓存
     */
    private void putNewsToCache() {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                for (NewsResult.News news : allNews){
                    subscriber.onNext(FileUtils.cacheNews(news, getActivity(), keys[allNews.indexOf(news)]));
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Boolean>(){
                    @Override public void onNext(Boolean aBoolean) {
                        Log.i(TAG, aBoolean ? "缓存成功" : "缓存失败");
                    }
                });
    }

    private void initDot() {
        dotList.clear();
        dots_ll.removeAllViews();
        for (int i = 0; i < 3; i++) {
            View view = new View(getActivity());
            if (i == 0) {
                view.setBackgroundResource(R.drawable.rb_shape_blue);
            } else {
                view.setBackgroundResource(R.drawable.rb_shape_gray);
            }
            // 指定点的大小
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    getActivity().getResources().getDimensionPixelSize(R.dimen.dot_radius),
                    getActivity().getResources().getDimensionPixelSize(R.dimen.dot_radius));
            // 间距
            layoutParams.setMargins(10, 0, 10, 0);
            dots_ll.addView(view, layoutParams);
            dotList.add(view);
        }
        ll_title_dot.setGravity(Gravity.CENTER);
        ll_title_dot.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden){
            if (loadDataDialog != null && loadDataDialog.isShowing()){
                loadDataDialog.dismiss();
            }
        }
    }
}
