package com.megvii.livenesslib;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.megvii.licensemanager.Manager;
import com.megvii.livenessdetection.DetectionConfig;
import com.megvii.livenessdetection.DetectionFrame;
import com.megvii.livenessdetection.Detector;
import com.megvii.livenessdetection.Detector.DetectionFailedType;
import com.megvii.livenessdetection.Detector.DetectionListener;
import com.megvii.livenessdetection.Detector.DetectionType;
import com.megvii.livenessdetection.FaceQualityManager;
import com.megvii.livenessdetection.FaceQualityManager.FaceQualityErrorType;
import com.megvii.livenessdetection.LivenessLicenseManager;
import com.megvii.livenessdetection.bean.FaceIDDataStruct;
import com.megvii.livenessdetection.bean.FaceInfo;
import com.megvii.livenesslib.util.CodeHelp;
import com.megvii.livenesslib.util.ConUtil;
import com.megvii.livenesslib.util.DialogUtil;
import com.megvii.livenesslib.util.FaceIDRequest;
import com.megvii.livenesslib.util.ICamera;
import com.megvii.livenesslib.util.IDetection;
import com.megvii.livenesslib.util.IFile;
import com.megvii.livenesslib.util.IMediaPlayer;
import com.megvii.livenesslib.util.Screen;
import com.megvii.livenesslib.util.SensorUtil;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.ContentValues.TAG;

public class LivenessActivity2 extends Activity implements PreviewCallback,
        DetectionListener, TextureView.SurfaceTextureListener {

    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String APP_KEY = "VL24sGJRPm7HgL46W2L2jekJvJRw0c9j";
    public static final String APP_SECRET = "f2lX0zkQMoeZc7rKkREL3X8JF-sSnsaP";
    public static final String IMAGE_BEST = "image_best";
    public static final String UUID = "uuid";
    public static final String IMAGE_REF_PATH = "image_ref_path";
    public static final String CONFIDENCE_RESULT = "confidence_result";
    public static final String CONFIDENCE_VALUE = "confidence_value";
    public static final String RESULT_REF1 = "result_ref1";
    public static final String CONFIDENCE = "confidence";
    private static final int CONFIDENCE_STANDARD = 80;// 人脸相似度标准   release版本需要调到85
    private TextureView camerapreview;
    private FaceMask mFaceMask;// 画脸位置的类（调试时会用到）
    private ProgressBar mProgressBar;// 网络上传请求验证时出现的ProgressBar
    private LinearLayout headViewLinear;// "请在光线充足的情况下进行检测"这个视图
    private LinearLayout rootView;// 根视图
    private TextView timeOutText;
    private LinearLayout timeOutLinear;
    private Detector mDetector;// 实体检测器
    private Handler mainHandler;
    private JSONObject jsonObject;
    private IMediaPlayer mIMediaPlayer;// 多媒体工具类
    private ICamera mICamera;// 照相机工具类
    private IFile mIFile;// 文件工具类
    private IDetection mIDetection;
    private DialogUtil mDialogUtil;
    private TextView promptText;
    private boolean isHandleStart;// 是否开始检测
    private Camera mCamera;
    private String mSession;
    private FaceQualityManager mFaceQualityManager;
    private SensorUtil sensorUtil;
    private int mFailFrame = 0;
    private int mCurStep = 0;// 检测动作的次数
    private Runnable mTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            // 倒计时开始
            initDetecteSession();
            if (mIDetection.mDetectionSteps != null)
                changeType(mIDetection.mDetectionSteps.get(0), 10);
        }
    };
    private boolean mHasSurface = false;
    private ProgressDialog mProgressDialog;
    private String uuid;
    private String imageRefPath;
//    private int cameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liveness_layout2);
        uuid = getIntent().getStringExtra(UUID);
        imageRefPath = getIntent().getStringExtra(IMAGE_REF_PATH);
//        cameraId = getIntent().getIntExtra("11",1);
        init();
        initData();
                new WarrantyTask().execute(); //验证授权代码移到，此页面的前一个页面
    }

    private void init() {
        sensorUtil = new SensorUtil(this);
        Screen.initialize(this);
        mSession = ConUtil.getFormatterTime(System.currentTimeMillis());
        mainHandler = new Handler();
        mIMediaPlayer = new IMediaPlayer(this);
        mIFile = new IFile();
        mDialogUtil = new DialogUtil(this);
        rootView = (LinearLayout) findViewById(R.id.liveness_layout_rootRel);
        mIDetection = new IDetection(this, rootView);
        mFaceMask = (FaceMask) findViewById(R.id.liveness_layout_facemask);
        mICamera = new ICamera();
        promptText = (TextView) findViewById(R.id.liveness_layout_promptText);
        camerapreview = (TextureView) findViewById(R.id.liveness_layout_textureview);
        camerapreview.setSurfaceTextureListener(this);
        mProgressBar = (ProgressBar) findViewById(R.id.liveness_layout_progressbar);
        mProgressBar.setVisibility(View.GONE);
        headViewLinear = (LinearLayout) findViewById(R.id.liveness_layout_bottom_tips_head);
        headViewLinear.setVisibility(View.VISIBLE);
        timeOutLinear = (LinearLayout) findViewById(R.id.detection_step_timeoutLinear);
        timeOutText = (TextView) findViewById(R.id.detection_step_timeout);

        mIDetection.viewsInit();
    }

    /**
     * 初始化数据
     */
    private void initData() {

        DetectionConfig config = new DetectionConfig.Builder().setDetectionTimeout(20000).build();
        mDetector = new Detector(this, config);
        boolean initSuccess = mDetector.init(this, ConUtil.readModel(this), "");
        if (!initSuccess) {
            mDialogUtil.showDialog("检测器初始化失败");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                mIDetection.animationInit();
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isHandleStart = false;
        mCamera = mICamera.openCamera(this);
        if (mCamera != null) {
            CameraInfo cameraInfo = new CameraInfo();
            Camera.getCameraInfo(1, cameraInfo);
            mFaceMask
                    .setFrontal(cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT);
            RelativeLayout.LayoutParams layout_params = mICamera
                    .getLayoutParam();
            layout_params.addRule(RelativeLayout.CENTER_IN_PARENT);//设置摄像头预览在屏幕中间
            camerapreview.setLayoutParams(layout_params);
            mFaceMask.setLayoutParams(layout_params);
            mFaceQualityManager = new FaceQualityManager(1 - 0.5f, 0.5f);
            mIDetection.mCurShowIndex = -1;
        } else {
            mDialogUtil.showDialog("打开前置摄像头失败");
        }
    }

    /**
     * 开始检测
     */
    private void handleStart() {
        if (isHandleStart)
            return;
        isHandleStart = true;
        Animation animationIN = AnimationUtils.loadAnimation(
                LivenessActivity2.this, R.anim.liveness_rightin);
        Animation animationOut = AnimationUtils.loadAnimation(
                LivenessActivity2.this, R.anim.liveness_leftout);
        headViewLinear.startAnimation(animationOut);
        mIDetection.mAnimViews[0].setVisibility(View.VISIBLE);
        mIDetection.mAnimViews[0].startAnimation(animationIN);
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                timeOutLinear.setVisibility(View.VISIBLE);
            }
        });
        mainHandler.post(mTimeoutRunnable);

        try {
            jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonObject.put("imgs", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initDetecteSession() {
        if (mICamera.mCamera == null)
            return;

        mProgressBar.setVisibility(View.GONE);
        mIDetection.detectionTypeInit();

        mCurStep = 0;
        mDetector.reset();
        mDetector.changeDetectionType(mIDetection.mDetectionSteps.get(0));
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Size previewsize = camera.getParameters().getPreviewSize();
        mDetector.doDetection(data, previewsize.width, previewsize.height,
                360 - mICamera.getCameraAngle(this));
    }

    /**
     * 实体验证成功
     */
    @Override
    public DetectionType onDetectionSuccess(final DetectionFrame validFrame) {


        mIMediaPlayer.reset();
        mCurStep++;
        mFaceMask.setFaceInfo(null);

        if (mCurStep >= mIDetection.mDetectionSteps.size()) {
            //            mProgressBar.setVisibility(View.VISIBLE);
            //            handleResult(R.string.verify_success);

            FaceIDDataStruct faceIDDataStruct = mDetector.getFaceIDDataStruct();
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle("正在验证");
            mProgressDialog.setTitle("正在验证您的身份");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            getBestImageAndDelta(faceIDDataStruct);

            File imageFile = new File(getExternalCacheDir(), IMAGE_BEST + ".jpg");
            Logger.e("imageFile = " + imageFile);
            File imageRefFile = new File(imageRefPath);
            Logger.e("imageRefFile = " + imageRefFile);
            verify(imageRefFile, imageFile);
        } else
            changeType(mIDetection.mDetectionSteps.get(mCurStep), 10);

        // 检测器返回值：如果不希望检测器检测则返回DetectionType.DONE，如果希望检测器检测动作则返回要检测的动作
        return mCurStep >= mIDetection.mDetectionSteps.size() ? DetectionType.DONE
                : mIDetection.mDetectionSteps.get(mCurStep);
    }

    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                MediaType.parse(MULTIPART_FORM_DATA), descriptionString);
    }

    private MultipartBody.Part prepareFilePart(String partName, File file) {

        RequestBody requestFile =
                RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);

        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void verify(File imageRef1File, File imageFile) {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.megvii.com/faceid/")
                .build();
        FaceIDRequest faceIDRequest = retrofit.create(FaceIDRequest.class);

        RequestBody partFromString1 = createPartFromString(APP_KEY);
        RequestBody partFromString2 = createPartFromString(APP_SECRET);
        RequestBody partFromString3 = createPartFromString("0");
        RequestBody partFromString4 = createPartFromString("raw_image");
        RequestBody partFromString5 = createPartFromString(uuid);

        MultipartBody.Part image_ref1 = prepareFilePart("image_ref1", imageRef1File);
        MultipartBody.Part image = prepareFilePart("image", imageFile);

        Call<ResponseBody> verify = faceIDRequest.verify(partFromString1
                , partFromString2
                , partFromString3
                , partFromString4
                , partFromString5
                , image_ref1
                , image);


        verify.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();

                if (response.isSuccessful()) {
                    String json = null;
                    double confidence = 0;
                    Logger.e("response raw  = " + response.raw());
                    try {
                        json = response.body().string();
                        Logger.e("json  = " + json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONObject jsonObject1 = jsonObject.optJSONObject(RESULT_REF1);
                        confidence = jsonObject1.optDouble(CONFIDENCE);

                        Toast.makeText(LivenessActivity2.this
                                , "相似度有百分之" + confidence
                                , Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (confidence >= CONFIDENCE_STANDARD) {
                        setFaceResult(RESULT_OK, true, confidence);
                    } else {
                        setFaceResult(RESULT_CANCELED, false, confidence);
                    }

                } else {
                    setFaceResult(RESULT_CANCELED, false, 0);

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Logger.e("出错  = " + t.getMessage());
                Logger.e("出错  = " + t);
                mProgressDialog.dismiss();
                setFaceResult(RESULT_CANCELED, false, 0);
                //                setResult(RESULT_CANCELED);
                //                onBackPressed();
            }
        });
    }

    /**
     * 获取活体检测的BestImage和Delta 注意：需要在活体检测成功后调用
     * <p>
     * 如何获取idDataStruct： （从活体检测器中获取） FaceIDDataStruct idDataStruct =
     * detector.getFaceIDDataStruct();
     */
    public void getBestImageAndDelta(FaceIDDataStruct idDataStruct) {
        String delta = idDataStruct.delta; // 获取delta；
        HashMap<String, byte[]> images = (HashMap<String, byte[]>) idDataStruct.images;// 获取所有图片
        for (String key : idDataStruct.images.keySet()) {
            byte[] data = idDataStruct.images.get(key);
            Logger.e("key = " + key);
            if (key.equals(IMAGE_BEST)) {
                byte[] imageBestData = data;// 这是最好的一张图片
                CodeHelp.saveJPGFile(this, imageBestData, key);
            } else if (key.equals("image_env")) {
                byte[] imageEnvData = data;// 这是一张全景图 q
                //                CodeHelp.saveJPGFile(this, imageEnvData, key);
            } else {
                // 其余为其他图片，根据需求自取
            }
        }
    }

    /**
     * 活体检测失败
     */
    @Override
    public void onDetectionFailed(final DetectionFailedType type) {

        //        Logger.e("type.name() = " + type.name());

        new Thread(new Runnable() {
            @Override
            public void run() {
                mIFile.saveLog(mSession, type.name());
            }
        }).start();
        int resourceID = R.string.liveness_detection_failed;
        switch (type) {
            case ACTIONBLEND:
                resourceID = R.string.liveness_detection_failed_action_blend;
                break;
            case NOTVIDEO:
                resourceID = R.string.liveness_detection_failed_not_video;
                break;
            case TIMEOUT:
                resourceID = R.string.liveness_detection_failed_timeout;
                break;
        }

        Toast.makeText(LivenessActivity2.this, resourceID, Toast.LENGTH_LONG).show();
        setFaceResult(RESULT_CANCELED, false, 0);
        //        handleResult(resourceID);
    }

    /**
     * 活体验证中
     */
    @Override
    public void onFrameDetected(long timeout, DetectionFrame detectionFrame) {
//        if (sensorUtil.isVertical()) {
            faceOcclusion(detectionFrame);
            handleNotPass(timeout);
            mFaceMask.setFaceInfo(detectionFrame);
//        } else
//            promptText.setText("请竖直握紧手机");
    }

    private void faceOcclusion(DetectionFrame detectionFrame) {
        mFailFrame++;
        if (detectionFrame != null) {
            FaceInfo faceInfo = detectionFrame.getFaceInfo();
            if (faceInfo != null) {
                if (faceInfo.eyeLeftOcclusion > 0.5
                        || faceInfo.eyeRightOcclusion > 0.5) {
                    if (mFailFrame > 10) {
                        mFailFrame = 0;
                        promptText.setText("请勿用手遮挡眼睛");
                    }
                    return;
                }
                if (faceInfo.mouthOcclusion > 0.5) {
                    if (mFailFrame > 10) {
                        mFailFrame = 0;
                        promptText.setText("请勿用手遮挡嘴巴");
                    }
                    return;
                }
            }
        }
        faceInfoChecker(mFaceQualityManager.feedFrame(detectionFrame));
    }

    public void faceInfoChecker(List<FaceQualityErrorType> errorTypeList) {
        if (errorTypeList == null || errorTypeList.size() == 0)
            handleStart();
        else {
            String infoStr = "";
            FaceQualityErrorType errorType = errorTypeList.get(0);
            if (errorType == FaceQualityErrorType.FACE_NOT_FOUND) {
                infoStr = "请让我看到您的正脸";
            } else if (errorType == FaceQualityErrorType.FACE_POS_DEVIATED) {
                infoStr = "请让我看到您的正脸";
            } else if (errorType == FaceQualityErrorType.FACE_NONINTEGRITY) {
                infoStr = "请让我看到您的正脸";
            } else if (errorType == FaceQualityErrorType.FACE_TOO_DARK) {
                infoStr = "请让光线再亮点";
            } else if (errorType == FaceQualityErrorType.FACE_TOO_BRIGHT) {
                infoStr = "请让光线再暗点";
            } else if (errorType == FaceQualityErrorType.FACE_TOO_SMALL) {
                /*infoStr = "请再靠近一些";
                if (isMeizuXiaomi()) {*/
                    infoStr = "";
                    handleStart();
//                }
            } else if (errorType == FaceQualityErrorType.FACE_TOO_LARGE) {
                /*infoStr = "请再离远一些";
                if (isMeizuXiaomi()) {*/
                    infoStr = "";
                    handleStart();
//                }
            } else if (errorType == FaceQualityErrorType.FACE_TOO_BLURRY) {
                infoStr = "请避免侧光和背光";
            } else if (errorType == FaceQualityErrorType.FACE_OUT_OF_RECT) {
                infoStr = "请保持脸在人脸框中";
            }

            // mFailFrame++;
            if (mFailFrame > 10) {
                mFailFrame = 0;
                promptText.setText(infoStr);
            }
        }
    }

    public static boolean isMeizuXiaomi() {
        String manufacturer = Build.MANUFACTURER;
        Log.i(TAG, "isMeizuXiaomi: " + manufacturer);
        return "Meizu".equals(manufacturer) || "Xiaomi".equals(manufacturer) || "LeMobile".equals(manufacturer);
    }

    /**
     * 跳转Activity传递信息
     */
    private void handleResult(final int resID) {
        String resultString = getResources().getString(resID);
        try {
            jsonObject.put("result", resultString);
            jsonObject.put("resultcode", resID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.putExtra("result", jsonObject.toString());
        Logger.e("jsonObject= " + jsonObject);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setFaceResult(int resultCode, boolean confidenceResult, double confidenceValue) {

        Intent intent = new Intent();
        intent.putExtra(CONFIDENCE_RESULT, confidenceResult);
        intent.putExtra(CONFIDENCE_VALUE, confidenceValue);
        setResult(resultCode, intent);
        onBackPressed();


    }

    public void changeType(final DetectionType detectiontype,
                           long timeout) {
        mIDetection.changeType(detectiontype, timeout);
        mFaceMask.setFaceInfo(null);

        if (mCurStep == 0) {
            mIMediaPlayer.doPlay(mIMediaPlayer.getSoundRes(detectiontype));
        } else {
            mIMediaPlayer.doPlay(R.raw.meglive_well_done);
            mIMediaPlayer.setOnCompletionListener(detectiontype);
        }
    }

    public void handleNotPass(final long remainTime) {
        if (remainTime > 0) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    timeOutText.setText(remainTime / 1000 + "");
                }
            });
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
                                          int height) {
        mHasSurface = true;
        doPreview();

        // 添加活体检测回调
        mDetector.setDetectionListener(this);
        mICamera.actionDetect(this);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
                                            int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mHasSurface = false;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private void doPreview() {
        if (!mHasSurface)
            return;

        mICamera.startPreview(camerapreview.getSurfaceTexture());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainHandler.removeCallbacksAndMessages(null);
        mICamera.closeCamera();
        mCamera = null;
        mIMediaPlayer.close();

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDetector != null)
            mDetector.release();
        mDialogUtil.onDestory();
        mIDetection.onDestroy();
        sensorUtil.release();
    }


        class WarrantyTask extends AsyncTask<Void, Void, Integer> {


            private ProgressDialog mProgressDialog = new ProgressDialog(LivenessActivity2.this);


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                mProgressDialog.setTitle("授权");
                mProgressDialog.setMessage("正在联网授权中...");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
            }

            @Override
            protected Integer doInBackground(Void... params) {


                Manager manager = new Manager(LivenessActivity2.this);
                LivenessLicenseManager licenseManager = new LivenessLicenseManager(
                        LivenessActivity2.this);
                manager.registerLicenseManager(licenseManager);

                manager.takeLicenseFromNetwork(ConUtil.getUUIDString(LivenessActivity2.this));
                if (licenseManager.checkCachedLicense() > 0)
                    return 1;
                else
                    return 0;


            }


            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                mProgressDialog.dismiss();
                if (integer == 1) {
                      uuid = getIntent().getStringExtra(UUID);
                      imageRefPath = getIntent().getStringExtra(IMAGE_REF_PATH);


                } else if (integer == 0) {
                    setFaceResult(RESULT_CANCELED, false, 0);
                }
            }
        }


}