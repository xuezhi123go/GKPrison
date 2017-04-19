package com.gkzxhn.gkprison.widget.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AutoRun extends BroadcastReceiver {
    public AutoRun() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = "android.intent.action.BOOT_COMPLETED";
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
//            intent.setClass(context, SplashActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
        }
    }
}
