package com.gkzxhn.gkprison.ui.activity.normal_activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * created by huangzhengneng on 2016/1/14
 * 账户信息页面
 */
public class UserInfoActivity extends BaseActivityNew {

    private final String[] USER_INFO_LEFT_TVS = {"姓名", "身份证号", "联系电话", "与服刑人员关系", "服刑人员性别", "服刑人员囚号", "服刑人员刑期"};

    @BindView(R.id.lv_user_info)
    ListView lv_user_info;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.rl_back)
    RelativeLayout rl_back;
    @BindView(R.id.tv_refund)
    TextView tv_refund;

    @Override
    public int setLayoutResId() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        tv_title.setText(getString(R.string.account_info));
        rl_back.setVisibility(View.VISIBLE);
        lv_user_info.setAdapter(new MyAdapter());
    }

    @Override
    protected boolean isApplyStatusBarColor() {
        return true;
    }

    @Override
    protected boolean isApplyTranslucentStatus() {
        return true;
    }

    @OnClick({R.id.iv_back, R.id.tv_refund})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_refund:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("确定申请退款？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //TODO... 申请退款
                        ToastUtil.showShortToast("申请退款");
                        UserInfoActivity.this.finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                break;
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return USER_INFO_LEFT_TVS.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.user_info_item, null);
                holder = new ViewHolder();
                holder.tv_user_info_left = (TextView) convertView.findViewById(R.id.tv_user_info_left);
                holder.tv_user_info_right = (TextView) convertView.findViewById(R.id.tv_user_info_right);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_user_info_left.setText(USER_INFO_LEFT_TVS[position]);
            switch (position) {
                case 0:// 姓名
                    holder.tv_user_info_right.setText(getStringSpValue(SPKeyConstants.NAME));
                    break;
                case 1:// 身份证号
                    String pwd = getStringSpValue(SPKeyConstants.PASSWORD);
                    if (!TextUtils.isEmpty(pwd)) {
                        holder.tv_user_info_right.setText(pwd.substring(0, 4) + "******" + pwd.substring(pwd.length() - 4, pwd.length()));
                    }
                    break;
                case 2:// 联系电话
                    holder.tv_user_info_right.setText(getStringSpValue(SPKeyConstants.USERNAME));
                    break;
                case 3:// 与服刑人员关系
                    holder.tv_user_info_right.setText(getStringSpValue(SPKeyConstants.RELATION_SHIP));
                    break;
                case 4:// 服刑人员性别
                    if (getStringSpValue(SPKeyConstants.GENDER).equals("m")) {
                        holder.tv_user_info_right.setText("男");
                    } else if (getStringSpValue(SPKeyConstants.GENDER).equals("f")) {
                        holder.tv_user_info_right.setText("女");
                    }
                    break;
                case 5:// 服刑人员囚号
                    holder.tv_user_info_right.setText(getStringSpValue(SPKeyConstants.PRISONER_NUMBER));
                    break;
                case 6:// 服刑人员刑期
                    holder.tv_user_info_right.setText(getStringSpValue(SPKeyConstants.PRISON_TERM_STARTED_AT)
                            + " 至 " + getStringSpValue(SPKeyConstants.PRISON_TERM_ENDED_AT));
                    break;
            }
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView tv_user_info_left;
        TextView tv_user_info_right;
    }


    /**
     * 获取sp值
     *
     * @param key
     * @return
     */
    private String getStringSpValue(String key) {
        return (String) SPUtil.get(this, key, "");
    }
}
