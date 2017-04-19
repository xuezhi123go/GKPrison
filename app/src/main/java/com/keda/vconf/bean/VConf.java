/**
 * @(#)VConf.java   2014-8-5
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

package com.keda.vconf.bean;

import com.gkzxhn.gkprison.R;
import com.google.gson.GsonBuilder;
import com.kedacom.kdv.mt.bean.TMTTime;
import com.kedacom.kdv.mt.bean.TMtApi;
import com.kedacom.kdv.mt.constant.EmConfListType;
import com.pc.utils.StringUtils;

/**
  * 视频会议(Video Conference)
  * 
  * @author chenj
  * @date 2014-8-5
  */
public class VConf extends TMtApi implements Comparable<VConf> {

	/**
	 * 会议ID
	 */
	private String confId;

	/**
	 * 会议名
	 */
	public String achConfName; // 会议名

	/**
	 * E164号码
	 */
	public String achConfE164; // 会议的E164号码

	/**
	 * 入会终端数
	 */
	public int dwMtNum; // 入会终端数

	/**
	 * 会议剩余时间
	 */
	public int dwRemainTime; // 会议剩余时间

	/**
	 * 是否需要密码：0 - 不需要，1 - 需要
	 */
	private boolean isNeedPsw;

	/**
	 * 会议模式：0-高清、1-标清、2-流畅、3-自定义
	 * EmMtClarity
	 */
	private int emConfClarity;

	/**
	 * 会议列表类型 EmConfListType
	 * <pre>
	 * 	 enum EmConfListType
	 * 	 {
	 * 	 emConfListType_Hold = 0, //正在召开
	 * 	 emConfListType_Subscribe, //预约
	 * 	 emConfListType_Idle, //空闲
	 * 	 emConfListType_All, //全部
	 * 	 emConfListTypeEnd
	 * 	 };
	 * </pre>
	 * @see com.kedacom.truetouch.vconf.constant.EmConfListType
	 */
	private int confListType;

	/**
	 * 会议开始时间对象
	 */
	private TMTTime tConfStartTime; // 会议开始时间

	/**
	 * 会议开始时间
	 */
	private String startTime;

	/**
	 * 会议码率
	 */
	private int callRate;

	private int confSpcl;

	private boolean bIsSatdcaseMode; // 是否支持卫星分散会议：0-不支持，1-支持

	private boolean bIsPublicConf; // 是否为开放会议：0-不是，1-是

	private int emOpenMode; // 开放方式EmMtOpenMode

	/** 
	 * @return the confId
	 */
	public String getConfId() {
		return confId;
	}

	/**
	 * @param confId the confId to set
	 */
	public void setConfId(String confId) {
		this.confId = confId;
	}

	/** 
	 * @return the isNeedPsw
	 */
	public boolean isNeedPsw() {
		return isNeedPsw;
	}

	/**
	 * @param isNeedPsw the isNeedPsw to set
	 */
	public void setNeedPsw(boolean isNeedPsw) {
		this.isNeedPsw = isNeedPsw;
	}

	/** 
	 * @return the confModeEx
	 */
	public int getConfModeEx() {
		return emConfClarity;
	}

	/**
	 * @param confModeEx the confModeEx to set
	 */
	public void setConfModeEx(int confModeEx) {
		this.emConfClarity = confModeEx;
	}

	/** 
	 * @return the confListType
	 */
	public int getConfListType() {
		return confListType;
	}

	/**
	 * @param confListType the confListType to set
	 */
	public void setConfListType(int confListType) {
		this.confListType = confListType;
	}

	/** 
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/** @return the tConfStartTime */
	public TMTTime gettConfStartTime() {
		return tConfStartTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/** 
	 * @return the callRate
	 */
	public int getCallRate() {
		return callRate;
	}

	/**
	 * @param callRate the callRate to set
	 */
	public void setCallRate(int callRate) {
		this.callRate = callRate;
	}

	/** 
	 * @return the confSpcl
	 */
	public int getConfSpcl() {
		return confSpcl;
	}

	/**
	 * @param confSpcl the confSpcl to set
	 */
	public void setConfSpcl(int confSpcl) {
		this.confSpcl = confSpcl;
	}

	/**
	 * 空闲会议
	 * 
	 * @return
	 */
	public boolean isTemplate() {
		return confListType == EmConfListType.Tmplt.ordinal();
	}

	/**
	 * 预定会议
	 * 
	 * @return
	 */
	public boolean isBook() {
		return confListType == EmConfListType.Book.ordinal();
	}

	/**
	 * 会议中
	 * 
	 * @return
	 */
	public boolean isHold() {
		return confListType == EmConfListType.Hold.ordinal();
	}

	/**
	 * 截取E164号后六位
	 * @return
	 */
	public String getSingleConfE164() {
		String split = "#";

		if (StringUtils.isNull(achConfE164) || !achConfE164.contains(split)) {
			return achConfE164;
		}

		int index = achConfE164.indexOf(split);
		String sigleConfE164 = achConfE164;
		try {
			sigleConfE164 = achConfE164.substring(index + 1);
		} catch (Exception e) {
		}

		return sigleConfE164;
	}

	public int getTimeLen() {
		if (dwRemainTime == 0) {
			return 0;
		}

		return dwRemainTime / 60;
	}

	/*	*//**
			* 会议类型对应的资源
			* 
			* @return
			*/
	/*
	public int getTypeResouceId() {
	if (isHold()) {
		return R.drawable.vconf_video_ing;
	}

	if (isBook()) {
		return R.drawable.video_book;
	}

	if (isTemplate()) {
		return R.drawable.video_free;
	}

	return R.drawable.video_free;
	}*/

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		try {
			return StringUtils.equals(achConfE164, ((VConf) o).achConfE164);
		} catch (Exception e) {
		}
		return super.equals(o);
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(VConf another) {
		if (another == null) {
			return -1;
		}

		if (StringUtils.isNull(this.getAchConfName()) || StringUtils.isNull(another.getAchConfName())) {
			return -1;
		}

		try {
			if (another.getConfListType() == getConfListType()) {
				return this.getAchConfName().compareTo(another.getAchConfName());
			} else {
				if (another.getConfListType() < getConfListType()) {
					return 1;
				} else {
					return -1;
				}
			}
		} catch (Exception e) {
		}

		return 0;
	}

	/** @return the achConfName */
	public String getAchConfName() {
		return achConfName;
	}

	/** @param achConfName the achConfName to set */
	public void setAchConfName(String achConfName) {
		this.achConfName = achConfName;
	}

	/** @return the achConfE164 */
	public String getAchConfE164() {
		return achConfE164;
	}

	/** @param achConfE164 the achConfE164 to set */
	public void setAchConfE164(String achConfE164) {
		this.achConfE164 = achConfE164;
	}

	/** @return the dwMtNum */
	public int getDwMtNum() {
		return dwMtNum;
	}

	/** @param dwMtNum the dwMtNum to set */
	public void setDwMtNum(int dwMtNum) {
		this.dwMtNum = dwMtNum;
	}

	/** @return the dwRemainTime */
	public int getDwRemainTime() {
		return dwRemainTime;
	}

	/** @param dwRemainTime the dwRemainTime to set */
	public void setDwRemainTime(int dwRemainTime) {
		this.dwRemainTime = dwRemainTime;
	}

	/** @return the emConfClarity */
	public int getEmConfClarity() {
		return emConfClarity;
	}

	/** @param emConfClarity the emConfClarity to set */
	public void setEmConfClarity(int emConfClarity) {
		this.emConfClarity = emConfClarity;
	}

	/** @return the bIsSatdcaseMode */
	public boolean isbIsSatdcaseMode() {
		return bIsSatdcaseMode;
	}

	/** @param bIsSatdcaseMode the bIsSatdcaseMode to set */
	public void setbIsSatdcaseMode(boolean bIsSatdcaseMode) {
		this.bIsSatdcaseMode = bIsSatdcaseMode;
	}

	/** @return the bIsPublicConf */
	public boolean isbIsPublicConf() {
		return bIsPublicConf;
	}

	/** @param bIsPublicConf the bIsPublicConf to set */
	public void setbIsPublicConf(boolean bIsPublicConf) {
		this.bIsPublicConf = bIsPublicConf;
	}

	/** @return the emOpenMode */
	public int getEmOpenMode() {
		return emOpenMode;
	}

	/** @param emOpenMode the emOpenMode to set */
	public void setEmOpenMode(int emOpenMode) {
		this.emOpenMode = emOpenMode;
	}

	/**
	 * @see com.kedacom.kdv.mt.mtapi.bean.TMtApi#toJson()
	 */
	@Override
	public String toJson() {
		return new GsonBuilder().create().toJson(this);
	}

	/**
	 * @see com.kedacom.kdv.mt.mtapi.bean.TMtApi#fromJson(java.lang.String)
	 */
	@Override
	public VConf fromJson(String gson) {
		return new GsonBuilder().create().fromJson(gson, VConf.class);
	}

	/**
	 * 会议类型对应的资源
	 * 
	 * @return
	 */
	public int getTypeResouceId() {
		if (isHold()) {
			return R.drawable.vconf_video_ing;
		}

		if (isBook()) {
			return R.drawable.video_book;
		}

		if (isTemplate()) {
			return R.drawable.video_free;
		}

		return R.drawable.video_free;
	}

}
