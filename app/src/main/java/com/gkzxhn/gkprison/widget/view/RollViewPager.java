package com.gkzxhn.gkprison.widget.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

public class RollViewPager extends ViewPager {

	private List<View> dot_list;
	private List<String> titlelist;
	private TextView top_news_title;
	private List<String> imgUrlList;
//	private int imgUrlList;
	private BitmapUtils bitmapUtils;
	private MyPagerAdapter myPagerAdapter;
//	private final int[] CAROUSEL_IVS;

	/**
	 * 当前viewpager指向的索引
	 */
	private int currentPosition = 0;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 让viewpager指向currentPosition索引指向界面
			RollViewPager.this.setCurrentItem(currentPosition);
			// 循环滚动，继续发消息
			startRoll();
		}
	};
	private RunnableTask runnableTask;
	private int downX;
	private int downY;
	private OnViewClickListener onViewClickListener;

	class RunnableTask implements Runnable {

		@Override
		public void run() {
			if(imgUrlList != null && imgUrlList.size() > 0) {
				// 切换指向图片
				currentPosition = (currentPosition + 1) % imgUrlList.size();
				// 发送消息
				handler.obtainMessage().sendToTarget();
			}
		}
	}

	/**
	 * viewpager内容view的点击监听
	 * 
	 * @author Administrator
	 * 
	 */
	public interface OnViewClickListener {
		/**
		 * viewpager内容view的点击回调方法
		 * 
		 * @param position
		 */
		void viewClick(int position);
	}

	// 当view移出界面的时候调用
	@Override
	protected void onDetachedFromWindow() {
		// 不再发送消息，将维护的任务也移除
		handler.removeCallbacksAndMessages(null);
		super.onDetachedFromWindow();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = (int) ev.getX();
			downY = (int) ev.getY();
			// 当前viewpager对应的父控件不能去拦截事件
			getParent().requestDisallowInterceptTouchEvent(true);

			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) ev.getX();
			int moveY = (int) ev.getY();
			if (Math.abs(moveX - downX) > Math.abs(moveY - downY)) {
				// 左右拉动
				// 1.滑动内部viewpager指向的item，父控件不拦截事件
				// 2.滑动整个模块，指向下一个模块，父控件拦截事件
				int dX = moveX - downX;
				// 由右向左滑，并且viewpager在最后一个页面的时候
				if (dX < 0 && getCurrentItem() == getAdapter().getCount() - 1) {
					// 父控件拦截事件
					getParent().requestDisallowInterceptTouchEvent(false);
				} else if (dX < 0 && getCurrentItem() > 0) {
					getParent().requestDisallowInterceptTouchEvent(true);
				} else if (dX > 0 && getCurrentItem() == 0) {
					getParent().requestDisallowInterceptTouchEvent(false);
				} else if (dX > 0 && getCurrentItem() < getAdapter().getCount()) {
					getParent().requestDisallowInterceptTouchEvent(true);
				}

			} else if (Math.abs(moveX - downX) < Math.abs(moveY - downY)) {
				// 上下拉动
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			break;
		case MotionEvent.ACTION_UP:

			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	public RollViewPager(Context context, final List<View> dot_list,
//						 int[] img,
						 OnViewClickListener onViewClickListener) {
		super(context);
		this.dot_list = dot_list;
		this.onViewClickListener = onViewClickListener;
//		this.CAROUSEL_IVS = img;
		//
		bitmapUtils = new BitmapUtils(context);
		runnableTask = new RunnableTask();

		this.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// 切换图片标题
				top_news_title.setText(titlelist.get(arg0));
				// 点切换
				for (int i = 0; i < dot_list.size(); i++) {
					if (i == arg0) {
						dot_list.get(arg0).setBackgroundResource(
								R.drawable.rb_shape_blue);
					} else {
						dot_list.get(i).setBackgroundResource(
								R.drawable.rb_shape_gray);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	/**
	 * 传递显示图片标题文字的方法
	 * 
	 * @param titlelist
	 * @param top_news_title
	 */
	public void initTitle(List<String> titlelist, TextView top_news_title) {
		if (titlelist != null && top_news_title != null && titlelist.size() > 0) {
			top_news_title.setText(titlelist.get(0));
		}
		this.titlelist = titlelist;
		this.top_news_title = top_news_title;
	}

	public void initImgUrl(List<String> imgUrlList) {
		this.imgUrlList = imgUrlList;
	}

	public void startRoll() {
		// 1.给viewpager设置数据适配器
		if (myPagerAdapter == null) {
			myPagerAdapter = new MyPagerAdapter();
			this.setAdapter(myPagerAdapter);
		} else {
			myPagerAdapter.notifyDataSetChanged();
		}
		// 2.滚动轮播图,定时器，handler机制
		handler.postDelayed(runnableTask, 2000);
	}

	class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imgUrlList == null ? 1 : imgUrlList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			View view = View.inflate(getContext(), R.layout.viewpager_item,
					null);
			ImageView image = (ImageView) view.findViewById(R.id.image);
			// 给imageview设置网络上获取的图片(三级缓存)
			// 参数：第一个下载后要在哪个控件显示，第二个下载图片的链接地址
			if(!TextUtils.isEmpty(imgUrlList.get(position))) {
				bitmapUtils.display(image, imgUrlList.get(position));
			}else {
				switch (position) {
					case 0:
						image.setImageResource(R.drawable.banner);
						break;
					case 1:
						image.setImageResource(R.drawable.banner2);
						break;
					case 2:
						image.setImageResource(R.drawable.banner3);
						break;
				}
			}
//			if(imgUrlList != null && imgUrlList.size() == 3){
//				switch (position){
//					case 0:
//						image.setImageResource(R.drawable.banner);
//						break;
//					case 1:
//						image.setImageResource(R.drawable.banner2);
//						break;
//					case 2:
//						image.setImageResource(R.drawable.banner3);
//						break;
//				}
//			}else {
//				image.setImageResource(R.drawable.banner);
//			}
			// viewpager和内部view的事件分发的过程
			// 1.点下操作ACTION_DOWN先传递给viewpager，然后传递给viewpager内部的view，view做响应
			// 2.滑动触发ACTION_MOVE事件，先传递给viewpager，然后传递给viewpager内部的view，view做响应
			// 当手指滑动距离达到一定值，不再在view上做响应，此时会触发ACTION_DOWN
			// 3.当内部的view不响应事件的时候，外侧的viewpager响应后续的事件(viewpager响应ACTION_MOVE，ACTION_UP事件)
			view.setOnTouchListener(new OnTouchListener() {

				private int downX;
				private int upX;
				private long downTime;
				private long upTime;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						downX = (int) event.getX();
						downTime = System.currentTimeMillis();
						// 移除handler维护的任务
						handler.removeCallbacksAndMessages(null);
						break;
					case MotionEvent.ACTION_UP:
						upX = (int) event.getX();
						upTime = System.currentTimeMillis();
						if (downX == upX && upTime - downTime < 500) {
							// 触发点击事件,回调(定义一个接口，定义一个点击事件会触发的的未实现的方法，谁用谁实现)
							onViewClickListener.viewClick(position);
						}
						startRoll();
						break;
					case MotionEvent.ACTION_CANCEL:
						startRoll();
						break;
					}
					return true;
				}
			});
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}
}
