/**
 * @(#)PBaseActivity.java 2014-1-16 Copyright 2014 it.kedacom.com, Inc. All
 *                        rights reserved.
 */

package com.keda.sky.app.base;


/**
 * pc BaseActivity interface
 * 
 * @author chenjian
 * @date 2014-1-16
 */

public interface PcBaseActivity extends PcIBaseActivity {

	/**
	 * init Extras
	 */
	void initExtras();

	/**
	 * find views (eg:for id)
	 */
	void findViews();

	/**
	 * 初始化控件的值
	 */
	void initComponentValue();

	/**
	 * 注册监听事件
	 */
	void registerListeners();
}
