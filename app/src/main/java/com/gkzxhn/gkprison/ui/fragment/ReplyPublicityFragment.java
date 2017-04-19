package com.gkzxhn.gkprison.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.model.net.bean.NewsResult;
import com.gkzxhn.gkprison.ui.activity.normal_activity.NewsDetailActivity;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.Utils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 公示信息
 */
public class ReplyPublicityFragment extends Fragment {

    private SwipeRefreshLayout srl_refresh;
    private ListView reply_list;
    private MyAdapter adapter;
    private String url = "";
    private int jail_id;
    private SharedPreferences sp;
    private TextView nonotice;
    private List<NewsResult.News> allnews = new ArrayList<>();
    private List<NewsResult.News> replys = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_reply_publicity,null);
        reply_list = (ListView)view.findViewById(R.id.reply_list);
        nonotice = (TextView)view.findViewById(R.id.tv_nothing);
        srl_refresh = (SwipeRefreshLayout) view.findViewById(R.id.srl_refresh);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        jail_id = sp.getInt("jail_id",0);
        url = Constants.URL_HEAD + "jails/"+jail_id+"/news";
//        url = Constants.URL_HEAD + "news?jail_id="+jail_id;
        getNews();
        initdate();
        srl_refresh.setColorSchemeResources(R.color.theme);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNews();
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * items点击事件
     */
    private void initdate() {
        reply_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int i = replys.get(position).getId();
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("type", 1);// 0是轮播图   1是新闻
                intent.putExtra("id", i);
                startActivity(intent);
            }
        });
    }

    private void getNews() {
        if(Utils.isNetworkAvailable(getActivity())) {
            OkHttpClient client = new OkHttpClient();
            String token = (String) SPUtil.get(MyApplication.getContext(), SPKeyConstants.ACCESS_TOKEN, "");

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("authorization",token)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(srl_refresh.isRefreshing())
                                srl_refresh.setRefreshing(false);
                            Toast.makeText(getActivity().getApplicationContext(), "刷新数据失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    allnews = analysisNews(response.body().string());
                    replys.clear();
                    for (int i = 0;i < allnews.size();i++){
                        NewsResult.News news = allnews.get(i);
                        if (news.getType_id() == 3){
                            replys.add(news);
                        }
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (replys.size() == 0){
                                nonotice.setVisibility(View.VISIBLE);
                            }else {
                                nonotice.setVisibility(View.GONE);
                            }
                            if(srl_refresh.isRefreshing())
                                srl_refresh.setRefreshing(false);
                            if(adapter == null){
                                adapter = new MyAdapter();
                                reply_list.setAdapter(adapter);
                            }else {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            });
        }else {
           Toast.makeText(getActivity().getApplicationContext(),"没有网络",Toast.LENGTH_SHORT).show();
        }
    }


    private List<NewsResult.News> analysisNews(String s){

        NewsResult newsResult = new Gson().fromJson(s, NewsResult.class);
        List<NewsResult.News> newses = newsResult.news;

        /*List<NewsResult.News> newses = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(s);
            for (int i = 0;i < jsonArray.length();i++){
                NewsResult.News news = new NewsResult.News();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                news.setId(jsonObject.getInt("id"));
                news.setTitle(jsonObject.getString("title"));
                news.setContents(jsonObject.getString("contents"));
                news.setJail_id(jsonObject.getInt("jail_id"));
                news.setImage(jsonObject.getString("image_url"));
                news.setType_id(jsonObject.getInt("type_id"));
                newses.add(news);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        return newses;
    }

 private class MyAdapter extends BaseAdapter{

     @Override
     public int getCount() {
         return replys.size();
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
         if (convertView == null){
             convertView = View.inflate(getActivity(), R.layout.laws_regulations_item,null);
             holder = new ViewHolder();
             holder.textView = (TextView)convertView.findViewById(R.id.tv_laws_regulations_item);
             convertView.setTag(holder);
         }else {
             holder = (ViewHolder)convertView.getTag();
         }
         holder.textView.setText(replys.get(position).getTitle());
         return convertView;
     }
 }
    private  static class ViewHolder{
        TextView textView;
    }

}
