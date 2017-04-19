/**
 * @(#)ContactSearchList.java   2014-8-27
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

package com.keda.main;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.gkzxhn.gkprison.R;
import com.keda.sky.app.GKStateMannager;
import com.keda.sky.app.base.PcActivity;
import com.kedacom.kdv.mt.api.RmtContact;
import com.kedacom.kdv.mt.bean.Contact;
import com.pc.utils.StringUtils;
import com.pc.utils.ValidateUtils;

import java.util.ArrayList;
import java.util.List;

/**
  * 联系人搜索列表
  * 
  * @author chenj
  * @date 2014-8-27
  */

public class ContactSearchListUI extends PcActivity {

	private EditText mSearchEdit;
	private Button mSearchBtn;

	private ListView mListView;

	private ContactSearchListAdapter mAdapter;

	// 当前搜索的关键字
	private String mCurrSearchTextStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_add_contact);
		onViewCreated();
	}

	public void findViews() {
		mSearchEdit = (EditText) findViewById(R.id.search_edit);
		mListView = (ListView) findViewById(R.id.sListView);
		mSearchBtn = (Button) findViewById(R.id.search_btn);
	}

	public void initComponentValue() {

		mAdapter = new ContactSearchListAdapter(ContactSearchListUI.this, null);
		mListView.setAdapter(mAdapter);

	}

	public void registerListeners() {

		// 搜索联系人
		mSearchBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				searchContact();
			}
		});
	}

	/**
	 * 搜索联系人
	 */
	private void searchContact() {

		mCurrSearchTextStr = mSearchEdit.getText().toString();
		if (StringUtils.isNull(mCurrSearchTextStr)) {
			return;
		}

		mCurrSearchTextStr = mCurrSearchTextStr.toLowerCase();
		// 搜索联系人
		RmtContact.getUserListByStrReq(mCurrSearchTextStr, 0, GKStateMannager.mE164);
	}

	/**
	 * 加载更多
	 */
	/*
	private void searchContactMore() {
	if (TextUtils.isEmpty(mCurrSearchTextStr) || null == mAdapter || mAdapter.isEmpty()) return;

	if (mIsSearching || mIsSearchingMore) {
		return;
	}

	View loadInfoTV = mFooterView.findViewById(R.id.loadInfoTV);
	View moreprogress = mFooterView.findViewById(R.id.moreprogress);

	if (null != loadInfoTV) loadInfoTV.setVisibility(View.GONE);
	if (null != moreprogress) moreprogress.setVisibility(View.VISIBLE);

	mIsSearching = false;
	mIsSearchingMore = true;
	RmtContactLibCtrl.getUserListByStrReq(mCurrEditTextStr, mAdapter.getContactsCount());
	}*/

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 更新列表
	 * 
	 * @param seachrKey
	 * @param contacts
	 */
	public void updateListView(final String seachrKey, final int totalCount, final List<Contact> contacts) {
		if (TextUtils.isEmpty(seachrKey) || !StringUtils.isEquals(seachrKey, mCurrSearchTextStr)) {
			return;
		}

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// 搜索一个e164为空时，显示出e164
				if ((null == contacts || contacts.isEmpty()) && ValidateUtils.isE164(seachrKey)) {
					Contact contact = new Contact();
					contact.setE164(seachrKey);
					contact.setMarkName(seachrKey);

					List<Contact> tmpContacts = new ArrayList<Contact>();
					tmpContacts.add(contact);
					mAdapter.setContacts(tmpContacts);
				} else {
					mAdapter.setContacts(contacts);
				}

				mAdapter.notifyDataSetChanged();
			}
		});
	}
}
