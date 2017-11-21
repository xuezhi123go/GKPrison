package com.gkzxhn.gkprison.utils.NomalUtils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.constant.Config;
import com.gkzxhn.gkprison.constant.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfigActivity extends AppCompatActivity {

    private static final String TAG = ConfigActivity.class.getSimpleName();
    @BindView(R.id.server_addr)EditText server_addr;
    @BindView(R.id.account)EditText account;
    @BindView(R.id.rate) EditText rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ButterKnife.bind(this);
    }

    public void save(View view){
        String addr = server_addr.getText().toString().trim();
        String acc = account.getText().toString().trim();
        String mRate = rate.getText().toString().trim();
        if (TextUtils.isEmpty(addr) && TextUtils.isEmpty(acc) && TextUtils.isEmpty(mRate))
            return;
        if (!TextUtils.isEmpty(addr)) {
            Log.i(TAG, Config.mAddr + "服务器地址" + addr);
            Config.mAddr = addr;
            ToastUtil.showShortToast("服务器地址已修改为：" + Config.mAddr);
        }
        if (!TextUtils.isEmpty(acc)) {
            Log.i(TAG, Config.mAccount + "账号" + acc);
            Config.mAccount = acc;
            ToastUtil.showShortToast("账号已由修改为：" + Config.mAccount);
        }
        if (!TextUtils.isEmpty(mRate)) {
            Log.i(TAG, Constants.RATE + "码率" + mRate);
            //TODO..修改码率
        }
        ToastUtil.showShortToast("保存成功");
        Config.isModify = true;
    }
}
