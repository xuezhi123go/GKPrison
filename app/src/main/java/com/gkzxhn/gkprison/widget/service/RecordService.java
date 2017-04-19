package com.gkzxhn.gkprison.widget.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.IBinder;
import android.widget.Toast;

import com.gkzxhn.gkprison.utils.NomalUtils.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RecordService extends Service {
  private static final String TAG = "RecordService";
  private MediaProjection mediaProjection;
  private MediaRecorder mediaRecorder;
  private VirtualDisplay virtualDisplay;

  private boolean running;
  private int width = 720;
  private int height = 1080;
  private int dpi;


  @Override
  public IBinder onBind(Intent intent) {
    return new RecordBinder();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    HandlerThread serviceThread = new HandlerThread("service_thread",
        android.os.Process.THREAD_PRIORITY_BACKGROUND);
    serviceThread.start();
    running = false;
    mediaRecorder = new MediaRecorder();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  public void setMediaProject(MediaProjection project) {
    mediaProjection = project;
  }

  public boolean isRunning() {
    return running;
  }

  public void setConfig(int width, int height, int dpi) {
    this.width = width;
    this.height = height;
    this.dpi = dpi;
  }

  public boolean startRecord() {
    if (mediaProjection == null || running) {
      return false;
    }
    Log.i("VConfVideoFrame", "start record");
    initRecorder();
    try {
      mediaRecorder.prepare();
    } catch (IOException e) {
      e.printStackTrace();
    }
    createVirtualDisplay();
    try {
      mediaRecorder.start();
    }catch (IllegalStateException e){
        e.printStackTrace();
    }
    running = true;
    Log.i("VConfVideoFrame", "start record");
    return true;
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public boolean stopRecord() {
    if (!running) {
      return false;
    }
    Log.i("stop record");
    running = false;
    try {
      mediaRecorder.stop();
    }catch (IllegalStateException e){
      e.printStackTrace();
    }

    mediaRecorder.reset();
    virtualDisplay.release();
    mediaProjection.stop();

    return true;
  }

  private void createVirtualDisplay() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      virtualDisplay = mediaProjection.createVirtualDisplay("MainScreen", width, height, dpi,
              DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), new VirtualDisplay.Callback() {
                @Override
                public void onPaused() {
                  super.onPaused();
                }

                @Override
                public void onResumed() {
                  super.onResumed();
                }

                @Override
                public void onStopped() {
                  super.onStopped();
                }
              }, null);
    }
  }

  private void initRecorder() {
    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
      mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

    }
    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);//视频格式
    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//音频格式
    mediaRecorder.setVideoSize(width, height);//视频分辨率大小
    mediaRecorder.setVideoFrameRate(30);
    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(System.currentTimeMillis()));
    mediaRecorder.setOutputFile(getsaveDirectory() + time + ".mp4");
    mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);//
  }

  public String getsaveDirectory() {
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "ScreenRecord" + "/";

      File file = new File(rootDir);
      if (!file.exists()) {
        if (!file.mkdirs()) {
          return null;
        }
      }

      Toast.makeText(getApplicationContext(), rootDir, Toast.LENGTH_SHORT).show();

      return rootDir;
    } else {
      return null;
    }
  }

  public class RecordBinder extends Binder {
    public RecordService getRecordService() {
      return RecordService.this;
    }
  }
}