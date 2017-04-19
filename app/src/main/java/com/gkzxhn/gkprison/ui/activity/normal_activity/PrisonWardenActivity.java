package com.gkzxhn.gkprison.ui.activity.normal_activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.ui.fragment.InterractiveMailboxFragment;
import com.gkzxhn.gkprison.ui.fragment.ReplyPublicityFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 投诉建议
 */
public class PrisonWardenActivity extends BaseActivityNew {

    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.viewpage) ViewPager viewPager;
    @BindView(R.id.rl_back) RelativeLayout rl_back;
    @BindView(R.id.rl_write_message) RelativeLayout rl_write_message;
    @BindView(R.id.tabs1) TabLayout tabLayout;

    @Override
    public int setLayoutResId() {
        return R.layout.activity_prison_warden;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        tv_title.setText(R.string.warden);
        rl_back.setVisibility(View.VISIBLE);
        rl_write_message.setVisibility(View.VISIBLE);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    @Override
    protected boolean isApplyStatusBarColor() {
        return true;
    }

    @Override
    protected boolean isApplyTranslucentStatus() {
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ReplyPublicityFragment(), getString(R.string.publish_info));
        adapter.addFragment(new InterractiveMailboxFragment(), getString(R.string.complaint_feedback));
        viewPager.setAdapter(adapter);
    }

    @OnClick({R.id.rl_back, R.id.rl_write_message})
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_write_message:
                intent = new Intent(this, WriteMessageActivity.class);
                startActivity(intent);
                break;
        }
    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        //List放置Fragment
        private final List<Fragment> mFragments = new ArrayList<>();
        //List放置标题
        private final List<String> mFragmentTitles = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //添加碎片的方法
        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
