package com.keda.main;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.keda.sky.app.PcAppStackManager;
import com.keda.vconf.bean.VConf;
import com.keda.vconf.dialog.MultiCallDialog;
import com.keda.vconf.manager.VConferenceManager;
import com.kedacom.kdv.mt.api.Conference;
import com.kedacom.kdv.mt.constant.EmConfListType;

public class VConfListActivity extends ListActivity {

	private ListView mListView;
	private VConfRoomAdapter mVConfAdapter;
	private MultiCallDialog mMultiCallDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PcAppStackManager.Instance().pushActivity(this);
		mListView = getListView();
		queryConfList();
		findviews();
		initComponentValue();
	}

	/**
	 * 请求获取会议列表
	 * 
	 * <pre>
	 * </pre>
	 */
	public void queryConfList() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// 查询正在召开的会议
				Conference.requestConfListByType(EmConfListType.Hold);

				try {
					// 100ms后，查询预定义会议
					Thread.sleep(100);
					Conference.requestConfListByType(EmConfListType.Book);
				} catch (Exception e) {
				}

				// try {
				// // 100ms后，查询空闲会议
				// Thread.sleep(100);
				// ConfLibCtrl.confGetConfListCmd(EmConfListType.Tmplt);
				// } catch (Exception e) {
				// }
			}
		}).start();
	}

	private void findviews() {

	}

	private void initComponentValue() {
		mVConfAdapter = new VConfRoomAdapter(this, null);
		mListView.setAdapter(mVConfAdapter);
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (mVConfAdapter == null) {
			return;
		}

		VConf vconf = mVConfAdapter.getItem(position);
		if (null == vconf) {
			return;
		}

		if (mMultiCallDialog == null) {
			mMultiCallDialog = new MultiCallDialog(VConfListActivity.this, vconf.achConfE164);
		}
		mMultiCallDialog.show();
	}

	@Override
	protected void onDestroy() {
		PcAppStackManager.Instance().popActivity(this, false);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	public void showList() {
		if (null == mListView) {
			return;
		}

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (VConferenceManager.getVConfs(true).size() < 1) {
					Toast.makeText(VConfListActivity.this, "无可用的会议室", Toast.LENGTH_SHORT).show();
				} else {
					mVConfAdapter.setVConf(VConferenceManager.getVConfs(true));
					mVConfAdapter.notifyDataSetChanged();
				}

			}
		});
	}
}
