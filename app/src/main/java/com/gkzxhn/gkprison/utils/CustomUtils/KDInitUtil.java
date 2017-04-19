package com.gkzxhn.gkprison.utils.CustomUtils;

import android.os.Environment;
import android.util.Log;

import com.gkzxhn.gkprison.base.MyApplication;
import com.google.gson.Gson;
import com.keda.callback.MyMtcCallback;
import com.keda.sky.app.TruetouchGlobal;
import com.kedacom.kdv.mt.api.Base;
import com.kedacom.kdv.mt.api.Configure;
import com.kedacom.kdv.mt.bean.TMtH323PxyCfg;
import com.kedacom.kdv.mt.bean.TagTNetUsedInfoApi;
import com.kedacom.kdv.mt.constant.EmMtModel;
import com.kedacom.kdv.mt.constant.EmNetAdapterWorkType;
import com.kedacom.truetouch.audio.AudioDeviceAndroid;
import com.pc.utils.FormatTransfer;
import com.pc.utils.NetWorkUtils;
import com.pc.utils.VConfStaticPic;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Author: Huang ZN
 * Date: 2016/12/20
 * Email:943852572@qq.com
 * Description:科达视频SDK相关操作
 */

public class KDInitUtil {

    private static final String TAG = KDInitUtil.class.getName();
    public static boolean isH323;// h323代理
    
    public static void init(){
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                Base.mtStart(EmMtModel.emSkyAndroidPhone, TruetouchGlobal.MTINFO_SKYWALKER, "5.0", getMediaLibDir()
                        + File.separator, MyMtcCallback.getInstance(), "kedacom"); // 启动业务终端，开始接受回调
                parseH323();
                // 设音视频上下文置
                AudioDeviceAndroid.initialize(MyApplication.getContext());
                setUserdNetInfo();
                // 启动Service
                Base.initService();
                VConfStaticPic.checkStaticPic(MyApplication.getContext(), getTempDir() + File.separator);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.i(TAG, "kd init complete");
                    }
                });
    }

    private static void parseH323() {
        // 从数据库获取当前 是否注册了代理
        StringBuffer H323PxyStringBuf = new StringBuffer();
        Configure.getH323PxyCfg(H323PxyStringBuf);
        String h323Pxy = H323PxyStringBuf.toString();
        TMtH323PxyCfg tmtH323Pxy = new Gson().fromJson(h323Pxy, TMtH323PxyCfg.class);
        // { "achNumber" : "", "achPassword" : "", "bEnable" : true, "dwSrvIp" : 1917977712, "dwSrvPort" : 2776 }
        if (null != tmtH323Pxy) {
            isH323 = tmtH323Pxy.bEnable;
//            isH323 = true;
        }
    }

    private static String getMediaLibDir() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "kedacom/sky_Demo/mediaLib" + File.separator);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath() + File.separator;
    }

    private static String getTempDir() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "kedacom/sky_Demo/mediaLib/temp" + File.separator);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    /**
     * getTmpDir
     * @return
     */
    public static String getTmpDir() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "kedacom/sky_Demo/.tmp" + File.separator);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    // 保存截图的路径(绝对路径)
    public static String getPictureDir() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "kedacom/sky_Demo/.picture" + File.separator);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    // 图片保存文件夹绝对路径
    public static String getSaveDir() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "kedacom/sky_Demo/save" + File.separator);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    /**
     * 设置正在使用的网络信息
     */
    private static void setUserdNetInfo() {
        String ip = NetWorkUtils.getIpAddr(MyApplication.getContext(), true);

        TagTNetUsedInfoApi netInfo = new TagTNetUsedInfoApi();
        netInfo.emUsedType = EmNetAdapterWorkType.emNetAdapterWorkType_Wifi_Api;
        // netInfo.dwIp = NetWorkUtils.getFirstWiFiIpAddres(TruetouchApplication.getContext());
        try {
            netInfo.dwIp = FormatTransfer.lBytesToLong(InetAddress.getByName(ip).getAddress());
        } catch (Exception e) {
            netInfo.dwIp = FormatTransfer.reverseInt((int) NetWorkUtils.ip2int(ip));
        }
        if (NetWorkUtils.isMobile(MyApplication.getContext())) {
            netInfo.emUsedType = EmNetAdapterWorkType.emNetAdapterWorkType_MobileData_Api;
        }
        String dns = NetWorkUtils.getDns(MyApplication.getContext());
        try {
            if (!com.pc.utils.StringUtils.isNull(dns)) {
                netInfo.dwDns = FormatTransfer.lBytesToLong(InetAddress.getByName(dns).getAddress());
            } else {
                netInfo.dwDns = 0;
            }
        } catch (UnknownHostException e) {
            android.util.Log.e("Test", "dwDns: " + dns + "--" + netInfo.dwDns);
        }

        android.util.Log.e("Test", "ip: " + ip + "--" + netInfo.dwIp);

        Configure.sendUsedNetInfoNtf(netInfo);
    }
}
