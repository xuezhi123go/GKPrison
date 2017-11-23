package com.gkzxhn.gkprison.utils.CustomUtils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.model.dao.GreenDaoHelper;
import com.gkzxhn.gkprison.model.dao.bean.Sysmsg;
import com.gkzxhn.gkprison.model.dao.bean.SysmsgDao;
import com.gkzxhn.gkprison.model.net.api.ApiRequest;
import com.gkzxhn.gkprison.model.net.bean.SystemMessage;
import com.gkzxhn.gkprison.ui.activity.MainActivity;
import com.gkzxhn.gkprison.ui.activity.normal_activity.SystemMessageActivity;
import com.gkzxhn.gkprison.utils.NomalUtils.DensityUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.StringUtils;
import com.gkzxhn.gkprison.utils.NomalUtils.SystemUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.gkzxhn.gkprison.widget.receiver.AlarmReceiver;
import com.gkzxhn.gkprison.zijing.ZjVideoActivity;
import com.google.gson.Gson;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.zjrtc.ZjVideoManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author: Huang ZN
 * Date: 2016/12/20
 * Email:943852572@qq.com
 * Description:云信sdk相关
 *              sdk初始化、
 *              UI初始化、
 *              监听云信系统通知及后续操作
 */

public class NimInitUtil {

    private static final String TAG = NimInitUtil.class.getName();
    private static String mSessionId;
    private static SessionTypeEnum mSessionType;

    /**
     * 初始化云信sdk相关
     */
    public static void initNim(){
        NIMClient.init(MyApplication.getContext(), loginInfo(), options()); // 初始化
        if (inMainProcess()) {
            observeCustomNotification();
            NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(
                    getObserver(), true);
        }
    }

    /**
     * 观察者
     * @return
     */
    @NonNull
    private static Observer<StatusCode> getObserver() {
        return new Observer<StatusCode>() {
            public void onEvent(StatusCode status) {
                Log.i("tag", "User status changed to: " + status);
                switch (status) {
                    case KICKOUT:
                        toMain();
                        break;
                    case NET_BROKEN:
                        ToastUtil.showShortToast(MyApplication.getContext()
                                .getString(R.string.net_broken));
                        break;
                }
            }
        };
    }

    public static void checkStatus(Context context){
        StatusCode status = NIMClient.getStatus();
        Log.i(TAG, "checkStatus: " + status.getValue() + status.name());
        switch (status) {
            case UNLOGIN:
//                ToastUtil.showShortToast(context.getString(R.string.relogin));
                String token = (String) SPUtil.get(context, SPKeyConstants.ACCESS_TOKEN, "");
                LoginInfo info = new LoginInfo(token, token); // config...
                NIMClient.getService(AuthService.class).login(info)
                        .setCallback(null);
                break;
            case NET_BROKEN:
                ToastUtil.showShortToast(context.getString(R.string.net_broken));
                break;
            default:
                break;
        }
    }

    /**
     * 被踢下线进入主页
     */
    private static void toMain() {
        boolean isCommoner = (boolean) SPUtil.get(MyApplication.getContext(),
                SPKeyConstants.IS_COMMON_USER, true);
        Intent intent = new Intent(MyApplication.getContext(), /*isCommoner ?*/
                MainActivity.class /*: DateMeetingListActivity.class*/);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getContext().startActivity(intent);
    }

    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    private static LoginInfo loginInfo() {
        return getLoginInfo();
    }

    /**
     * // 从本地读取上次登录成功时保存的用户登录信息
     * @return
     */
    private static LoginInfo getLoginInfo() {
        String token = (String) SPUtil.get(MyApplication.getContext(), SPKeyConstants.ACCESS_TOKEN, "");
        String password = (String) SPUtil.get(MyApplication.getContext(), SPKeyConstants.PASSWORD, "");
        boolean isCommonUser = (boolean)SPUtil.get(MyApplication.getContext(), SPKeyConstants.IS_COMMON_USER, true);
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(token)) {
            return new LoginInfo(token, isCommonUser ? token : password);
        } else {
            return null;
        }
    }

    /**
     * 主进程
     * @return
     */
    private static boolean inMainProcess() {
        String packageName = MyApplication.getContext().getPackageName();
        String processName = SystemUtil.getProcessName(MyApplication.getContext());
        return packageName.equals(processName);
    }

    // 如果返回值为 null，则全部使用默认参数。
    private static SDKOptions options() {
        SDKOptions options = new SDKOptions();

        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        config.notificationEntrance = MainActivity.class; // 点击通知栏跳转到该Activity
        config.notificationSmallIconId = R.mipmap.ic_launcher;
        options.statusBarNotificationConfig = config;

        // 配置保存图片，文件，log 等数据的目录
        // 如果 options 中没有设置这个值，SDK 会使用下面代码示例中的位置作为 SDK 的数据目录。
        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
        options.sdkStorageRootPath = Environment.getExternalStorageDirectory() + "/" + MyApplication.getContext().getPackageName() + "/nim";

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小，该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
        options.thumbnailSize = DensityUtil.getScreenWidthHeight(MyApplication.getContext())[0] / 2;

        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
        options.userInfoProvider = new UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String account) {
                return null;
            }

            @Override
            public int getDefaultIconResId() {
                return R.drawable.avatar_def;
            }

            @Override
            public Bitmap getTeamIcon(String tid) {
                return null;
            }

            @Override
            public Bitmap getAvatarForMessageNotifier(String account) {
                return null;
            }

            @Override
            public String getDisplayNameForMessageNotifier(String account, String sessionId,
                                                           SessionTypeEnum sessionType) {
                return null;
            }
        };
        return options;
    }


    /**
     * 监听系统通知
     */
    private static void observeCustomNotification() {
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(new Observer<CustomNotification>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onEvent(CustomNotification customNotification) {
                Log.i(TAG, "custom notification ApnsText : " + customNotification.getApnsText());
                String content = customNotification.getContent();
                Log.i(TAG, "custom notification Content : " + content);
                Log.i(TAG, "custom notification FromAccount : " + customNotification.getFromAccount());
                Log.i(TAG, "custom notification SessionId : " + customNotification.getSessionId());
                Log.i(TAG, "custom notification Time : " + customNotification.getTime());
                Log.i(TAG, "custom notification SessionType : " + customNotification.getSessionType());
                // 第三方 APP 在此处理自定义通知：存储，处理，展示给用户等
                Log.i(TAG, "receive custom notification: " + content
                        + " from :" + customNotification.getSessionId() + "/" + customNotification.getSessionType());
                Gson gson = new Gson();
                SystemMessage systemMessage = new SystemMessage();
                try {
                    systemMessage = gson.fromJson(content, SystemMessage.class);
                }catch (Exception e) {
                    android.util.Log.i(TAG, "saveToDataBase: --- " +e.getMessage());
                }
                if (systemMessage.code == -1) {
                    //code=-1 表示接收的是视频通话消息 不显示到通知栏,不注册到数据库
                    mSessionId = customNotification.getSessionId();
                    mSessionType = customNotification.getSessionType();

                    sendNotificationToPrison(-1);
                    String msg = systemMessage.msg;
                    if (!TextUtils.isEmpty(msg)) {
                        String[] strings = msg.split("##");
                        //strings 账号##主持人密码##参会密码
                        if (strings.length == 3) {
                            callRoom(strings[0], Constants.ZIJING_DOMAIN, strings[2]);
                        }
                    }
                    return;
                }else if (systemMessage.code == -2) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.ZIJING_ACTION);
                    MyApplication.getContext().sendBroadcast(intent);
                }
                sendNotification(MyApplication.getContext(), content,
                            customNotification.getSessionId());

                /*if(customNotification.getContent().contains("type_id")) {
                    SPUtil.put(MyApplication.getContext(), "has_new_notification", true);
                    sendNotification(MyApplication.getContext(), customNotification.getContent(),
                }else if(customNotification.getContent().contains("审核")){
                            customNotification.getSessionId());
                    doExamineResult(customNotification.getContent());
                }else {
                    Log.e(TAG, "sorry,other type notification");
                }*/
            }
        }, true);
    }

    /**
     * 呼叫会议室
     * @param address
     * @param domain
     * @param password
     */
    private static void callRoom(String address, String domain, String password) {
//设置服务器地址、显示名称、呼叫地址、呼叫密码；
        ZjVideoManager manager = ZjVideoManager.getInstance();
        manager.setDomain(domain);
        manager.setDisplayName("guoke001");
        manager.setBandwidth((int) SPUtil.get(MyApplication.getContext(), Constants.RATE, 384));
        manager.setAddress(address);
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getContext().startActivity(intent);
    }

    /**
     * 注册后发送云信消息到监狱端
     * @param code -1表示连接成功 -2表示挂断
     */
    public static void sendNotificationToPrison(int code) {
        if (mSessionId == null || mSessionType == null) {
            return;
        }
        android.util.Log.i(TAG, "sendNotificationToPrison: sesionId=== " + mSessionId);
        //发送云信消息，告诉监狱端现在的GK状态;
        CustomNotification notification = new CustomNotification();
        notification.setSessionId(mSessionId);
        notification.setSessionType(mSessionType);
        // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
        // 这里以类型 “1” 作为“正在输入”的状态通知。
        JSONObject json = new JSONObject();
        try {
            json.put("code", code); //挂断发送-2
        } catch (JSONException e) {
            e.printStackTrace();
        }
        notification.setContent(json.toString());
        android.util.Log.i(TAG, "sendNotificationToPrison: json=== " + json.toString());
        // 发送自定义通知
        NIMClient.getService(MsgService.class).sendCustomNotification(notification);
    }

    /**
     * 发送通知
     * @param content
     * @param formId
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void sendNotification(Context context, String content, String formId){
        saveToDataBase(context, content);// 系统通知保存至数据库
        if((boolean)SPUtil.get(MyApplication.getContext(), SPKeyConstants.IS_MSG_REMIND, true)) {
            setRemindAlarm(context, content);
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, SystemMessageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("您有新的消息，点击查看")
                .setContentTitle("狱务通提醒")
                .setContentText("您有来自" + SPUtil.get(MyApplication.getContext(), "jail", "德山监狱") +"新的消息，点击查看")
                .setContentIntent(pendingIntent).setNumber(1).build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_SOUND;
        manager.notify(1, notification);
    }

    /**
     * 设置闹钟
     * @param context
     * @param content
     */
    public static void setRemindAlarm(Context context, String content) {
        String meeting_date = "";
        long alarm_time = 0;
        try {
            JSONObject jsonObject = new JSONObject(content);
            meeting_date = jsonObject.getString("meeting_time");
            String meeting_time = meeting_date.substring(0, meeting_date.lastIndexOf("-"));
            Log.i("meeting_time", meeting_time);
            String start_time = meeting_time.trim();
            Log.i("start_time", start_time);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            long pre_alarm_time = format.parse(start_time).getTime();
            alarm_time = pre_alarm_time - 300000;
            Log.i("alarm_time", alarm_time + "---" + StringUtils.formatTime(alarm_time, "yyyy-MM-dd HH:mm:ss"));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            String time = StringUtils.formatTime(System.currentTimeMillis(), "HH:mm:ss");
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.split(":")[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(time.split(":")[1]));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(
                    context, 0, intent, 0);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, alarm_time, sender);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存至数据库
     * @param content
     */
    private static void saveToDataBase(Context context, String content) {
        getMeetingInfo();
        Gson gson = new Gson();
        SystemMessage systemMessage = new SystemMessage();
        try {
            systemMessage = gson.fromJson(content, SystemMessage.class);
        }catch (Exception e) {
            android.util.Log.i(TAG, "saveToDataBase: --- " +e.getMessage());
        }
        //greendao保存数据库
        SysmsgDao sysmsgDao = GreenDaoHelper.getDaoSession().getSysmsgDao();
        Sysmsg sysmsg = new Sysmsg();
        sysmsg.setName(systemMessage.jail);
        sysmsg.setMeeting_date(systemMessage.meeting_time);
        sysmsg.setReason(systemMessage.msg);
        sysmsg.setUser_id((String)SPUtil.get(MyApplication.getContext(), "username", ""));
        String msg_reveice_time = StringUtils.formatTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
        sysmsg.setReceive_time(msg_reveice_time);
        sysmsgDao.insert(sysmsg);

        // 保存至数据库
        /*SQLiteDatabase db = StringUtils.getSQLiteDB(context);
        ContentValues values = new ContentValues();
//        values.put("apply_date", systemMessage.getApply_date());
//        values.put("type_id", systemMessage.getType_id());
        values.put("name", systemMessage.jail);
//        values.put("is_read", systemMessage.is_read());
//        values.put("result", systemMessage.getResult());
        values.put("meeting_date", systemMessage.meeting_time);
//        if (!TextUtils.isEmpty(systemMessage.meeting_time)) {
//            SPUtil.put(MyApplication.getContext(), SPKeyConstants.NEARLEST_MEET_TIME, systemMessage.meeting_time);
//        }
        values.put("reason", systemMessage.msg);
        values.put("user_id", (String) SPUtil.get(MyApplication.getContext(), "username", ""));
        String msg_reveice_time = StringUtils.formatTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
        values.put("receive_time", msg_reveice_time);
        db.insert("sysmsg", null, values);
        db.close();*/
    }

    /**
     * 获取会见列表信息
     */
    private static void getMeetingInfo() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiRequest api = retrofit.create(ApiRequest.class);
        String token = (String) SPUtil.get(MyApplication.getContext(), SPKeyConstants.ACCESS_TOKEN, "");
        int family_id = (int) SPUtil.get(MyApplication.getContext(), SPKeyConstants.FAMILY_ID, -1);
        Map<String, String> header = new HashMap<>();
        header.put("authorization", token);
        api.getMeetings(header,family_id)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<ResponseBody>(){
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        android.util.Log.i(TAG, "onError: getMeetingInfo--- " + e.getMessage());
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String meetingTimeInfo = responseBody.string();
                            SPUtil.put(MyApplication.getContext(), SPKeyConstants.MEETINGS_TIME, meetingTimeInfo);
                            android.util.Log.i(TAG, "onNext: meetings_time_info == " + meetingTimeInfo);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        super.onNext(responseBody);
                    }
                });
    }
}
