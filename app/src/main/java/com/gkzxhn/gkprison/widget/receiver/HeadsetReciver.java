package com.gkzxhn.gkprison.widget.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.keda.sky.app.TruetouchGlobal;
import com.pc.utils.TerminalUtils;

/**
 * Created by 方 on 2017/10/23.
 */

public class HeadsetReciver extends BroadcastReceiver {

    private static final String TAG = "HeadsetReciver";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra("state")){
            //0：无插入，1：耳机和话筒均插入，2：仅插入话筒。
            if (intent.getIntExtra("state", 0) == 0){
                Toast.makeText(context, "耳机未插入", Toast.LENGTH_LONG).show();
                TerminalUtils.setSpeakerphoneOn(TruetouchGlobal.getContext(), true, true);
            }
            else if (intent.getIntExtra("state", 0) == 1){
                Toast.makeText(context, "耳机话筒均插入", Toast.LENGTH_LONG).show();
                TerminalUtils.setReceiverModel(TruetouchGlobal.getContext(), true, false);
            }else if(intent.getIntExtra("state", 0) == 2) {
                ToastUtil.showShortToast("话筒已插入");
            }
        }

    }
}
