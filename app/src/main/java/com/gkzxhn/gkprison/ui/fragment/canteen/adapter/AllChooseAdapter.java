package com.gkzxhn.gkprison.ui.fragment.canteen.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.model.net.bean.CategoriesInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Huang ZN
 * Date: 2017/1/11
 * Email:943852572@qq.com
 * Description:选择分类条目
 */

public class AllChooseAdapter extends BaseAdapter {

    private static final String[] CHOOSE_ITEM = {"全部分类", "洗涤日化", "食品", "服饰鞋帽"};

    private Context mContext;
    private List<CategoriesInfo.CategoriesBean> categories = new ArrayList<>();
    private HashMap<Integer, Integer> mPosition2Id = new HashMap<>();
    private HashMap<Integer, String> mPosition2title = new HashMap<>();

    public AllChooseAdapter(Context context){
        mContext = context;
    }

    @Override
    public int getCount() {
        return categories.size();
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
        Holder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.choose_item, null);
            holder = new Holder();
            holder.textView = (TextView) convertView.findViewById(R.id.tv_fenlei);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.textView.setText(categories.get(position).title);
        mPosition2Id.put(position, categories.get(position).id);
        mPosition2title.put(position, categories.get(position).title);
        return convertView;
    }

    public HashMap<Integer, Integer> getPosition2Id() {
        return mPosition2Id;
    }

    public HashMap<Integer, String> getPosition2title() {
        return mPosition2title;
    }

    /**
     * 设置数据
     * @param categories
     */
    public void setData(List<CategoriesInfo.CategoriesBean> categories) {
        this.categories = categories;
    }

    private class Holder {
        TextView textView;
    }
}
