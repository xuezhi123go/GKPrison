/**
 * 
 */
package com.keda.contact.manager;

import com.kedacom.kdv.mt.bean.ContactStatus;
import com.kedacom.kdv.mt.bean.TDeviceOnlineState;
import com.kedacom.kdv.mt.constant.EmMtOnlineState;
import com.kedacom.kdv.mt.constant.EmStateLocal;
import com.pc.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 联系人状态管理器
 * 
 * @author chenj
 * @date 2014-12-25
 */
public class ContactStateManager {

	private static Map<String, ContactStatus> mContactStateMap = new HashMap<String, ContactStatus>();

	/**
	 * 清除状态
	 */
	public synchronized static void cleanStateMap() {
		mContactStateMap.clear();
	}

	/**
	 * 返回状态信息
	 * 
	 * @param jid
	 * @return
	 */
	public static ContactStatus getState(String jid) {
		return mContactStateMap.get(jid);
	}

	/**
	 * 最大状态
	 * 
	 * @param jid
	 * @return
	 */
	public static int getMaxState(String jid) {
		ContactStatus state = getState(jid);
		return state != null ? state.getMaxState().ordinal() : EmStateLocal.offline.ordinal();
	}

	/**
	 * 最大状态
	 * 
	 * @param jid
	 * @return
	 */
	public static EmStateLocal getEmMaxState(String jid) {
		ContactStatus state = getState(jid);
		return state != null ? state.getMaxState() : EmStateLocal.offline;
	}

	/**
	 * 最大状态
	 * 
	 * @param jid
	 * @return
	 */
	public static EmStateLocal getEmMaxState(String jid, EmStateLocal defValue) {
		ContactStatus state = getState(jid);
		return state != null ? state.getMaxState() : defValue;
	}

	/**
	 * 更新状态
	 * 
	 * @param state
	 */
	public synchronized static void updateState(ContactStatus state) {
		if (null == state || StringUtils.isNull(state.getJid())) {
			return;
		}

		mContactStateMap.put(state.getJid(), state);
	}

	/**
	 * 更新状态
	 * 
	 * @param state
	 */
	public synchronized static void updateState(TDeviceOnlineState state) {
		if (null == state || StringUtils.isNull(state.achNO)) {
			return;
		}


		// 无效状态
		if (state.emMaxState == EmMtOnlineState.EM_STATE_INVALID || state.emMaxState == EmMtOnlineState.EM_STATE_END || state.emMaxState == EmMtOnlineState.EM_STATE_OFFLINE) {
			if (mContactStateMap.containsKey(state.achNO)) {
				mContactStateMap.remove(state.achNO);
			}

			return;
		}

		mContactStateMap.put(state.achNO, state.createContactStatus(null));
	}

}
