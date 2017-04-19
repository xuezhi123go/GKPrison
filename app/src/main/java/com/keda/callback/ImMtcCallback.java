/**
 * 
 */
package com.keda.callback;

import android.app.Activity;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.keda.contact.manager.ContactStateManager;
import com.keda.main.ContactSearchListUI;
import com.keda.sky.app.PcAppStackManager;
import com.kedacom.kdv.mt.api.IM;
import com.kedacom.kdv.mt.bean.Contact;
import com.kedacom.kdv.mt.bean.ContactStatus;
import com.kedacom.kdv.mt.bean.MemberInfo;
import com.kedacom.kdv.mt.bean.TDeviceOnlineState;
import com.kedacom.kdv.mt.bean.TImMemberInfo;
import com.kedacom.kdv.mt.bean.TImMemberList;
import com.kedacom.kdv.mt.bean.TImUserStateEx;
import com.kedacom.kdv.mt.bean.TImUserStateListEx;
import com.kedacom.kdv.mt.bean.TMTWbParseKedaEntUser;
import com.kedacom.kdv.mt.bean.TMTWbParseKedaEntUsers;
import com.kedacom.kdv.mt.bean.TMTWbParseKedaSearchUsers;
import com.kedacom.kdv.mt.constant.EmMtMemberType;
import com.pc.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * IM mtc call back
 * 
 * @author chenj
 * @date 2014-12-22
 */
public class ImMtcCallback {

	//=> For SDM-00037552 added by gaofan_kd7331 2015-10-15 21:26:18
	private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	//<= For SDM-00037552 added by gaofan_kd7331 2015-10-15 21:26:18
	private final static ReentrantLock lockForContactAndGroup = new ReentrantLock();

	public static List<String> groupSnList; //组的序列号
	public static int groupNumber; //组的个数

	public static List<Contact> mContacts;
	public static List<String> mJids;

	/**
	 * 通知XMPP已准备好接受组织架构和离线讨论组创建、成员、成员状态等消息推送
	 * 
	 * {"mtapi":{"head":{"eventid":5131,"eventname":"ImSetReadyRsp","SessionID": "1"},
	 *		"body":{
	 *			"dwErrID" : 0,
	 *			"dwHandle" : 1520324616,
	 *			"dwReserved" : 0
	 *		}
	 *	}}
	 * @param jsonBodyObj
	 *//*
	public static void parseImSetReadyReq(JSONObject jsonBodyObj) {
		if (null == jsonBodyObj) {
			return;
		}

		try {
			int errId = -1;
			if (jsonBodyObj.has("dwErrID")) {
				errId = jsonBodyObj.getInt("dwErrID");
			}
		} catch (Exception e) {
			if (PcLog.isPrint) Log.e("Test", "parseImSetReadyReq", e);
		}
	}	*/
	// 锁，比synchronized 更强大
	private final static ReentrantLock lockForQueryGroupOrMember = new ReentrantLock();

	/**
	 * 查询组信息
	 * 
	 * <pre>
	 * 	{"mtapi":{"head":{"eventid":5037,"eventname":"ImQuerySubGroupInfoByGroupSnNtf","SessionID": "1"},
	 * 		"body":{
	 * 			"AssParam" : {
	 * 				"atArray" : [
	 * 								{ "achGroupName" : "��", "achGroupSn" : "2",  "achParentGroupSn" : "0",  "wGroupLevel" : 1 },
	 *          					...
	 *          				],
	 *         		 "dwArraySize" : 2
	 *         		 },
	 *         "MainParam" : { "dwHandle" : 1504629536 }
	 *        }
	 *  }}
	 * @param jsonBodyObj
	 */
	public static void parseImQuerySubGroupInfoByGroupSnNtf(JSONObject jsonBodyObj) {
		if (null == jsonBodyObj) return;

		try {
			lockForQueryGroupOrMember.lock();

			if (IM.imHandle != jsonBodyObj.getJSONObject(MyMtcCallback.KEY_MainParam).getInt(MyMtcCallback.KEY_dwHandle)) {
				return;
			}

			JSONArray jsonArr = jsonBodyObj.getJSONObject(MyMtcCallback.KEY_AssParam).getJSONArray("atArray");
			if (null == jsonArr || jsonArr.length() == 0) {
				return;
			}

			if (null == groupSnList) {
				groupSnList = new ArrayList<String>();
				mContacts = new ArrayList<Contact>();
				mJids = new ArrayList<String>();
			}
			groupSnList.clear();
			mContacts.clear();
			mJids.clear();
			groupNumber = 0;
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject jobj = jsonArr.getJSONObject(i);
				if (null == jobj) continue;
				if (!jobj.has("achGroupSn") || !jobj.has("achGroupName")) continue;

				String gName = jobj.getString("achGroupName");
				String gSn = jobj.getString("achGroupSn");
				String groupLevel = jobj.getString("wGroupLevel");
				String parentGroupSn = jobj.getString("achParentGroupSn");
				if (StringUtils.isNull(gName) || StringUtils.isNull(gSn)) continue;
				if (groupSnList.contains(gSn)) {
					continue;
				}
				groupSnList.add(gSn);
			}
			groupNumber = groupSnList.size();

		} catch (Exception e) {
		} finally {
			lockForQueryGroupOrMember.unlock();
		}
	}

	/**
	 * 查询组Finish信息
	 * 
	 * <pre>
	 * 	{"mtapi":{"head":{"eventid":5038,"eventname":"ImQuerySubGroupInfoByGroupSnFinNtf","SessionID": "1"},
	 * 		"body":{
	 * 			"AssParam" : {"achSn" : "0"},
	 * 			"MainParam" : { "dwErrID" : 0, "dwHandle" : 1504629536, "dwReserved" : 0 }
	 * 		}
	 * }}
	 * @param jsonBodyObj
	 */
	public static void parseImQuerySubGroupInfoByGroupSnFinNtf(JSONObject jsonBodyObj) {
		if (null == jsonBodyObj) {
			return;
		}

		try {
			lockForQueryGroupOrMember.lock();
			IM.queryMemberInfoByGroupSn(groupSnList);
		} catch (Exception e) {
		} finally {
			lockForQueryGroupOrMember.unlock();
		}
	}

	/**
	 * 查询组联系人
	 * 
	 * @param jsonBodyObj
	 */
	public static void parseImQueryMemberInfoByGroupSnNtf(JSONObject jsonBodyObj) {
		if (null == jsonBodyObj) {
			return;
		}

		try {
			lockForQueryGroupOrMember.lock();

			TImMemberInfo[] memberlist = new TImMemberList().getTImMemberInfoArrfromJson(jsonBodyObj.getString(MyMtcCallback.KEY_AssParam));
			if (null == memberlist || memberlist.length == 0) {
				return;
			}

			for (TImMemberInfo timMemberInfo : memberlist) {
				if (null == timMemberInfo) continue;
				if (StringUtils.isNull(timMemberInfo.achGroupSn)) continue;
				if (StringUtils.isNull(timMemberInfo.achMemberSn)) continue;
				if (timMemberInfo.emMemberType == EmMtMemberType.EM_MEMBER_INVALID) continue;

				if (null == mContacts) {
					mContacts = new ArrayList<Contact>();
				}
				if (null == mJids) {
					mJids = new ArrayList<String>();
				}
				Contact contact = timMemberInfo.createContact(null);
				if (!mContacts.contains(contact)) {
					mContacts.add(contact);
					mJids.add(contact.getJid());
					//查询联系人详情 
					//					RmtContact.queryUserInfoReq(contact.getJid());
				}

				/*TMTWbParseKedaEntUser weiboInfo = timMemberInfo.tWeiboInfo;
				if (null != weiboInfo && !StringUtils.isNull(weiboInfo.achMoid)) {
					MemberInfo memberInfo = memberInfoDao.queryByMoId(weiboInfo.achMoid);
					if (memberInfo == null) {
						memberInfoDao.saveData(weiboInfo.createMemberInfo(memberInfo));
					} else {
						memberInfoDao.updateData(weiboInfo.createMemberInfo(memberInfo));
					}

					weiboInfo.updateContact(contact);

				} else {
					if (null != contact && !StringUtils.isNull(contact.getJid())) {
						MemberInfo memberInfo = memberInfoDao.queryByJid(contact.getJid());
						if (null != memberInfo) {
							contact = ContactManger.updateContactFromMemberInfo(contact, memberInfo);
						}
					}
				}*/
			}

		} catch (Exception e) {
		} finally {
			lockForQueryGroupOrMember.unlock();
		}
	}

	/**
	 * 查询组联系人Finish
	 * 
	 * {"mtapi":{"head":{"eventid":5044,"eventname":"ImQueryMemberInfoByGroupSnFinNtf","SessionID": "1"},
	 *		"body":{"AssParam" : {"achSn" : "22"},
	 *		"MainParam" : {
	 *			"dwErrID" : 0,
	 *			"dwHandle" : 1537458248,
	 *			"dwReserved" : 0
	 *		}
	 * }}
	 * @param jsonBodyObj
	 */
	public static boolean parseImQueryMemberInfoByGroupSnFinNtf(JSONObject jsonBodyObj) {

		if (null == jsonBodyObj) {
			return false;
		}

		try {
			lockForQueryGroupOrMember.lock();

			String gsn = jsonBodyObj.getJSONObject(MyMtcCallback.KEY_AssParam).getString("achSn");
			if (StringUtils.isNull(gsn)) return false;

		} catch (Exception e) {
		} finally {
			lockForQueryGroupOrMember.unlock();
		}

		return false;
	}



	/**
	 * 查询组成员列表在线状态
	 * 
	 *  @param jsonBodyObj
	 */
	public static void parseImQueryOnlineStateByGroupSnExNtf(JSONObject jsonBodyObj) {
		try {
			lockForQueryGroupOrMember.lock();

			TImUserStateListEx stateListEx = new TImUserStateListEx().fromJson(jsonBodyObj.getString(MyMtcCallback.KEY_AssParam));
			if (null == stateListEx || stateListEx.atArray == null || stateListEx.atArray.length == 0) {
				return;
			}

			for (TImUserStateEx stateEx : stateListEx.atArray) {
				if (null == stateEx) continue;

				ContactStatus cStatus = stateEx.createContactStatus(null);
				if (null == cStatus) continue;

				ContactStateManager.updateState(cStatus);
			}
		} catch (Exception e) {
		} finally {
			lockForQueryGroupOrMember.unlock();
		}
	}

	/**
	 * 查询组成员列表在线状态结束通知
	 * 
	 *  @param jsonBodyObj
	 */
	public synchronized static void parseImQueryOnlineStateByGroupSnExFinNtf(JSONObject jsonBodyObj) {
		try {
			lockForQueryGroupOrMember.lock();

			//			MainUIManager.refreshMainContactsView();
		} catch (Exception e) {
		} finally {
			lockForQueryGroupOrMember.unlock();
		}
	}

	/**
	 * 安全通知
	 * 
	 * <pre>
	 * {"mtapi":{"head":{"eventid":5112,"eventname":"ImNotifySecurityNtf","SessionID": "1"},"body":{
	 * 		"dwHandle" : 1612146776
	 * 	}
	 * }}
	 * @param jsonBodyObj
	 *//*
	public static void parseImNotifySecurityNtf(JSONObject jsonBodyObj) {
		if (null == jsonBodyObj) return;
		try {
			if (!jsonBodyObj.has(MyMtcCallback.KEY_dwHandle) && TruetouchGlobal.imHandle != jsonBodyObj.getInt(MyMtcCallback.KEY_dwHandle)) {
				return;
			}
		} catch (Exception e) {
			if (PcLog.isPrint) Log.e("Test", "parseImNotifySecurityNtf", e);
		}
	}

	*//**
		* 联系人状态改变通知
		* 
		* <pre>
		* {"mtapi":{"head":{"eventid":5051,"eventname":"ImMemberOnlineStateChangeNtf","SessionID": "1"},"body":{
		*   "AssParam" : {
		*      "achNO" : "005dd95e-f551-44fd-845c-5311058af5b1@kedacom.com",
		*      "bAudioCapability" : true,
		*      "bFileShare" : true,
		*      "bFirst" : true,
		*      "bGkConnected" : false,
		*      "bLoginBeforeMe" : true,
		*      "bMaxAudio" : false,
		*      "bMaxVideo" : false,
		*      "bSelf" : false,
		*      "bVideoCapability" : true,
		*      "emDeviceType" : 1,
		*      "emMaxState" : 2,
		*      "emState" : 2
		*   },
		*   "MainParam" : {
		*      "dwHandle" : 1612146776
		*   }
		* }
		* }}
		* @param jsonBodyObj
		*/
	//For SDM-00037552 modified by gaofan_kd7331 2015-10-15 21:26:18
	public static void parseImMemberOnlineStateChangeNtf(JSONObject jsonBodyObj) {
		if (null == jsonBodyObj) return;
		Gson gson = null;
		try {
//			TDeviceOnlineState state = new TDeviceOnlineState().fromJson(jsonBodyObj.getString(MyMtcCallback.KEY_AssParam));
			gson = new Gson();
			TDeviceOnlineState state = gson.fromJson(jsonBodyObj.getString(MyMtcCallback.KEY_AssParam), TDeviceOnlineState.class);
			if (null == state || StringUtils.isNull(state.achNO)) {
				return;
			}

			ContactStateManager.updateState(state);
			//			ContactManger.updateContactState(state.achNO);
			//viewchange
		} catch (Exception e) {
		}finally{
		}
	}

	/**
	 * 获取成员状态扩展信息响应
	 * 
	 * @param jsonBodyObj
	 *//*
	public static void parseImGetUsersStateExRsp(JSONObject jsonBodyObj) {

	}

	*//**
		* 获取成员状态扩展信息通知
		* 
		* @param GetUsersStateExCallback
		*/
	public static void parseImGetUsersStateExNtf(JSONObject jsonBodyObj) {
		if (null == jsonBodyObj) return;

		try {
			lockForQueryGroupOrMember.lock();

			TImUserStateListEx stateListEx = new TImUserStateListEx().fromJson(jsonBodyObj.getString(MyMtcCallback.KEY_AssParam));
			if (null == stateListEx || stateListEx.atArray == null || stateListEx.atArray.length == 0) {
				return;
			}

			List<String> jids = new ArrayList<String>();
			for (TImUserStateEx stateEx : stateListEx.atArray) {
				if (null == stateEx) continue;

				ContactStatus cStatus = stateEx.createContactStatus(null);
				if (null == cStatus) continue;

				jids.add(cStatus.getJid());
				ContactStateManager.updateState(cStatus);
			}

			/*	Activity currActivity = AppGlobal.getCurrActivity();
				if (currActivity instanceof ChatWindowActivity) {
					((ChatWindowActivity) currActivity).updateOnlineStatus(jids);
				}*/

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lockForQueryGroupOrMember.unlock();
		}
	}


	/**
	 * 添加Member
	 * 
	 * <pre>
	 * {"mtapi":{"head":{"eventid":5045,"eventname":"ImAddMemberInfoRsp","SessionID": "1"},"body":{
	 * 		"AssParam" : {
	 * 			"bMaxAudio" : false,
	 * 			"bMaxVideo" : false,
	 * 			"dwStateCount" : 0,
	 * 			"emMemberState" : 1,
	 * 			"tMemberinfo" : {
	 * 				"achGroupSn" : "1",
	 * 				"achMarkName" : "",
	 * 				"achMemberNO" : "8d78d5e5-ef8d-4e8e-b46c-efd27da35ba0@hello_146",
	 * 				"achMemberSn" : "0",
	 * 				"bNoQuery" : false,
	 * 				"emMemberType" : 1,
	 * 				"tWeiboInfo" : {
	 * 					"achAccount" : "0812110000003",
	 * 					"achBrief" : "",
	 * 					"achDateOfBirth" : "",
	 * 					"achDeviceGuid" : "10252200000000000000812110000003",
	 * 					"achDeviceType" : "522",
	 * 					"achE164" : "0812110000003",
	 * 					"achFax" : "",
	 * 					"achJid" : "8d78d5e5-ef8d-4e8e-b46c-efd27da35ba0@hello_146",
	 * 					"achJobNum" : "",
	 * 					"achMobileNum" : "",
	 * 					"achMoid" : "8d78d5e5-ef8d-4e8e-b46c-efd27da35ba0",
	 * 					"achNuServerID" : "3af99e8d-787e-48f3-992e-b8bffd80d9a9",
	 * 					"achOfficeLocation" : "",
	 * 					"achPortrait128" : "",
	 * 					"achPortrait256" : "",
	 * 						...
	 * 					"bWeiboAdmin" : false,
	 * 					"tMtWbParseKedaDepts" : {
	 * 						"atMtWbParseKedaDept" : [
	 * 							{
	 * 								"achDepartmentName" : "号码组",
	 * 								"achDeptPosition" : "",
	 * 								"achFullPath" : "科达用户域2/号码组",
	 * 								"dwDepartmentId" : 0
	 * 							}
	 * 						],
	 * 					"dwDeptNum" : 1
	 * 					}
	 * 				},
	 * 			"wBitRate" : 0
	 * 			}
	 * 		},
	 * 		"MainParam" : {
	 * 			"achReserved" : "",
	 * 			"dwErrID" : 0,
	 * 			"dwHandle" : 1596042128,
	 * 			"dwReserved" : 0
	 * 		}
	 * 	}
	 * }}
	 * 
	 * @param jsonBodyObj
	 */
	public static void parseImAddMemberInfoRsp(JSONObject jsonBodyObj) {
		int drrId = 0;
		try {
			drrId = jsonBodyObj.getJSONObject(MyMtcCallback.KEY_MainParam).getInt(MyMtcCallback.KEY_dwErrID);
		} catch (Exception e) {
		}

		try {
			String memberNO = "";
			String markName = "";
			boolean success = false;
			boolean isPhoneContact = false;

			if (drrId != 0) {
				success = false;

				TImMemberInfo timMemberInfo = new TImMemberInfo().fromJson(jsonBodyObj.getString(MyMtcCallback.KEY_AssParam));
				if (null != timMemberInfo) {
					memberNO = timMemberInfo.achMemberNO;
					markName = timMemberInfo.achMarkName;
				}
			} else {
				success = true;

				String memberinfoJsonStr = jsonBodyObj.getJSONObject(MyMtcCallback.KEY_AssParam).getString("tMemberinfo");
				TImMemberInfo timMemberInfo = new TImMemberInfo().fromJson(memberinfoJsonStr);

				if (null == timMemberInfo || StringUtils.isNull(timMemberInfo.achGroupSn) || StringUtils.isNull(timMemberInfo.achMemberSn)
						|| StringUtils.isNull(timMemberInfo.achMemberNO)) {
					success = false;
				} else {
					success = true;
					memberNO = timMemberInfo.achMemberNO;
					markName = timMemberInfo.achMarkName;

					if (timMemberInfo.emMemberType == EmMtMemberType.EM_MEMBER_TELEPHONE || timMemberInfo.emMemberType == EmMtMemberType.EM_MEMBER_FXO) {
						isPhoneContact = true;
					}
				}

				Contact contact = null;
				if (null != timMemberInfo && success) {
					contact = timMemberInfo.createContact(null);
				}

				if (!mContacts.contains(contact)) {
					mContacts.add(contact);
					mJids.add(contact.getJid());
				}
			}
		} catch (Exception e) {
		}
	}



	/**
	 * 通知添加Member
	 * 
	 * <pre>
	 * {"mtapi":{"head":{"eventid":5090,"eventname":"ImMemberAddNtf","SessionID": "1"},"body":{
	 * 		"AssParam" : {
	 * 			"achGroupSn" : "1",
	 * 			"achMarkName" : "0812110000164",
	 * 			"achMemberNO" : "1addbc31-3b50-4c54-a740-7dea8e6ad68b@hello_161",
	 * 			"achMemberSn" : "5",
	 * 			"bNoQuery" : false,
	 * 			"emMemberType" : 1,
	 * 			"tWeiboInfo" : {
	 * 				……
	 * 			},
	 * 			"wBitRate" : 0
	 * 		},
	 * 		"MainParam" : {
	 * 			"dwHandle" : 1731002392
	 * 		}
	 * 	}
	 * }}
	 * 
	 * @param jsonBodyObj
	 */
	public static void parseImMemberAddNtf(JSONObject jsonBodyObj) {
		try {
			String jid = "";
			boolean success = false;
			boolean isPhoneContact = false;
			TImMemberInfo timMemberInfo = new TImMemberInfo().fromJson(jsonBodyObj.getString(MyMtcCallback.KEY_AssParam));
			if (null == timMemberInfo || StringUtils.isNull(timMemberInfo.achGroupSn) || StringUtils.isNull(timMemberInfo.achMemberSn)
					|| StringUtils.isNull(timMemberInfo.achMemberNO)) {
				success = false;
			} else {
				success = true;
				jid = timMemberInfo.achMemberNO;

				if (timMemberInfo.emMemberType == EmMtMemberType.EM_MEMBER_TELEPHONE || timMemberInfo.emMemberType == EmMtMemberType.EM_MEMBER_FXO) {
					isPhoneContact = true;
				}
			}
			Contact contact = null;
			if (null != timMemberInfo && success) {
				contact = timMemberInfo.createContact(null);
			}

			if (!mContacts.contains(contact)) {
				mContacts.add(contact);
				mJids.add(contact.getJid());
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 通知删除Member
	 * 
	 * <pre>
	 *  {"mtapi":{"head":{"eventid":5091,"eventname":"ImMemberDelNtf","SessionID": "1"},"body":{
	 *		"AssParam" : {
	 *			"achSn" : "11"
	 *		},
	 *		"MainParam" : {
	 *			"dwHandle" : 1722142448
	 *		}
	 *	}
	 *	}}
	 * @param jsonBodyObj
	 */
	public static void parseImMemberDelNtf(JSONObject jsonBodyObj) {
		try {
			lockForContactAndGroup.lock();

			boolean delSuccess = false;
			String sn = jsonBodyObj.getJSONObject(MyMtcCallback.KEY_AssParam).getString("achSn");
			if (StringUtils.isNull(sn)) {
				return;
			}

			for (Contact c : mContacts) {
				if (c.getJid().equals(sn)) {
					mContacts.remove(c);
					mJids.remove(c.getJid());
					//view
					return;
				}
			}

			// 是否正在移动联系人界面
			/*TODO ContactMoveUI & MainContacts if (currActivity instanceof ContactMoveUI) {
			}

			if (delSuccess) {
				SlidingMenuManager.refreshMainContactsView();
			}*/
		} catch (Exception e) {
		} finally {
			lockForContactAndGroup.unlock();
		}
	}

	/**
	 * 按姓名模糊查找本企业联系人请求
	 * @param jsonBodyObj 
	 */

	public static void parseRestUserListByStrRsp(JSONObject jsonBodyObj) {
		try {
			if (null == jsonBodyObj) return;

			TMTWbParseKedaSearchUsers kedaSearchUsers = null;
			if (jsonBodyObj.has(MyMtcCallback.KEY_AssParam)) {
				String assParam = jsonBodyObj.getString(MyMtcCallback.KEY_AssParam);
				if (!TextUtils.isEmpty(assParam)) {
					kedaSearchUsers = new TMTWbParseKedaSearchUsers().fromJson(assParam);
				}
			}
			List<Contact> contacts = new ArrayList<Contact>();
			if (null != kedaSearchUsers && null != kedaSearchUsers.tEntUsers) {
				TMTWbParseKedaEntUser[] atEntUser = kedaSearchUsers.tEntUsers.atEntUser;
				if (null != atEntUser && atEntUser.length > 0) {
					for (TMTWbParseKedaEntUser wbParseKedaEntUser : atEntUser) {
						if (null == wbParseKedaEntUser) continue;
						contacts.add(wbParseKedaEntUser.createContact(null));
					}
				}
			}
			Activity currActivity = PcAppStackManager.Instance().currentActivity();
			// 搜索联系人界面
			if (currActivity instanceof ContactSearchListUI) {
				((ContactSearchListUI) currActivity).updateListView(kedaSearchUsers.achStr, kedaSearchUsers.dwTotalCount, contacts);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 查询账号详细信息应答
	 * 
	 * <pre>
	 * 		查询联系人详细会返回RestGetAccountInfo_Rsp 和 ImQueryAccountInfoRsp 2条消息，
	 * 		经确认ImQueryAccountInfoRsp消息不用处理
	 * 
	 * 失败TMTAccountManagerSystem
	 * {"mtapi":{"head":{"eventid":5153,"eventname":"RestGetAccountInfo_Rsp","SessionID": "1"},
	 * 		"body":{
	 * 			"AssParam" : {
	 * 				"achAPIAddr" : "522",
	 * 				"achName" : "",
	 * 				"achbrief" : "",
	 * 				"achextNum" : "",
	 * 				"achmobileNum" : "0712110000001",
	 * 				"achpassword" : "0eebb4c6-ea77-429f-8692-bf1c4a0193ad@hello_149",
	 * 				"achposition" : "10252200000000000000712110000001",
	 * 				"achseat" : "0712110000001",
	 * 				"achusername" : "0eebb4c6-ea77-429f-8692-bf1c4a0193ad",
	 * 				"bIsAdding" : true,
	 * 				"bMale" : true,
	 * 				"dwBitMask" : 1634618608,
	 * 				"dwContextId" : 1633010200
	 * 			},
	 * 			"MainParam" : {"achErrorInfo" : "","adwParams" : [ 0, 0, 0, 0 ],"dwErrorID" : 1000,"dwNackEventId" : 0,"emApiType" : 0}
	 * 		}
	 * 	}}
	 * @param jsonBodyObj
	 */
	public static void parseRestGetAccountInfoRsp(JSONObject jsonBodyObj) {
		try {
			if (null == jsonBodyObj) return;

			String assParam = "";

			if (jsonBodyObj.has(MyMtcCallback.KEY_AssParam)) {
				assParam = jsonBodyObj.getString(MyMtcCallback.KEY_AssParam);
			}

			MemberInfo memberInfo = null;
			TMTWbParseKedaEntUser wbParseKedaEntUser = null;

			if (!TextUtils.isEmpty(assParam)) {
				wbParseKedaEntUser = new TMTWbParseKedaEntUser().fromJson(assParam);
			}


			if (null != wbParseKedaEntUser) {
				memberInfo = wbParseKedaEntUser.createMemberInfo(null);
			}
			// 查询失败
			if (null == memberInfo) {
				return;
			}

			if (null != wbParseKedaEntUser) {
				// Contact contact = contactDao.queryByJid(memberInfo.getJid());

				for (Contact c : mContacts) {
					if (c.getJid().equals(memberInfo.getJid())) {
						wbParseKedaEntUser.updateContact(c);
						return;
					}
				}

			}
			// if (null != memberInfo) {
			// MainMessage mainMessage = SlidingMenuManager.getMainMessage();
			// if (null != mainMessage) {
			// mainMessage.updateContactFromCahce(memberInfo.getMoId(), memberInfo.getPortrait64());
			// }
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 批量查询账号详细信息
	 * @param jsonBodyObj
	 */
	public static void parseRestQueryUserInfoRsp(JSONObject jsonBodyObj) {
		try {
			String assParam = "";
			if (jsonBodyObj.has(MyMtcCallback.KEY_AssParam)) {
				assParam = jsonBodyObj.getString(MyMtcCallback.KEY_AssParam);
			}

			final TMTWbParseKedaEntUsers wbParseKedaEntUsers = !TextUtils.isEmpty(assParam) ? new TMTWbParseKedaEntUsers().fromJson(assParam) : null;
			if (null != wbParseKedaEntUsers && wbParseKedaEntUsers.atEntUser != null) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						for (TMTWbParseKedaEntUser wbParseKedaEntUser : wbParseKedaEntUsers.atEntUser) {
							if (null == wbParseKedaEntUser) continue;
							MemberInfo memberInfo = wbParseKedaEntUser.createMemberInfo(null);
							if (null == memberInfo || StringUtils.isNull(memberInfo.getJid())) continue;
							for (Contact c : mContacts) {
								if (c.getJid().equals(memberInfo.getJid())) {
									c = wbParseKedaEntUser.updateContact(c);
								}
							}
						} // end for
					}
				}).start();
			}
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}

	/**
	 * 查询账号详细信息应答
	 * 
	 * @param jsonBodyObj 
	 */

	public static void parseRestQueryUserInfoFinishRsp(JSONObject jsonBodyObj) {


	}
}
