package com.gkzxhn.gkprison.ui.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.model.net.bean.NewsResult;
import com.gkzxhn.gkprison.ui.activity.normal_activity.NewsDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: Huang ZN
 * Date: 2016/12/29
 * Email:943852572@qq.com
 * Description:
 */
public class FocusNewsAdapter extends RecyclerView.Adapter<FocusNewsAdapter.MyViewHolder> {

    private static final String TAG = "FocusNewsAdapter";
    private List<NewsResult.News> focusNewsList;
    private Context mContext;
    private boolean isCache;

    public FocusNewsAdapter(List<NewsResult.News> list, Context context){
        this.focusNewsList = list;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.main_focus_news_item, parent, false));
    }

    @Override
    public int getItemCount() {
        return focusNewsList.size() > 3 ? 3 : focusNewsList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String imgUrl = Constants.RESOURSE_HEAD + focusNewsList
                .get(position).getImage();
        Picasso.with(mContext).load(imgUrl).error(R.drawable.default_img).into(holder.iv_news_icon);
        Log.i(TAG, "onBindViewHolder: focusNewsImg----- " + imgUrl);
        holder.tv_news_title.setText(Html.fromHtml(focusNewsList.get(position).getTitle()));
        holder.tv_news_content.setText(Html.fromHtml(focusNewsList.get(position).getContents()));
        holder.ll_home_news1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NewsDetailActivity.class);
                intent.putExtra("type", 1);// 0是轮播图   1是新闻
                intent.putExtra("id", focusNewsList.get(position).getId());
                mContext.startActivity(intent);
            }
        });
    }

    public void setData(List<NewsResult.News> focus_news_list) {
        focusNewsList = focus_news_list;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ll_home_news1) LinearLayout ll_home_news1;
        @BindView(R.id.iv_news_icon) ImageView iv_news_icon;
        @BindView(R.id.tv_news_title) TextView tv_news_title;
        @BindView(R.id.tv_news_content) TextView tv_news_content;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
