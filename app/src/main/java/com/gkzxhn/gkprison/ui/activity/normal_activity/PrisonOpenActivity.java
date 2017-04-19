package com.gkzxhn.gkprison.ui.activity.normal_activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.model.net.api.ApiRequest;
import com.gkzxhn.gkprison.model.net.bean.NewsResult;
import com.gkzxhn.gkprison.utils.CustomUtils.RxUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.SystemUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.UIUtils;
import com.gkzxhn.gkprison.widget.view.RefreshLayout;
import com.gkzxhn.gkprison.widget.view.RollViewPager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 狱务公开页面
 */
public class PrisonOpenActivity extends BaseActivityNew {

    private static final String TAG = "PrisonOpenActivity";
    @BindView(R.id.lv_prison_open) ListView lv_prison_open;
    @BindView(R.id.swipe_container) RefreshLayout mRefreshLayout;
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.rl_back) RelativeLayout rl_back;

    private RollViewPager vp_carousel;
    private View layout_roll_view;
    private LinearLayout dots_ll;
    private TextView top_news_title;
    private LinearLayout top_news_viewpager;
    private final List<String> list_news_title = new ArrayList<>();
    private int jail_id;
    private List<NewsResult.News> newsList = new ArrayList<>();
    private ProgressDialog getNews_Dialog;
    private MyAdapter myAdapter;// 新闻列表适配器
    private View footerLayout;
    private TextView textMore;
    private ProgressBar progressBar;
    private boolean isLoadingMore = false;

    private Subscription getNewsSubscription;

    /**
     * 相关ui操作
     */
    private void checkUI() {
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        if (isLoadingMore) {
            mRefreshLayout.setLoading(false);
            textMore.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            isLoadingMore = false;
        }
    }

    /**
     * 设置轮播图
     *
     * @param imgurl_list
     */
    private void setCarousel(List<String> imgurl_list) {
        if (newsList.size() > 3) {
            imgurl_list.add(Constants.RESOURSE_HEAD + newsList.get(0).getImage());
            imgurl_list.add(Constants.RESOURSE_HEAD + newsList.get(1).getImage());
            imgurl_list.add(Constants.RESOURSE_HEAD + newsList.get(2).getImage());
            imgurl_list.add(Constants.RESOURSE_HEAD + newsList.get(3).getImage());
            list_news_title.clear();
            list_news_title.add(newsList.get(0).getTitle());
            list_news_title.add(newsList.get(1).getTitle());
            list_news_title.add(newsList.get(2).getTitle());
            list_news_title.add(newsList.get(3).getTitle());
        } else if (newsList.size() == 3) {
            imgurl_list.add(Constants.RESOURSE_HEAD + newsList.get(0).getImage());
            imgurl_list.add(Constants.RESOURSE_HEAD + newsList.get(1).getImage());
            imgurl_list.add(Constants.RESOURSE_HEAD + newsList.get(2).getImage());
            list_news_title.clear();
            list_news_title.add(newsList.get(0).getTitle());
            list_news_title.add(newsList.get(1).getTitle());
            list_news_title.add(newsList.get(2).getTitle());
        } else if (newsList.size() == 2) {
            imgurl_list.add(Constants.RESOURSE_HEAD + newsList.get(0).getImage());
            imgurl_list.add(Constants.RESOURSE_HEAD + newsList.get(1).getImage());
            list_news_title.clear();
            list_news_title.add(newsList.get(0).getTitle());
            list_news_title.add(newsList.get(1).getTitle());
        } else if (newsList.size() == 1) {
            imgurl_list.add(Constants.RESOURSE_HEAD + newsList.get(0).getImage());
            list_news_title.clear();
            list_news_title.add(newsList.get(0).getTitle());
        }
        initDot();// 初始化轮播图底部小圆圈
        vp_carousel = new RollViewPager(getApplicationContext(), dotList, new RollViewPager.OnViewClickListener() {
            @Override
            public void viewClick(int position) {
                int i = newsList.get(position).getId();
                Intent intent = new Intent(PrisonOpenActivity.this, NewsDetailActivity.class);
                intent.putExtra("type", 1);// 0是轮播图   1是新闻
                intent.putExtra("id", i);
                startActivity(intent);
            }
        });
        vp_carousel.initImgUrl(imgurl_list);
        vp_carousel.initTitle(list_news_title, top_news_title);
        vp_carousel.startRoll();
        top_news_viewpager.removeAllViews();
        top_news_viewpager.addView(vp_carousel);
        if (getNews_Dialog.isShowing()) {
            getNews_Dialog.dismiss();
        }
    }

    /**
     * 轮播图导航点集合
     */
    private List<View> dotList = new ArrayList<>();

    @Override
    public int setLayoutResId() {
        return R.layout.activity_prison_open;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        findViews();// 初始化view
        tv_title.setText(R.string.prison_open);
        rl_back.setVisibility(View.VISIBLE);
        jail_id = (int) SPUtil.get(this, SPKeyConstants.JAIL_ID, 0);
        getNews(0);// 获取新闻
        lv_prison_open.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    int i = newsList.get(position - 1).getId();
                    Intent intent = new Intent(PrisonOpenActivity.this, NewsDetailActivity.class);
                    intent.putExtra("type", 1);// 0是轮播图   1是新闻
                    intent.putExtra("id", i);
                    startActivity(intent);
                }
            }
        });
        mRefreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNews(1);
            }
        });
        mRefreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                getNews(2);
            }
        });
        textMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNews(2);
            }
        });
    }

    /**
     * find views
     */
    private void findViews() {
        layout_roll_view = View.inflate(getApplicationContext(), R.layout.layout_roll_view, null);
        dots_ll = (LinearLayout) layout_roll_view.findViewById(R.id.dots_ll);
        top_news_title = (TextView) layout_roll_view.findViewById(R.id.top_news_title);
        top_news_viewpager = (LinearLayout) layout_roll_view.findViewById(R.id.top_news_viewpager);
        lv_prison_open.addHeaderView(layout_roll_view);
        footerLayout = View.inflate(this, R.layout.listview_footer, null);
        textMore = (TextView) footerLayout.findViewById(R.id.text_more);
        progressBar = (ProgressBar) footerLayout.findViewById(R.id.load_progress_bar);
        lv_prison_open.addFooterView(footerLayout);
        mRefreshLayout.setChildView(lv_prison_open);
        mRefreshLayout.setColorSchemeResources(R.color.theme);
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
        UIUtils.dismissProgressDialog(getNews_Dialog);
        RxUtils.unSubscribe(getNewsSubscription);
        super.onDestroy();
    }


    /**
     * 获取新闻
     */
    private void getNews(final int getType) {
        if (!SystemUtil.isNetWorkUnAvailable()) {
            if (getType == 0) {// 进入页面
                getNews_Dialog = UIUtils.showProgressDialog(this);
            }else if (getType == 2) {// 上拉加载
                textMore.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                isLoadingMore = true;
            }
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.URL_HEAD)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiRequest api = retrofit.create(ApiRequest.class);
            String token = (String) SPUtil.get(this, SPKeyConstants.ACCESS_TOKEN, "");
            Map<String, String> header = new HashMap<>();
            header.put("authorization", token);
            getNewsSubscription = api.getNews(jail_id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SimpleObserver<NewsResult>() {
                        @Override public void onError(Throwable e) {
                            Log.e(TAG, "get news failed : " + e.getMessage());
                            ToastUtil.showShortToast(getString(R.string.load_data_failed));
                            UIUtils.dismissProgressDialog(getNews_Dialog);
                            checkUI();// 相关ui操作
                        }

                        @Override public void onNext(NewsResult newsResult) {
                            android.util.Log.i(TAG, "onNext: newsResult--  " + newsResult);
                            newsList.clear();
                            List<NewsResult.News> newses = newsResult.news;
                            for (NewsResult.News news : newses){
                                Log.i(TAG, news.toString());
                                if(news.getType_id() == 1){
                                    newsList.add(news);
                                }
                            }
                            sortNews();// 将新闻按新闻id排序
                            List<String> imgurl_list = new ArrayList<>();
                            setCarousel(imgurl_list); // 设置轮播
                            if (myAdapter == null) {
                                myAdapter = new MyAdapter();
                                lv_prison_open.setAdapter(myAdapter);
                            } else {
                                myAdapter.notifyDataSetChanged();
                            }
                            checkUI();// 相关ui操作
                            if(getType == 1){
                                ToastUtil.showShortToast(getString(R.string.refresh_success));
                            }
                        }
                    });
        } else {
            ToastUtil.showShortToast(getString(R.string.net_broken));
        }
    }

    /**
     * 将新闻按id排序
     */
    private void sortNews() {
        Collections.sort(newsList, new Comparator<NewsResult.News>() {
            @Override
            public int compare(NewsResult.News lhs, NewsResult.News rhs) {
                int heat1 = lhs.getId();
                int heat2 = rhs.getId();
                if (heat1 < heat2) {
                    return 1;
                }
                return -1;
            }
        });
    }

    /**
     * 初始化轮播小圆点
     */
    private void initDot() {
        dotList.clear();
        dots_ll.removeAllViews();
        for (int i = 0; i < list_news_title.size(); i++) {
            View view = new View(getApplicationContext());
            if (i == 0) {
                view.setBackgroundResource(R.drawable.rb_shape_blue);
            } else {
                view.setBackgroundResource(R.drawable.rb_shape_gray);
            }
            // 指定点的大小
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.dot_radius), getResources().getDimensionPixelSize(R.dimen.dot_radius));
            // 间距
            layoutParams.setMargins(10, 0, 10, 0);
            dots_ll.addView(view, layoutParams);
            dotList.add(view);
        }
    }

    @OnClick(R.id.rl_back)
    public void onClick(View v) {
        finish();
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return newsList.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.prison_open_item, null);
                holder = new ViewHolder();
                holder.iv_home_news_icon = (ImageView) convertView.findViewById(R.id.iv_home_news_icon);
                holder.tv_home_news_title = (TextView) convertView.findViewById(R.id.tv_home_news_title);
                holder.tv_home_news_content = (TextView) convertView.findViewById(R.id.tv_home_news_content);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String t = Constants.RESOURSE_HEAD + newsList.get(position).getImage();
            Picasso.with(holder.iv_home_news_icon.getContext()).load(t).into(holder.iv_home_news_icon);
            holder.tv_home_news_title.setText(Html.fromHtml(newsList.get(position).getTitle()));
            holder.tv_home_news_content.setText(Html.fromHtml(newsList.get(position).getContents()));
            return convertView;
        }
    }

    private static class ViewHolder {
        ImageView iv_home_news_icon;
        TextView tv_home_news_title;
        TextView tv_home_news_content;
        ImageView iv_home_news_go;
    }
}
