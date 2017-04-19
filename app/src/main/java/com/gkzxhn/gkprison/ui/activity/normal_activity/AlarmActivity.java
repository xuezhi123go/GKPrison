package com.gkzxhn.gkprison.ui.activity.normal_activity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.ImageView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * created by hzn 2016/1/20
 * 闹钟提醒页面
 */
public class AlarmActivity extends BaseActivityNew {

    @BindView(R.id.iv_alarm_icon) ImageView iv_alarm_icon;
    private Vibrator vibrator;
    private MediaPlayer player;
    private Handler handler = new Handler();
    private int index = 0;

    @Override
    public int setLayoutResId() {
        return R.layout.activity_alarm;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        handler.post(alarm_Task);
        player = MediaPlayer.create(this, R.raw.alarm);
        player.setVolume(1.0f, 1.0f);
        player.setLooping(true);
        player.start();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {500, 500, 500, 500};
        vibrator.vibrate(pattern, 2);
    }

    @Override
    protected boolean isApplyStatusBarColor() {
        return true;
    }

    @Override
    protected boolean isApplyTranslucentStatus() {
        return true;
    }

    /**
     * 闹钟动画任务
     */
    private Runnable alarm_Task = new Runnable() {
        @Override
        public void run() {
            if(index % 2 == 0){
                iv_alarm_icon.setImageResource(R.drawable.alarm_right);
            }else {
                iv_alarm_icon.setImageResource(R.drawable.alarm_left);
            }
            index++;
            handler.postDelayed(alarm_Task, 100);
        }
    };

    @OnClick(R.id.tv_alarm_stop)
    public void onClick() {
        player.stop();
        vibrator.cancel();
        handler.removeCallbacks(alarm_Task);
        AlarmActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        if (player != null && player.isPlaying()){
            return;
        }
        super.onBackPressed();
    }
}
