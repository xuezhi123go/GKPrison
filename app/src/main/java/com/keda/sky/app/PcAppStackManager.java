package com.keda.sky.app;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Activity 管理栈
 * 
 * @author chenjian
 * @date 2014-1-16
 */
public class PcAppStackManager {

	private static PcAppStackManager mAppStackManager;
	private static Stack<Activity> mActivityStack;

	static {
		mActivityStack = new Stack<Activity>();
	}

	private PcAppStackManager() {
		mActivityStack = new Stack<Activity>();
	}

	public static PcAppStackManager Instance() {
		synchronized (PcAppStackManager.class) {
			if (mAppStackManager == null) {
				mAppStackManager = new PcAppStackManager();
			}

			if (null == mActivityStack) {
				mActivityStack = new Stack<Activity>();
			}
		}

		return mAppStackManager;
	}

	public boolean isEmpty() {
		if (mActivityStack != null) {
			return mActivityStack.isEmpty();
		}

		return true;
	}

	public synchronized static boolean isRelaseStatck() {
		return mAppStackManager == null;
	}

	/**
	 * relase Statck
	 */
	public synchronized static void relaseActivityStack() {
		if (mActivityStack != null) {
			mActivityStack.clear();
		}

		if (mAppStackManager != null) {
			mAppStackManager = null;
		}
	}

	/**
	 * 压入堆栈顶部
	 * 
	 * @param activity
	 */
	public synchronized void pushActivity(Activity activity) {
		if (mActivityStack == null) mActivityStack = new Stack<Activity>();

		if (activity == null) return;

		mActivityStack.push(activity);
	}

	/**
	 * 移除堆栈顶部的Activity
	 */
	public synchronized void popActivity() {
		Log.i(PcAppStackManager.class.getSimpleName(), "popActivity 1");

		if (mActivityStack == null) {
			Log.i(PcAppStackManager.class.getSimpleName(), "popActivity null");
		} else {
			Log.i(PcAppStackManager.class.getSimpleName(), "popActivity " + mActivityStack.size() + "  isEmpty " + mActivityStack.isEmpty());
		}

		if (mActivityStack == null || mActivityStack.empty()) return;

		Log.i(PcAppStackManager.class.getSimpleName(), "popActivity 2");

		Activity activity = mActivityStack.peek();
		if (activity != null) {
			activity = mActivityStack.pop();
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 移除堆栈中的Activity
	 * 
	 * @param activity
	 * @param finish  Activity finish
	 */
	public synchronized void popActivity(Activity activity, boolean finish) {
		if (activity == null) {
			return;
		}

		if (finish) {
			activity.finish();
		}

		if (mActivityStack != null && !mActivityStack.empty() && mActivityStack.contains(activity)) {
			mActivityStack.remove(activity);
		}

		activity = null;
	}

	/**
	 * 移除堆栈中的Activity，并将Activity Finish
	 * 
	 * @param activity
	 */
	public synchronized void popActivity(Activity activity) {
		popActivity(activity, true);
	}

	/**
	 * 移除堆栈中的Activit
	 *
	 * @param activity
	 */
	public synchronized void popActivity(Object activity) {
		if (null == activity || !(activity instanceof Activity)) {
			return;
		}

		popActivity((Activity) activity, true);
	}

	/**
	 * 移除堆栈中的Activit
	 *
	 * @param activitys
	 */
	public synchronized void popActivitys(List<Activity> activitys) {
		if (null == activitys || activitys.isEmpty()) return;

		for (Activity PIBaseActivity : activitys) {
			if (null == PIBaseActivity) continue;

			popActivity(PIBaseActivity, true);
		}
	}

	/**
	 * 移除堆栈中指定位置的Activit
	 *
	 * @param index
	 */
	public synchronized void popActivity(int index) {
		if (index < 0) return;

		if (mActivityStack != null && !mActivityStack.empty() && index < mActivityStack.size()) {
			Activity activity = mActivityStack.get(index);
			if (mActivityStack.contains(activity)) popActivity(activity);
		}
	}

	/**
	 * 返回指定位置的Activity
	 * 
	 * @param index
	 * @return
	 */
	public synchronized Activity getActivity(int index) {
		Activity activity = null;
		if (index < 0) return activity;

		if (mActivityStack != null && !mActivityStack.empty() && index < mActivityStack.size()) {
			activity = mActivityStack.get(index);
		}

		return activity;
	}

	/**
	 * 返回Calss对应的Activit对象 
	 *
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public synchronized Activity getActivity(Class cls) {
		if (cls == null) return null;

		Activity result = null;
		try {
			if (mActivityStack != null && !mActivityStack.empty()) {
				for (Activity a : mActivityStack) {
					if (a == null) continue;
					if (a.getClass().equals(cls)) {
						result = a;
						break;
					}
				}
			}
		} catch (Exception e) {
		}

		return result;
	}

	/**
	 * 返回Calss对应的Activity的所有对象
	 *
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public synchronized List<Activity> getActivityList(Class cls) {
		if (cls == null) return null;

		List<Activity> list = new ArrayList<Activity>();
		if (mActivityStack != null && !mActivityStack.empty()) {
			for (Activity a : mActivityStack) {
				if (a == null) continue;
				if (a.getClass().equals(cls)) {
					list.add(a);
				}
			}
		}

		return list;
	}

	/**
	 * 返回当前 Activity
	 * 
	 * @return
	 */
	public synchronized Activity currentActivity() {
		if (mActivityStack == null || mActivityStack.empty()) return null;

		Activity activity = mActivityStack.peek();

		return activity != null ? activity : null;
	}

	/**
	 * 返回前一个 Activity
	 * 
	 * @return
	 */
	public synchronized Activity preActivity() {
		int index = currentActivityIndex();
		return getActivity(index - 1);
	}

	/**
	 * 当前Activit索引位置 
	 *
	 * @return
	 */
	public synchronized int currentActivityIndex() {
		Activity activity = currentActivity();
		if (activity == null) return 0;

		int index = 0;
		if (mActivityStack != null && !mActivityStack.empty() && mActivityStack.contains(activity)) {
			index = mActivityStack.indexOf(activity);
		}

		return index;
	}

	/**
	 * Calss对应第一个Activity对象的位置索引
	 *
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public synchronized int activityIndex(Class cls) {
		if (cls == null) return -1;

		Activity activity = getActivity(cls);
		if (activity == null) return -1;

		int index = -1;
		if (mActivityStack != null && !mActivityStack.empty() && mActivityStack.contains(activity)) {
			index = mActivityStack.indexOf(activity);
		}

		return index;
	}

	/**
	 * 移除Activity至某一个Activity为止
	 * 
	 * @param cls first-activity
	 */
	@SuppressWarnings("rawtypes")
	public synchronized void popAllActivityExceptOne(Class cls) {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) break;

			if (activity.getClass().equals(cls)) break;

			popActivity(activity);
		}
	}

	/**
	 * finish除了cls之外的所有Activity
	 * 
	 * @param cls
	 */
	@SuppressWarnings("rawtypes")
	public synchronized void finishAllActivityExceptOne(Class cls) {
		ArrayList<Activity> activityList = getAllActivity();
		if (null == activityList || activityList.isEmpty()) {
			return;
		}

		for (android.app.Activity Activity : activityList) {
			if (Activity.getClass().equals(cls)) continue;
			popActivity(Activity);
		}
	}

	/**
	 * 移除栈中的所有Activity
	 */
	public synchronized void popAllActivity() {
		Log.i(PcAppStackManager.class.getSimpleName(), "popAllActivity 1");

		if (mActivityStack == null) {
			Log.i(PcAppStackManager.class.getSimpleName(), "popAllActivity null");
		} else {
			Log.i(PcAppStackManager.class.getSimpleName(), "popAllActivity " + mActivityStack.size() + "  isEmpty " + mActivityStack.isEmpty());
		}

		if (mActivityStack == null || mActivityStack.isEmpty()) {
			return;
		}

		Log.i(PcAppStackManager.class.getSimpleName(), "popAllActivity 2");

		// for (PIBaseActivity a : mActivityStack) {
		// if (a != null) {
		// a.onFinish();
		// }
		// }

		for (int i = mActivityStack.size() - 1; i >= 0; i--) {
			Activity a = mActivityStack.get(i);
			if (a != null) {
				a.finish();
			}
		}

		mActivityStack.clear();
	}

	/**
	 * 获取所有的acticvity
	 * 
	 * @return
	 */
	public synchronized ArrayList<Activity> getAllActivity() {
		ArrayList<Activity> activityList = new ArrayList<Activity>();
		for (Activity activity : mActivityStack) {
			if (activity != null && activityList != null) {
				activityList.add(activity);
			}
		}

		return activityList;
	}

	/**
	 * 栈中是否存在Calss对应的对象
	 * 
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public synchronized boolean existActivity(Class cls) {
		boolean b = false;

		for (int i = 0; i < mActivityStack.size(); i++) {
			Activity currentActivity = mActivityStack.get(i);
			if (currentActivity != null) {
				if (currentActivity.getClass().equals(cls)) {
					b = true;
					break;
				}
			}
		}

		return b;
	}

	/**
	 * 栈中是存在Calss对应的对象数
	 * 
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public synchronized int existActivityCount(Class cls) {
		int count = 0;
		for (int i = 0; i < mActivityStack.size(); i++) {
			Activity currentActivity = mActivityStack.get(i);
			if (currentActivity != null) {
				if (currentActivity.getClass().equals(cls)) {
					count++;
				}
			}
		}

		return count;
	}

}
