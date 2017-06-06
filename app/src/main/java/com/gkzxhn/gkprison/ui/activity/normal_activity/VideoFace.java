package com.gkzxhn.gkprison.ui.activity.normal_activity;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.faceutil.FaceRect;
import com.gkzxhn.gkprison.utils.faceutil.FaceUtil;
import com.gkzxhn.gkprison.utils.faceutil.ParseResult;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.FaceDetector;
import com.iflytek.cloud.FaceRequest;
import com.iflytek.cloud.RequestListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.util.Accelerometer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 离线视频流检测示例
 * 该业务仅支持离线人脸检测SDK，请开发者前往<a href="http://www.xfyun.cn/">讯飞语音云</a>SDK下载界面，下载对应离线SDK
 */
public class VideoFace extends Activity {
	private final static String TAG = VideoFace.class.getSimpleName();
	private SurfaceView mPreviewSurface;
	private SurfaceView mFaceSurface;
	private Camera mCamera;
	private int mCameraId = CameraInfo.CAMERA_FACING_FRONT;
	// Camera nv21格式预览帧的尺寸，默认设置640*480
	private int PREVIEW_WIDTH = 640;
	private int PREVIEW_HEIGHT = 480;
	// 预览帧数据存储数组和缓存数组
	private byte[] nv21;
	private byte[] buffer;
	// 缩放矩阵
	private Matrix mScaleMatrix = new Matrix();
	// 加速度感应器，用于获取手机的朝向
	private Accelerometer mAcc;
	// FaceDetector对象，集成了离线人脸识别：人脸检测、视频流检测功能
	private FaceDetector mFaceDetector;
	private boolean mStopTrack;
	private Toast mToast;
	private long mLastClickTime;
	private int isAlign = 1;
    private byte[] mData;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_demo);

		initUI();

		nv21 = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
		buffer = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
		mAcc = new Accelerometer(VideoFace.this);
		mFaceDetector = FaceDetector.createDetector(VideoFace.this, null);
	}


	private Callback mPreviewCallback = new Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			closeCamera();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			openCamera();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
			mScaleMatrix.setScale(width/(float)PREVIEW_HEIGHT, height/(float)PREVIEW_WIDTH);
		}
	};

	private void setSurfaceSize() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;
		int height = (int) (width * PREVIEW_WIDTH / (float)PREVIEW_HEIGHT);
		RelativeLayout.LayoutParams params = new LayoutParams(width, height);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

		mPreviewSurface.setLayoutParams(params);
		mFaceSurface.setLayoutParams(params);
	}

	@SuppressLint("ShowToast")
	@SuppressWarnings("deprecation")
	private void initUI() {
		mPreviewSurface = (SurfaceView) findViewById(R.id.sfv_preview);
		mFaceSurface = (SurfaceView) findViewById(R.id.sfv_face);
        mFaceRequest = new FaceRequest(this);

		mPreviewSurface.getHolder().addCallback(mPreviewCallback);
		mPreviewSurface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mFaceSurface.setZOrderOnTop(true);
		mFaceSurface.getHolder().setFormat(PixelFormat.TRANSLUCENT);

		// 点击SurfaceView，切换摄相头
		mFaceSurface.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 只有一个摄相头，不支持切换
				if (Camera.getNumberOfCameras() == 1) {
					showTip("只有后置摄像头，不能切换");
					return;
				}
				closeCamera();
				if (CameraInfo.CAMERA_FACING_FRONT == mCameraId) {
					mCameraId = CameraInfo.CAMERA_FACING_BACK;
				} else {
					mCameraId = CameraInfo.CAMERA_FACING_FRONT;
				}
				openCamera();
			}
		});

		// 长按SurfaceView 500ms后松开，摄相头聚集
		mFaceSurface.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						mLastClickTime = System.currentTimeMillis();
						break;
					case MotionEvent.ACTION_UP:
						if (System.currentTimeMillis() - mLastClickTime > 500) {
							mCamera.autoFocus(null);
							return true;
						}
						break;

					default:
						break;
				}
				return false;
			}
		});

		setSurfaceSize();
		mToast = Toast.makeText(VideoFace.this, "", Toast.LENGTH_SHORT);
	}

	private void openCamera() {
		if (null != mCamera) {
			return;
		}

		if (!checkCameraPermission()) {
			showTip("摄像头权限未打开，请打开后再试");
			mStopTrack = true;
			return;
		}

		// 只有一个摄相头，打开后置
		if (Camera.getNumberOfCameras() == 1) {
			mCameraId = CameraInfo.CAMERA_FACING_BACK;
		}

		try {
			mCamera = Camera.open(mCameraId);
			if (CameraInfo.CAMERA_FACING_FRONT == mCameraId) {
				showTip("前置摄像头已开启，点击可切换");
			} else {
				showTip("后置摄像头已开启，点击可切换");
			}
		} catch (Exception e) {
			e.printStackTrace();
			closeCamera();
			return;
		}

		Parameters params = mCamera.getParameters();
		params.setPreviewFormat(ImageFormat.NV21);
		params.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
		mCamera.setParameters(params);

		// 设置显示的偏转角度，大部分机器是顺时针90度，某些机器需要按情况设置
		mCamera.setDisplayOrientation(90);
		mCamera.setPreviewCallback(new PreviewCallback() {

			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				System.arraycopy(data, 0, nv21, 0, data.length);
			}
		});

		try {
			mCamera.setPreviewDisplay(mPreviewSurface.getHolder());
			mCamera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(mFaceDetector == null) {
			/**
			 * 离线视频流检测功能需要单独下载支持离线人脸的SDK
			 * 请开发者前往语音云官网下载对应SDK
			 */
			// 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
			showTip( "创建对象失败，请确认 libmsc.so 放置正确，\n 且有调用 createUtility 进行初始化" );
		}
	}

	private void closeCamera() {
		if (null != mCamera) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	private boolean checkCameraPermission() {
		int status = checkPermission(permission.CAMERA, Process.myPid(), Process.myUid());
		if (PackageManager.PERMISSION_GRANTED == status) {
			return true;
		}

		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (null != mAcc) {
			mAcc.start();
		}

		mStopTrack = false;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (!mStopTrack) {
					if (null == nv21) {
						continue;
					}

					synchronized (nv21) {
						System.arraycopy(nv21, 0, buffer, 0, nv21.length);
					}

					// 获取手机朝向，返回值0,1,2,3分别表示0,90,180和270度
					int direction = Accelerometer.getDirection();
					boolean frontCamera = (Camera.CameraInfo.CAMERA_FACING_FRONT == mCameraId);
					// 前置摄像头预览显示的是镜像，需要将手机朝向换算成摄相头视角下的朝向。
					// 转换公式：a' = (360 - a)%360，a为人眼视角下的朝向（单位：角度）
					if (frontCamera) {
						// SDK中使用0,1,2,3,4分别表示0,90,180,270和360度
						direction = (4 - direction)%4;
					}

					if(mFaceDetector == null) {
						/**
						 * 离线视频流检测功能需要单独下载支持离线人脸的SDK
						 * 请开发者前往语音云官网下载对应SDK
						 */
						// 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
						showTip( "创建对象失败，请确认 libmsc.so 放置正确，\n 且有调用 createUtility 进行初始化" );
						break;
					}

					String result = mFaceDetector.trackNV21(buffer, PREVIEW_WIDTH, PREVIEW_HEIGHT, isAlign, direction);
					Log.d(TAG, "result:"+result);

					FaceRect[] faces = ParseResult.parseResult(result);

					Canvas canvas = mFaceSurface.getHolder().lockCanvas();
					if (null == canvas) {
						continue;
					}

					canvas.drawColor(0, PorterDuff.Mode.CLEAR);
					canvas.setMatrix(mScaleMatrix);

					if( faces == null || faces.length <=0 ) {
						mFaceSurface.getHolder().unlockCanvasAndPost(canvas);
						continue;
					}

					if (null != faces && frontCamera == (Camera.CameraInfo.CAMERA_FACING_FRONT == mCameraId)) {
						for (FaceRect face: faces) {
							face.bound = FaceUtil.RotateDeg90(face.bound, PREVIEW_WIDTH, PREVIEW_HEIGHT);
							if (face.point != null) {
								for (int i = 0; i < face.point.length; i++) {
									face.point[i] = FaceUtil.RotateDeg90(face.point[i], PREVIEW_WIDTH, PREVIEW_HEIGHT);
								}
							}
							FaceUtil.drawFaceRect(canvas, face, PREVIEW_WIDTH, PREVIEW_HEIGHT,
									frontCamera, false);
						}
                        if (mData != null && flag) {
                            // 设置用户标识，格式为6-18个字符（由字母、数字、下划线组成，不得以数字开头，不能包含空格）。
                            // 当不设置时，云端将使用用户设备的设备ID来标识终端用户。
                            String mAuthid = (String) SPUtil.get(MyApplication.getContext(), SPKeyConstants.USERNAME, "");
                            mFaceRequest.setParameter(SpeechConstant.AUTH_ID, mAuthid);
                            mFaceRequest.setParameter(SpeechConstant.WFR_SST, "verify");
                            mFaceRequest.sendRequest(mData, mRequestListener);
                            flag = false;
                        }else if (mData == null){
                            getPreViewImage();
                        }
                } else {
						Log.d(TAG, "faces:0");
					}

					mFaceSurface.getHolder().unlockCanvasAndPost(canvas);
				}
			}
		}).start();
	}

    private boolean flag = true;
    // FaceRequest对象，集成了人脸识别的各种功能
    private FaceRequest mFaceRequest;
    // authid为6-18个字符长度，用于唯一标识用户
    private String mAuthid = null;
    private RequestListener mRequestListener = new RequestListener() {

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {

            try {
                String result = new String(buffer, "utf-8");
                Log.d("FaceDemo", result);

                JSONObject object = new JSONObject(result);
                String type = object.optString("sst");
                if ("reg".equals(type)) {
//                    register(object);
                } else if ("verify".equals(type)) {
                    verify(object);
//                    finish();
                } else if ("detect".equals(type)) {
//                    detect(object);
                } else if ("align".equals(type)) {
//                    align(object);
                }
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO: handle exception
            }
        }

        @Override
        public void onCompleted(SpeechError error) {

            if (error != null) {
                switch (error.getErrorCode()) {
                    case ErrorCode.MSP_ERROR_ALREADY_EXIST:
                        showTip("authid已经被注册，请更换后再试");
                        break;

                    default:
                        showTip(error.getPlainDescription(true));
                        break;
                }
            }
        }
    };

	public static final String CONFIDENCE_RESULT = "confidence_result";
	public static final String CONFIDENCE_VALUE = "confidence_value";

    private void verify(JSONObject obj) throws JSONException {
            int ret = obj.getInt("ret");
            Intent intent = new Intent();
            if (ret != 0) {
                showTip("验证失败");
                setResult(RESULT_CANCELED, intent);
                finish();
                return;
            }
            if ("success".equals(obj.get("rst"))) {
            intent.putExtra(CONFIDENCE_RESULT, obj.getBoolean("verf"));
            intent.putExtra(CONFIDENCE_VALUE, obj.getDouble("score"));
                if (obj.getBoolean("verf")) {
                    showTip("通过验证,匹配率: " + obj.getDouble("score"));
                    setResult(RESULT_OK, intent);
                } else {
                    showTip("验证不通过,匹配率: " + obj.getDouble("score"));
                    setResult(RESULT_CANCELED, intent);
                }
            } else {
                showTip("验证失败");
                setResult(RESULT_CANCELED, intent);
            }
            finish();
        }

    private void getPreViewImage() {

        mCamera.setOneShotPreviewCallback(new Camera.PreviewCallback(){

            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Size size = camera.getParameters().getPreviewSize();
                try{
                    YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                    if(image!=null){
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);

                        Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());

                        //**********************
                        //因为图片会放生旋转，因此要对图片进行旋转到和手机在一个方向上
                        bmp = rotateMyBitmap(bmp);
                        //**********************************
                        if (null != bmp) {
                            FaceUtil.saveBitmapToFile(VideoFace.this, bmp);
                        }

                        // 获取图片保存路径
                        String fileSrc = FaceUtil.getImagePath(VideoFace.this);
                        // 获取图片的宽和高
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        bmp = BitmapFactory.decodeFile(fileSrc, options);

                        // 压缩图片
                        options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
                                (double) options.outWidth / 1024f,
                                (double) options.outHeight / 1024f)));
                        options.inJustDecodeBounds = false;
                        bmp = BitmapFactory.decodeFile(fileSrc, options);

                        // 部分手机会对图片做旋转，这里检测旋转角度
                        int degree = FaceUtil.readPictureDegree(fileSrc);
                        if (degree != 0) {
                            // 把图片旋转为正的方向
                            bmp = FaceUtil.rotateImage(degree, bmp);
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        //可根据流量及网络状况对图片进行压缩
                        bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                        mData = baos.toByteArray();

                        stream.close();
                        ((ImageView) findViewById(R.id.imageview)).setImageBitmap(bmp);
                    }
                }catch(Exception ex){
                    Log.e("Sys","Error:"+ex.getMessage());
                }
//                mData = data;
            }
        });
    }

    public Bitmap rotateMyBitmap(Bitmap bmp){
        //*****旋转一下
        Matrix matrix = new Matrix();
        matrix.postRotate(-90);

        Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);

        Bitmap nbmp2 = Bitmap.createBitmap(bmp, 0,0, bmp.getWidth(),  bmp.getHeight(), matrix, true);

        return nbmp2;
    }

	@Override
	protected void onPause() {
		super.onPause();
        Log.i(TAG, "onPause: ======");
        closeCamera();
		if (null != mAcc) {
			mAcc.stop();
		}
		mStopTrack = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if( null != mFaceDetector ){
			// 销毁对象
			mFaceDetector.destroy();
		}
	}

	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}

}
