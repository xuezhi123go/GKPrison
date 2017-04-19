/**
 * @(#)ContactListAdapter.java   2014-8-27
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

package com.keda.main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.keda.contact.manager.ContactStateManager;
import com.keda.vconf.dialog.P2PCallDialog;
import com.kedacom.kdv.mt.bean.Contact;
import com.kedacom.kdv.mt.constant.EmStateLocal;
import com.pc.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
  * 联系人搜索列表Adapter
  * 
  * @author chenj
  * @date 2014-8-27
  */

public class ContactSearchListAdapter extends BaseAdapter {

	protected Context mContext;
	protected List<Contact> mContacts;

	public ContactSearchListAdapter(Context context, List<Contact> contacts) {
		mContext = context;
		mContacts = new ArrayList<Contact>();
		if (contacts != null && !contacts.isEmpty()) {
			mContacts.addAll(contacts);
		}
	}

	@Override
	public int getCount() {
		if (mContacts == null || mContacts.isEmpty()) {
			return 0;
		}

		return mContacts.size();
	}

	/**
	 * 设置联系人数据
	 * 
	 * @param contacts
	 */
	public synchronized void setContacts(List<Contact> contacts) {
		if (null == mContacts) {
			mContacts = new ArrayList<Contact>();
		} else {
			mContacts.clear();
		}

		if (contacts != null) {
			mContacts.addAll(contacts);
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	/**
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = createConvertView(convertView, parent);
		if (null == convertView) {
			return convertView;
		}

		if (convertView.getTag() == null) {
			return convertView;
		}

		final Contact contact = getItem(position);
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		if (null == contact || null == viewHolder) {
			return convertView;
		}

		initViewHolder(viewHolder, contact);
		return convertView;
	}

	protected View createConvertView(View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();

			convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_searchlist_item, parent, false);

			viewHolder.nameTView = (TextView) convertView.findViewById(R.id.nickname);

			convertView.setTag(viewHolder);
		}

		return convertView;
	}

	/**
	 * 初始化ViewHolder的值
	 *
	 * @param viewHolder
	 * @param contact
	 */
	protected void initViewHolder(ViewHolder viewHolder, final Contact contact) {
		if (null == viewHolder || null == contact) {
			return;
		}

		viewHolder.nameTView.setText(contact.getName() + "  (" + getState(contact.getJid()) + ")");
		viewHolder.nameTView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!StringUtils.isNull(contact.getE164())) {
					Dialog dialog = new P2PCallDialog(mContext, contact.getE164());
					dialog.show();
				}
				
			}
		});
	}

	protected String getState(String jid){
		EmStateLocal state = ContactStateManager.getEmMaxState(jid);
		return state.toString();
	}

	protected class ViewHolder {

		TextView nameTView;

	}

	@Override
	public Contact getItem(int position) {
		if (mContacts == null || mContacts.isEmpty()) {
			return null;
		}

		if (position < 0 || position >= mContacts.size()) {
			return null;
		}

		return mContacts.get(position);
	}

	/**
	 * 清除数据
	 */
	public void cleanup() {
		if (null == mContacts || mContacts.isEmpty()) {
			return;
		}

		mContacts.clear();
	}

	/**
	 * @see android.widget.Adapter#getItemId(int)
	 */

	@Override
	public long getItemId(int position) {
		// TODO 该方法尚未实现
		return 0;
	}
}
