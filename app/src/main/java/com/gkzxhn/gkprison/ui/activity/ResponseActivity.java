package com.gkzxhn.gkprison.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.ui.activity.normal_activity.VideoFace;
import com.gkzxhn.gkprison.utils.CustomUtils.NimInitUtil;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.gkzxhn.gkprison.zijing.ZjVideoActivity;
import com.zjrtc.ZjVideoManager;

/**
 * Created by 方 on 2017/11/23.
 */

public class ResponseActivity extends BaseActivityNew implements View.OnClickListener {

    private static final int ACTIVITY_REQUEST_CODE = 1;

    private final String TAG = ResponseActivity.class.getSimpleName();

    private TextView mTv_jail;
    private TextView mRefuse_btn;
    private TextView mResponse_btn;
    private String mRoom;
    private String mJoinPassword;

    @Override
    public int setLayoutResId() {
        return R.layout.avresponse_layout;
    }

    @Override
    protected void initUiAndListener() {
        registerReceiver();
        mTv_jail = (TextView) findViewById(R.id.tv_jail);
        mRefuse_btn = (TextView) findViewById(R.id.refuse_response_btn);
        mResponse_btn = (TextView) findViewById(R.id.video_response_btn);

        mRefuse_btn.setOnClickListener(this);
        mResponse_btn.setOnClickListener(this);
        Intent intent = getIntent();
        mRoom = intent.getStringExtra(Constants.ROOM_NUMBER);
        mJoinPassword = intent.getStringExtra(Constants.JOIN_PASSWORD);
        String jail = (String) SPUtil.get(this, SPKeyConstants.JAIL, "");
        mTv_jail.setText(jail);
        startMusic();
    }


    private MediaPlayer mMediaPlayer;

    private void startMusic() {

        Uri systemDefultRingtoneUri = getSystemDefultRingtoneUri();
        Log.i(TAG, "startMusic: " +systemDefultRingtoneUri.toString());
        mMediaPlayer = MediaPlayer.create(this, systemDefultRingtoneUri);

        if (mMediaPlayer == null) {
            return;
        }

        try {
            mMediaPlayer.setLooping(true);
//            mMediaPlayer.prepare();
        } catch (Exception e) {
            Log.i(TAG, "startAlarm: " + e.getMessage());
            e.printStackTrace();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    private void stopMusic() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    }

    //获取系统默认铃声的Uri
    private Uri getSystemDefultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(this,
                RingtoneManager.TYPE_RINGTONE);
    }

    @Override
    protected boolean isApplyStatusBarColor() {
        return true;
    }

    @Override
    protected boolean isApplyTranslucentStatus() {
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.refuse_response_btn:
                //挂断
                stopMusic();
                NimInitUtil.sendNotificationToPrison(-2);
                finish();
                break;
            case R.id.video_response_btn:
                stopMusic();
                startActivityForResult(new Intent(this, VideoFace.class), ACTIVITY_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == ACTIVITY_REQUEST_CODE){
            boolean verify = data.getBooleanExtra(VideoFace.CONFIDENCE_RESULT, false);
            double value = data.getDoubleExtra(VideoFace.CONFIDENCE_VALUE, 0);
            Log.i(TAG, "result : " + verify + ", value: " + value);
            if (verify){
                // 应答
                if (TextUtils.isEmpty(mRoom)) {
                    ToastUtil.showShortToast("房间号错误");
                    finish();
                    NimInitUtil.sendNotificationToPrison(-2);
                    return;
                }
                callRoom(mRoom, Constants.ZIJING_DOMAIN, mJoinPassword);
                finish();
            }
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("提示");
            builder.setMessage("人脸身份验证失败!");
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    NimInitUtil.sendNotificationToPrison(-2);
                    finish();
                }
            });
            builder.create().show();
        }
    }

    /**
     * 呼叫会议室
     * @param address
     * @param domain
     * @param password
     */
    private void callRoom(String address, String domain, String password) {
//设置服务器地址、显示名称、呼叫地址、呼叫密码；
        String name = (String) SPUtil.get(this, SPKeyConstants.NAME, "");
        ZjVideoManager manager = ZjVideoManager.getInstance();
        manager.setDomain(domain);
        manager.setDisplayName(name);
        manager.setBandwidth((int) SPUtil.get(MyApplication.getContext(), Constants.RATE, 1024));
        manager.setAddress(address);
        manager.setVideoSize(720, 1280);
        manager.openSpeaker(this,true);
        manager.setPwd(password);
//        manager.openSpeaker(this,true);
//        manager.setNotSupportH264(true);
//        manager.printLogs();
//        manager.setTvSupport();
//        manager.addZjCallListener(new ZjCallListenerBase(){
//
//            @Override
//            public void callState(String state, String reason) {
//                Log.e(TAG, "callState: "+state+" "+reason );
//            }
//
//            @Override
//            public void onMuteChanged(boolean muted) {
//                Log.e(TAG, "onMuteChanged: "+muted );
//            }
//        });

        Intent intent = new Intent(MyApplication.getContext(), ZjVideoActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        stopMusic();
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((Constants.ZIJING_ACTION).equals(intent.getAction())) {
                ToastUtil.showShortToast("对方已挂断");
                finish();
            }
        }
    };

    /**
     * 注册广播监听器
     */
    private void registerReceiver(){
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Constants.ZIJING_ACTION);
        registerReceiver(mBroadcastReceiver,intentFilter);
    }
}
