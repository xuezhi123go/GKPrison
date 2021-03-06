package com.gkzxhn.gkprison.constant;

import com.gkzxhn.gkprison.base.MyApplication;

import java.io.File;

/**
 * Created by zhengneng on 2016/1/7.
 * 常量类
 */
public class Constants {

    // 资源头url
    public static final String RESOURSE_HEAD = "https://www.fushuile.com"; //10.93.1.10:3000   www.fushuile.com

//    public static final String RESOURSE_HEAD = "http://10.10.10.109:3000"; //10.93.1.10:3000   www.fushuile.com
    // 头url
    public static final String URL_HEAD = "https://www.fushuile.com/api/v1/";
//    public static final String URL_HEAD = "http://10.10.10.109:3000/api/v1/";
    // 新版本apk地址
    public static final String NEW_VERSION_APK_URL = "https://www.fushuile.com/apps/"; // 后加apk文件名

    //头像全路径
    public static final String fileName = MyApplication.getContext().getExternalFilesDir(null) + File.separator + "avatar.png";
}

