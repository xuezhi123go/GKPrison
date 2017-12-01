package com.gkzxhn.gkprison.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.dagger.componet.activity.DaggerRegisterComponent;
import com.gkzxhn.gkprison.dagger.contract.RegisterContract;
import com.gkzxhn.gkprison.presenter.activity.RegisterPresenter;
import com.gkzxhn.gkprison.ui.activity.normal_activity.ImageCropActivity;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.NomalUtils.DensityUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ImageTools;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.SystemUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.UIUtils;
import com.gkzxhn.gkprison.widget.view.auto.AutoCompleteTv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author: Huang ZN
 * Date: 2016/12/26
 * Email:943852572@qq.com
 * Description:
 */

public class RegisterActivity extends BaseActivityNew implements RegisterContract.View, CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {

    private static final String TAG = RegisterActivity.class.getName();
    private static final int SCALE = 5;
    private static final float ID_RATIO = 856f / 540f ;
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.rl_back) RelativeLayout rl_back;

    @BindView(R.id.et_name) EditText et_name;// 姓名
    @BindView(R.id.rb_male) RadioButton rb_male;
    @BindView(R.id.rb_female) RadioButton rb_female;
    @BindView(R.id.rg_sex) RadioGroup rg_sex;// 性别
    @BindView(R.id.iv_user_icon) ImageView iv_user_icon;// 头像
    @BindView(R.id.et_ic_card) EditText et_ic_card;// 身份证号
    @BindView(R.id.et_phone_num) EditText et_phone_num;// 手机号
    @BindView(R.id.et_relationship_with_prisoner) EditText et_relationship_with_prisoner;// 与服刑人员关系
    @BindView(R.id.et_prisoner_num) EditText et_prisoner_num;// 服刑人员囚号
    @BindView(R.id.actv_prison_choose) AutoCompleteTv actv_prison_choose;//监狱选择
    @BindView(R.id.et_identifying_code) EditText et_identifying_code;// 验证码
    @BindView(R.id.bt_send_identifying_code) Button bt_send_identifying_code;// 发送验证码
    @BindView(R.id.iv_add_photo_01) ImageView iv_add_photo_01;
    @BindView(R.id.iv_add_photo_02) ImageView iv_add_photo_02;
    @BindView(R.id.cb_agree_disagree) CheckBox cb_agree_disagree;// 我已阅读复选框
    @BindView(R.id.tv_read) TextView tv_read;// 我已阅读协议
    @BindView(R.id.tv_software_protocol) TextView tv_software_protocol;// 蓝色软件协议
    @BindView(R.id.bt_register) Button bt_register;// 提交申请

    private String name = "";// 姓名
    private String id_num = "";// 身份证号
    private String phone_num = "";// 手机号码
    private String relationship_with_prisoner = "";// 与囚犯关系
    private String prisoner_number = "";// 囚号输入框内容
    private String prison_choose = "";// 监狱选择输入框内容
    private String identifying_code = "";// 验证码输入框的内容
    private String sex = "男";// 性别

    // 三个位图  头像以及身份证正反面照
    private Bitmap newBitmap1;
    private Bitmap newBitmap2;
    private Bitmap newBitmap3;
    private int imageClick = 0;// 判断点击的是哪个图片  三个位图  头像以及身份证正反面照

    @Inject
    RegisterPresenter mPresenter;
    private ProgressDialog dialog;
    private AlertDialog reminderDialog;
    private AlertDialog agreement_dialog;
    private AlertDialog listDialig;
    private AlertDialog confirmDialog;
    private AlertDialog resultDialog;

    private Handler handler;
    private int countdown = 60;

    // 头像身份证图片相关
    private static final int TAKE_PHOTO = 0; //imageview1照相;
    private static final int CHOOSE_PHOTO = 1;//imageview1选图片;
    private static final int CROP_SMALL_PICTURE = 2;
    private static final int CROP_SMALL_ID1 = 3;
    private static final int CROP_SMALL_ID2 = 4;
    private String uploadFile1 = "";
    private String uploadFile2 = "";
    private Bitmap photo;// 相册选取的bitmap

    private List<String> prisonNameList;
    private Map<String, Integer> prisonDataList;
    private Uri mImageUri;  // takephoto临时文件

    /**
     * 开启此activity
     * @param mContext
     */
    public static void startActivity(Context mContext){
        Intent intent = new Intent(mContext, RegisterActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public void showMainUi() {
        tv_title.setText(getString(R.string.register));
        rl_back.setVisibility(View.VISIBLE);
        iv_add_photo_01.setTag(1);
        iv_add_photo_02.setTag(2);
        // 设置两个性别图标大小
        Drawable[] drawables = rb_male.getCompoundDrawables();
        drawables[0].setBounds(0, 0, DensityUtil.dip2px(getApplicationContext(),
                30), DensityUtil.dip2px(getApplicationContext(), 30));
        rb_male.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
        Drawable[] drawables2 = rb_female.getCompoundDrawables();
        drawables2[0].setBounds(0, 0, DensityUtil.dip2px(getApplicationContext(), 30),
                DensityUtil.dip2px(getApplicationContext(), 30));
        rb_female.setCompoundDrawables(drawables2[0], drawables2[1], drawables2[2], drawables2[3]);
        cb_agree_disagree.setOnCheckedChangeListener(this);
        rg_sex.setOnCheckedChangeListener(this);
    }

    @Override
    public void showProgress(String msg) {
        if (dialog == null){
            dialog = UIUtils.showProgressDialog(this, msg);
        }else {
            if (!dialog.isShowing())
                dialog.show();
        }
    }

    @Override
    public void dismissProgress() {
        UIUtils.dismissProgressDialog(dialog);
    }

    @Override
    public void showToast(String msg) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()){
            throw new IllegalStateException("must show toast in main thread");
        }
        ToastUtil.showShortToast(msg);
    }

    @Override
    public void startCountDown() {
        if (!isRunning) {
            handler = new Handler();
            bt_send_identifying_code.setEnabled(false);
            bt_send_identifying_code.setBackgroundColor(getResources().getColor(R.color.tv_gray));
            bt_send_identifying_code.setTextColor(getResources().getColor(R.color.white));
            handler.post(count_down_task);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void removeCountDown() {
        removeCodeTask();
    }

    @Override
    public void checkVerifyCodeSuccess() {
        prisonNameList = actv_prison_choose.getNameList();
        prisonDataList = actv_prison_choose.getDataList();
        mPresenter.register(prisonDataList, newBitmap3, newBitmap1, newBitmap2, name,
        id_num, phone_num, relationship_with_prisoner,
        prisoner_number, prison_choose, identifying_code, sex); // 发送注册信息至服务器
        SPUtil.put(RegisterActivity.this, SPKeyConstants.PRISON_NAME, prison_choose);
    }

    @Override
    public void showResultDialog(final boolean isSuccess, String msg) {
        resultDialog = UIUtils.showAlertDialog(this, msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (isSuccess){
                    RegisterActivity.this.finish();
                }
            }
        }, null, false);
    }

    @Override
    public int setLayoutResId() {
        return R.layout.activity_register_new;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        mPresenter.attachView(this);
    }

    @OnClick({R.id.iv_user_icon, R.id.bt_send_identifying_code,
            R.id.iv_add_photo_01, R.id.iv_add_photo_02,
            R.id.tv_software_protocol, R.id.bt_register, R.id.rl_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_send_identifying_code:// 发送验证码
                if (isRunning)
                    return; // 正在倒计时
                if (SystemUtil.isNetWorkUnAvailable())
                    return;// 网络不可用
                mPresenter.sendVerifyCode(et_phone_num.getText().toString().trim());
                break;
            case R.id.iv_add_photo_01:// 添加身份证正反面照片
                showPhotoPicker(true);imageClick = 1;
                break;
            case R.id.iv_add_photo_02:// 添加身份证正反面照片
                showPhotoPicker(true);imageClick = 2;
                break;
            case R.id.iv_user_icon:// 添加用户头像，只跳转至拍照
                showPhotoPicker(false);imageClick = -1;
                break;
            case R.id.tv_software_protocol:// 软件协议
//                agreement_dialog = UIUtils.showSoftProtocolDialog(this);
                AlertDialog.Builder agreement_builder = new AlertDialog.Builder(this);
                View agreement_view = View.inflate(this, R.layout.software_agreement_dialog, null);
                agreement_builder.setView(agreement_view);
                agreement_dialog = agreement_builder.create();
                agreement_dialog.setCancelable(true);
                Button btn_ok = (Button) agreement_view.findViewById(R.id.btn_ok);
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        agreement_dialog.dismiss();
                        cb_agree_disagree.setChecked(true);
                    }
                });
                agreement_dialog.show();

                break;
            case R.id.bt_register:// 注册
                if (SystemUtil.isNetWorkUnAvailable())
                    return;// 网络不可用
                getInputText();
                if (mPresenter.checkInput(newBitmap3, newBitmap1, newBitmap2, name,
                        id_num, phone_num, relationship_with_prisoner,
                        prisoner_number, prison_choose, identifying_code)){
                    // 注册
                    confirmDialog = UIUtils.showConfirmDialog(this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPresenter.checkVerifyCode(phone_num, identifying_code);
                            dialog.dismiss();
                        }
                    });
                }
                break;
            case R.id.rl_back:// toolbar的返回按钮
                setBackPressed();
                break;
        }
    }

    /**
     * 设置返回按钮操作
     */
    private void setBackPressed(){
        if (agreement_dialog != null && agreement_dialog.isShowing()) {
            agreement_dialog.dismiss();
            return;
        }
        if (listDialig != null && listDialig.isShowing()){
            listDialig.dismiss();
            return;
        }
        if (confirmDialog != null && confirmDialog.isShowing()){
            confirmDialog.dismiss();
            return;
        }
        if (getInputTextAndCheckNull()){
            finish();
        }else {
            reminderDialog = UIUtils.showAlertDialog(this, getString(R.string.giveup_make_content),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RegisterActivity.this.finish();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        setBackPressed();
    }

    /**
     * 获取输入框文本并赋值到相关变量上
     * @return true为全都是空  可以直接关闭页面
     *          false为已经编辑  需弹出对话框提示是否放弃编辑
     */
    private boolean getInputTextAndCheckNull() {
        getInputText();
        return (TextUtils.isEmpty(name) && TextUtils.isEmpty(id_num) && TextUtils.isEmpty(phone_num)
                 && TextUtils.isEmpty(relationship_with_prisoner) && TextUtils.isEmpty(prisoner_number)
                && TextUtils.isEmpty(prison_choose) && TextUtils.isEmpty(identifying_code)
                && newBitmap1 == null && newBitmap2 == null && newBitmap3 == null);
    }

    /**
     * 获取文本
     */
    private void getInputText() {
        name = et_name.getText().toString().trim();
        id_num = et_ic_card.getText().toString().trim().toLowerCase();
        phone_num = et_phone_num.getText().toString().trim();
        relationship_with_prisoner = et_relationship_with_prisoner.getText().toString().trim();
        prisoner_number = et_prisoner_num.getText().toString().trim();
        prison_choose = actv_prison_choose.getText().toString().trim();
        identifying_code = et_identifying_code.getText().toString().trim();
    }

    @Override
    protected void initInjector() {
        DaggerRegisterComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .build()
                .inject(this);
    }

    @Override
    protected boolean isApplyStatusBarColor() {
        return true;
    }

    @Override
    protected boolean isApplyTranslucentStatus() {
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        if (isRunning) // 移除倒计时任务
            removeCountDown();
        releaseOrCloseDialog();// 释放资源  或者关闭对话框
        super.onDestroy();
        mPresenter.detachView();
    }

    /**
     * 释放资源  或者关闭对话框
     */
    private void releaseOrCloseDialog() {
        // 位图资源释放
        if (newBitmap1 != null)
            newBitmap1 = null;
        if (newBitmap2 != null)
            newBitmap2 = null;
        if (newBitmap3 != null)
            newBitmap3 = null;
        // 对话框关闭及释放
        if (agreement_dialog != null) {
            if (agreement_dialog.isShowing())
                agreement_dialog.dismiss();
            agreement_dialog = null;
        }
        if (reminderDialog != null) {
            if (reminderDialog.isShowing())
                reminderDialog.dismiss();
            reminderDialog = null;
        }
        if (listDialig != null){
            if (listDialig.isShowing())
                listDialig.dismiss();
            listDialig = null;
        }
        if (confirmDialog != null){
            if (confirmDialog.isShowing())
                confirmDialog.dismiss();
            confirmDialog = null;
        }
    }

    private boolean isRunning = false;
    /**
     * 验证码发送倒计时任务
     */
    private Runnable count_down_task = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void run() {
            isRunning = true;
            String text = countdown + " 秒";
            bt_send_identifying_code.setText(text);
            countdown--;
            if (countdown == 0) {
                removeCodeTask();
            } else {
                handler.postDelayed(count_down_task, 1000);
            }
        }
    };

    /**
     * 移除倒计时任务
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void removeCodeTask() {
        if (isRunning) {
            handler.removeCallbacks(count_down_task);
            bt_send_identifying_code.setEnabled(true);
            bt_send_identifying_code.setBackground(getResources().getDrawable(R.drawable.theme_bg_bt_selector));
            bt_send_identifying_code.setTextColor(getResources().getColor(R.color.white));
            bt_send_identifying_code.setText(getString(R.string.send_verify_code));
            countdown = 60;
            isRunning = false;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        bt_register.setEnabled(isChecked);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        sex = checkedId == rb_male.getId() ? "男" : "女";
    }

    /**
     * 显示相片操作(0 拍照 / 1 选择相片)
     *
     * @param isTwo
     */
    private void showPhotoPicker(boolean isTwo) {
        if (isTwo) {
            listDialig = UIUtils.showListDialog(this, getString(R.string.upload_id_photo), new String[]{getString(R.string.take_photo)
                    , getString(R.string.choose_from_album), getString(R.string.cancel)}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        takePhotoFromCamera(false);// 拍照
                    } else if (which == 1) {
                        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PHOTO);
                    } else {
                        listDialig.dismiss();
                    }
                }
            });
        }else {
            listDialig = UIUtils.showListDialog(this, getString(R.string.upload_avatar), new String[]{getString(R.string.take_photo),
                    getString(R.string.cancel)}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        takePhotoFromCamera(true);// 拍照
                    } else {
                        listDialig.dismiss();
                    }
                }
            });
        }
    }

    /**
     * 相机拍照
     */
    private void takePhotoFromCamera(boolean front) {
        Intent openCameraIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        mImageUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), "image.jpg"));
        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        try {
            if (front) {
                //打开前置摄像头
//                openCameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivityForResult(openCameraIntent, TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO:
                    // 将处理过的图片显示在界面上，并保存到本地
                    if (imageClick == 1) {
//                        setIDPhoto(1, true); // 设置第一张身份证照片
                        Log.i(TAG, "uploadFile1 : " + uploadFile1);
                        cropImage(mImageUri, CROP_SMALL_ID1);
                    } else if (imageClick == 2) {
//                        setIDPhoto(2, true); // 设置第二张身份证照片
                        cropImage(mImageUri, CROP_SMALL_ID2);
                        Log.i(TAG, "uploadFile2 : " + uploadFile2);
                    } else if (imageClick == -1) {
                        cropImageUri(mImageUri, 300, 300, CROP_SMALL_PICTURE);// 裁剪
//                        cropImage(imageUri, CROP_SMALL_PICTURE);
                    }
                    break;
                case CHOOSE_PHOTO:
                    ContentResolver resolver = getContentResolver();
                    // 照片的原始资源地址
                    Uri originalUri = data.getData();
                    Log.i(TAG, "originalUri : " + originalUri.getPath());
                    try {// 使用ContentProvider通过URI获取原始图片
                        photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        if (photo != null) {
                            Log.i(TAG, "photo not null " + imageClick);
                            if (imageClick == 1) {
                                cropImage(originalUri, CROP_SMALL_ID1);
//                                setIDPhoto(1, false);
                            }else if (imageClick == 2){
                                cropImage(originalUri, CROP_SMALL_ID2);
//                                setIDPhoto(2, false);
                            }
                        }
                    } catch (FileNotFoundException e) {
                        showToast(getString(R.string.file_not_exist));
                    } catch (IOException e) {
                        showToast(getString(R.string.read_file_error));
                    }
                    break;
                case CROP_SMALL_PICTURE:
                    // 将保存在本地的图片取出并缩小后显示在界面上
                    newBitmap3 = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/image.jpg");
                    iv_user_icon.setImageBitmap(newBitmap3);
                    break;
                case CROP_SMALL_ID1:
                    // 将保存在本地的图片取出并缩小后显示在界面上
                    newBitmap1 = BitmapFactory.decodeFile(this.getCacheDir()+ "/image.jpg");
                    iv_add_photo_01.setImageBitmap(newBitmap1);
                    break;
                case CROP_SMALL_ID2:
                    // 将保存在本地的图片取出并缩小后显示在界面上
                    newBitmap2 = BitmapFactory.decodeFile(this.getCacheDir()+ "/image.jpg");
                    iv_add_photo_02.setImageBitmap(newBitmap2);
                    break;
            }
        }
    }

    /**
     * 设置第一张身份证照片
     * @param position  1表示第一张   2表示第二张
     * @param isSave  是否将照片保存至本地
     */
    private void setIDPhoto(int position, boolean isSave) {
        // 将保存在本地的图片取出并缩小后显示在界面上
        Bitmap bitmap;
        if (isSave) {
            bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/image.jpg");
            ImageTools.savePhotoToSDCard(newBitmap1, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .getAbsolutePath() + "/Camera", String.valueOf(System.currentTimeMillis()));
            if (position == 1) {
                uploadFile1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                        .getAbsolutePath() + "/Camera/" + String.valueOf(System.currentTimeMillis()) + ".png";
            }else {
                uploadFile2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                        .getAbsolutePath() + "/Camera/" + String.valueOf(System.currentTimeMillis()) + ".png";
            }
        }else {
            bitmap = photo;
            if (position == 1) {
                uploadFile1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                        .getAbsolutePath() + "/Camera/" + "emptyphoto.png";
            }else {
                uploadFile2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                        .getAbsolutePath() + "/Camera/" + "emptyphoto.png";
            }
        }
        if (position == 1) {
            newBitmap1 = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
            iv_add_photo_01.setImageBitmap(newBitmap1);
        }else {
            newBitmap2 = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
            iv_add_photo_02.setImageBitmap(newBitmap2);
        }
        // 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
        bitmap.recycle();
    }

    /**
     * 裁剪照片
     *
     * @param uri         image uri
     * @param outputX     default image width
     * @param outputY     default image height
     * @param requestCode
     */
    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, requestCode);
    }

    private void cropImage(Uri uri, int requestCode) {
        Intent intent = new Intent(this, ImageCropActivity.class);
        intent.putExtra(Constants.INTENT_CROP_IMAGE_URI, uri);
        startActivityForResult(intent, requestCode);
    }
}
