package com.gkzxhn.gkprison.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.model.net.bean.Reply;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 监狱长信箱 --> 投诉反馈
 */
public class InterractiveMailboxFragment extends Fragment {
    private String url = "";
    private TextView nonotice;
    private List<Reply> replies = new ArrayList<Reply>();
    private SwipeRefreshLayout srl_refresh;
    private MyAdapter myAdapter;

    private ExpandableListView elv_my_mailbox_list;
    private List<String> my_mailbox_list_title = new ArrayList<String>() {
        {
            add("回复：关于监狱用餐问题的建议");
            add("关于住宿问题的建议");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interractive_mailbox, null);
        elv_my_mailbox_list = (ExpandableListView) view.findViewById(R.id.elv_my_mailbox_list);
        srl_refresh = (SwipeRefreshLayout) view.findViewById(R.id.srl_refresh);
        nonotice = (TextView) view.findViewById(R.id.tv_nothing);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        initData();
        super.onActivityCreated(savedInstanceState);
    }

    private void initData() {
        SharedPreferences sp = getActivity().getSharedPreferences("config", getActivity().MODE_PRIVATE);
        int family_id = sp.getInt("family_id", 1);
        String token = sp.getString("token", "");
        url = Constants.URL_HEAD + "comments?"+ "&family_id=" + family_id;
        getReply();
        srl_refresh.setColorSchemeResources(R.color.theme, R.color.theme, R.color.theme, R.color.theme);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getReply();
            }
        });
    }

    /**
     * 获取列表
     */
    private void getReply() {
        String token = (String) SPUtil.get(MyApplication.getContext(), SPKeyConstants.ACCESS_TOKEN, "");

        OkHttpClient client = new OkHttpClient();
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
                        if (srl_refresh.isRefreshing())
                            srl_refresh.setRefreshing(false);
                        Toast.makeText(getActivity(), "刷新失败,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                replies = analysisReply(response.body().string());

                Collections.sort(replies, new Comparator<Reply>() {
                    @Override
                    public int compare(Reply lhs, Reply rhs) {
                        int heat1 = lhs.getId();
                        int heat2 = rhs.getId();
                        if (heat1 < heat2) {
                            return 1;
                        }
                        return -1;
                    }
                });

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (replies.size() == 0) {
                            nonotice.setVisibility(View.VISIBLE);
                        } else {
                            nonotice.setVisibility(View.GONE);
                        }
                        if (srl_refresh.isRefreshing())
                            srl_refresh.setRefreshing(false);
                        if (myAdapter == null) {
                            myAdapter = new MyAdapter();
                            elv_my_mailbox_list.setAdapter(myAdapter);
                        } else {
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }

    /**
     * 解析
     * @param s
     * @return
     */
    private List<Reply> analysisReply(String s) {
        List<Reply> replies = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(s);
            for (int i = 0; i < jsonArray.length(); i++) {
                Reply reply = new Reply();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                reply.setId(jsonObject.getInt("id"));
                reply.setTitle(jsonObject.getString("title"));
                reply.setContents(jsonObject.getString("contents"));
                reply.setReplies(jsonObject.getString("replies"));
                reply.setReply_date(jsonObject.getString("reply_date"));
                replies.add(reply);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return replies;
    }


    private class MyAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return replies.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.prison_warden_item, null);
                holder = new GroupViewHolder();
                holder.tv_reply_item_title = (TextView) convertView.findViewById(R.id.tv_reply_item_title);
                holder.iv_reply_item = (ImageView) convertView.findViewById(R.id.iv_reply_item);
                convertView.setTag(holder);
            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }
            holder.tv_reply_item_title.setText(replies.get(groupPosition).getTitle());
            if (isExpanded) {
                holder.iv_reply_item.setImageResource(R.drawable.up_gray);
            } else {
                holder.iv_reply_item.setImageResource(R.drawable.down_gray);
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.interactive_mailbox_child, null);
                holder = new ChildViewHolder();
                holder.tv_send_reply = (TextView) convertView.findViewById(R.id.tv_send_reply_contents);
                holder.tv_reply_content = (TextView) convertView.findViewById(R.id.tv_reply_content);
                holder.tv_warden_signature = (TextView) convertView.findViewById(R.id.tv_warden_signature);
                holder.tv_message_time = (TextView) convertView.findViewById(R.id.tv_message_time);
                convertView.setTag(holder);
            } else {
                holder = (ChildViewHolder) convertView.getTag();
            }
            holder.tv_send_reply.setText(replies.get(groupPosition).getContents());
            holder.tv_reply_content.setText(replies.get(groupPosition).getReplies());
            holder.tv_message_time.setText(replies.get(groupPosition).getReply_date());
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    private static class GroupViewHolder {
        TextView tv_reply_item_title;
        ImageView iv_reply_item;
    }

    private static class ChildViewHolder {
        TextView tv_reply_content;
        TextView tv_warden_signature;
        TextView tv_message_time;
        TextView tv_send_reply;
    }
}
