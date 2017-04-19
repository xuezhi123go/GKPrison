package com.gkzxhn.gkprison.widget.view.auto;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.utils.NomalUtils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengneng on 2016/1/9.
 */
public class AutoTextAdapater implements ListAdapter, Filterable {

    List<String> strs;
    Context conx;
    MyFilter myFilter;
    String tempKeyString;

    public AutoTextAdapater(List<String> strs, Context conx) {
        super();
        this.strs = strs;
        this.conx = conx;
    }

    @Override
    public Filter getFilter() {
        //自定义的拦截器，对包含的关键字进行处理
        if (null == myFilter) {
            myFilter = new MyFilter();
        }
        return myFilter;
    }

    @Override
    public int getCount() {
        return strs.size();
    }

    @Override
    public Object getItem(int position) {
        return strs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String temp1 = strs.get(position);
        ViewHolder holder;
        if(convertView == null){
            convertView = View.inflate(conx, R.layout.suggest_item, null);
            holder = new ViewHolder();
            holder.online_user_list_item_textview = (TextView) convertView.findViewById(R.id.online_user_list_item_textview);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.online_user_list_item_textview.setText(temp1);
        return convertView;
    }

    class ViewHolder {
        TextView online_user_list_item_textview;
    }


    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }


    class MyFilter extends Filter {
        List<String> strsContains = new ArrayList<>();

        public MyFilter() {
            super();
            strsContains.clear();
            strsContains.addAll(strs);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.i(" constraint ", "constraint == " + constraint);
            //filterREsults是filter的一个结果对象，里面只包括两个成员属性，Object 和 count
            FilterResults result = new FilterResults();

            //定义一个集合来保存数组中存在的关键字的字符串
            ArrayList strsTemp = new ArrayList();

            //在这里可以获取autoCompeted中输入的信息
            //把字符串中包含这个关键字的item返回给adapter.
            if (null != constraint && constraint.length() > 0) {
                for (int i = 0; i < strsContains.size(); i++) {
                    String tempstr = strsContains.get(i);
                    //同一做大小写的处理
                    if (tempstr.toLowerCase().contains(constraint.toString()
                            .toLowerCase())){//包含关键字的添加进去
                        strsTemp.add(tempstr);
                    }
                }
                result.values = strsTemp;
                result.count = strsTemp.size();
            }

            //这个结果集 将会返回给 publishResults 方法中的 FilterResults results这个参数 所以我们在下面获取
            return result;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList tempList = (ArrayList) results.values;
            if (null != tempList) {
                List<String> strsTemps = new ArrayList<>();
                for (int i = 0; i < tempList.size(); i++) {
                    strsTemps.add((String) tempList.get(i));
                }
                strs = strsTemps;
                //这个时候输入的关键字
                tempKeyString = constraint.toString();
            }
        }
    }
}
