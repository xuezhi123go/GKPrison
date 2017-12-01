package com.gkzxhn.gkprison.ui.activity.normal_activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.ui.activity.RegisterActivity;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;
import com.gkzxhn.gkprison.utils.NomalUtils.ImageTools;
import com.gkzxhn.gkprison.utils.NomalUtils.Log;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.pqpo.smartcropperlib.view.CropImageView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 方 on 2017/12/1.
 */

public class ImageCropActivity extends BaseActivityNew {

    private static final java.lang.String TAG = ImageCropActivity.class.getSimpleName();
    private static final float ID_RATIO = 856f / 540f ;

    @BindView(R.id.iv_crop)
    CropImageView iv_crop;// 裁剪图片
    @BindView(R.id.tv_save)
    TextView tv_save; // 保存
    private Subscription mSubscribe;
    private ProgressDialog mProgressDialog;

    @Override
    public int setLayoutResId() {
        return R.layout.activity_image_crop;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);

        Parcelable uri = getIntent().getParcelableExtra(Constants.INTENT_CROP_IMAGE_URI);
        iv_crop.setImageURI((Uri) uri);
        Bitmap bitmap = iv_crop.getBitmap();
        try {
            iv_crop.setImageToCrop(MediaStore.Images.Media.getBitmap(getContentResolver(), (Uri) uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean canRightCrop = iv_crop.canRightCrop();
        Log.i(TAG, "canRightCrop : " + canRightCrop);

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int height = (int) (bitmapWidth / ID_RATIO);
        Point[] cropPoint = new Point[4];
        int y = bitmapHeight / 4;
        cropPoint[0] = new Point(0, y);
        cropPoint[1] = new Point(bitmapWidth, y);
        cropPoint[2] = new Point(bitmapWidth, height + y);
        cropPoint[3] = new Point(0, height + y);
        iv_crop.setCropPoints(cropPoint);

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog = new ProgressDialog(ImageCropActivity.this);
                mProgressDialog.show();
                mSubscribe = Observable.create(new Observable.OnSubscribe<Boolean>() {
                    @Override
                    public void call(Subscriber<? super Boolean> subscriber) {
                        Bitmap crop = iv_crop.crop();
                        if (crop.getWidth() < crop.getHeight()) {
                            crop = rotateCrop(crop);
                        }
                        ImageTools.saveBitmap(ImageCropActivity.this,
                                "image.jpg", crop);
                        subscriber.onNext(true);
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SimpleObserver<Boolean>() {
                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                mProgressDialog.dismiss();
                                ImageCropActivity.this.setResult(RegisterActivity.RESULT_OK);
                                finish();
                            }
                        });
            }
        });
    }

    /**
     * 逆时针旋转图片
     * @param crop bitmap
     */
    private Bitmap rotateCrop(Bitmap crop) {

        Matrix matrix = new Matrix();
        matrix.postRotate(-90);

        return Bitmap.createBitmap(crop, 0, 0, crop.getWidth(), crop.getHeight(), matrix, true);

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
    protected void onDestroy() {
        if (mSubscribe != null) {
            mSubscribe.unsubscribe();
        }
        super.onDestroy();
    }
}
