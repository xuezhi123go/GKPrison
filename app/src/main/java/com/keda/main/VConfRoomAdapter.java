/**
 * @(#)VConfRoomContentAdapter.java   2014-8-26
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

package com.keda.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.keda.sky.app.PcAppStackManager;
import com.keda.vconf.bean.VConf;
import com.keda.vconf.manager.VConferenceManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
  * 会议室 Adapter
  */

public class VConfRoomAdapter extends BaseAdapter {

	private Context mContext;
	private List<VConf> mVConfList;

	public VConfRoomAdapter(Context context, List<VConf> list) {
		super();

		mContext = context;
		mVConfList = new ArrayList<VConf>();

		if (null != list && !list.isEmpty()) {
			mVConfList.addAll(list);
		}
	}

	/**
	 * 设置会议室
	 * 
	 * @param list
	 */
	public void setVConf(List<VConf> list) {
		if (mVConfList == null) {
			mVConfList = new ArrayList<VConf>();
		} else {
			mVConfList.clear();
		}

		if (null != list && !list.isEmpty()) {
			mVConfList.addAll(list);
		}
	}

	/**
	 * 清除数据
	 */
	public void clearVConf() {
		if (null == mVConfList) return;

		mVConfList.clear();
	}

	/**
	 * 添加会议室
	 * 
	 * @param vconf
	 */
	public void appendTo(VConf vconf) {
		appendTo(-1, vconf);
	}

	/**
	 * 添加会议室
	 * 
	 * @param location
	 * @param vconf
	 */
	public void appendTo(int location, VConf vconf) {
		if (null == vconf) {
			return;
		}

		if (null == mVConfList) {
			mVConfList = new ArrayList<VConf>();
		}

		if (mVConfList.contains(vconf)) {
			return;
		}
		if (location < 0 || location >= mVConfList.size()) {
			mVConfList.add(vconf);
		} else {
			mVConfList.add(location, vconf);
		}
	}

	/**
	 * 添加会议室
	 * 
	 * @param list
	 */
	public void appendTo(List<VConf> list) {
		appendTo(-1, list);
	}

	/**
	 * 添加会议室
	 * 
	 * @param location
	 * @param list
	 */
	public void appendTo(int location, List<VConf> list) {
		if (null == list || list.isEmpty()) {
			return;
		}

		if (null == mVConfList) {
			mVConfList = new ArrayList<VConf>();
		}

		if (location < 0 || location >= mVConfList.size()) {
			mVConfList.addAll(list);
		} else {
			mVConfList.addAll(location, list);
		}
	}

	/**
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		if (mVConfList == null || mVConfList.isEmpty()) {
			return 0;
		}

		return mVConfList.size();
	}

	/**
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public VConf getItem(int position) {
		if (mVConfList == null || mVConfList.isEmpty()) {
			return null;
		}

		if (position < 0 || position >= mVConfList.size()) {
			return null;
		}

		return mVConfList.get(position);
	}

	/**
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		if (mVConfList == null || mVConfList.isEmpty()) {
			return 0;
		}

		return position;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	public void notifyDataSetChanged(Collection<VConf> vconfs) {
		mVConfList.clear();

		if (null != vconfs && !vconfs.isEmpty()) {
			mVConfList.addAll(vconfs);
		}

		super.notifyDataSetChanged();
	}

	/**
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.vconf_list_item, null);

			holder = new ViewHolder();

			holder.mTypeSign = ((ImageView) convertView.findViewById(R.id.type));
			holder.mTitleTextView = ((TextView) convertView.findViewById(R.id.title));
			holder.mVconfDetalBtn = ((Button) convertView.findViewById(R.id.getVconfDetail));

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final VConf vconf = (VConf) getItem(position);
		if (vconf == null) {
			return convertView;
		}

		// holder.mE164TextView.setText(vconf.getConfE164());
		holder.mTitleTextView.setText(vconf.getAchConfName());
		holder.mTypeSign.setImageResource(vconf.getTypeResouceId());

		holder.mVconfDetalBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				VConferenceManager.openVConfDetails(PcAppStackManager.Instance().currentActivity(), vconf.achConfE164);
			}
		});

		return convertView;
	}

	private static class ViewHolder {

		// 会议室类型
		private ImageView mTypeSign;

		// 会议室Title
		private TextView mTitleTextView;

		// 会议室号码
		private TextView mE164TextView;

		// 查看会议详情
		private Button mVconfDetalBtn;
	}

}
