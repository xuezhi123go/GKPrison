package com.gkzxhn.gkprison.utils.NomalUtils;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

/**
 * Created by 方 on 2017/4/11.
 */

public class DownloadManager {
    private static final String TAG = "DownLoadManager";

    private static String APK_CONTENTTYPE = "application/octet-stream";
//    private static String APK_CONTENTTYPE = "application/vnd.android.package-archive";

    private static String PNG_CONTENTTYPE = "image/png";

    private static String JPG_CONTENTTYPE = "image/jpg";

    private static String fileSuffix="";

    private static ProgressListener progressListener;

    public static void setProgressListener(ProgressListener progressListener) {
        DownloadManager.progressListener = progressListener;
    }

    public static boolean  writeResponseBodyToDisk(Context context, ResponseBody body, ProgressListener progressListener) {
        DownloadManager.progressListener = progressListener;

        Log.d(TAG, "contentType:>>>>"+ body.contentType().toString());

        String type = body.contentType().toString();

        String path = "";

        if (type.equals(APK_CONTENTTYPE)) {
            fileSuffix = ".apk";
            path = context.getExternalFilesDir(null) + File.separator + "yuwutong" + fileSuffix;
        } else if (type.equals(PNG_CONTENTTYPE)) {
            fileSuffix = ".png";
            path = context.getExternalFilesDir(null) + File.separator + System.currentTimeMillis() + fileSuffix;
        }

        // 其他同上 自己判断加入



        Log.d(TAG, "path:>>>>"+ path);

        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(path);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                final long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                Activity activity = context instanceof Activity ? ((Activity) context) : null;
                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                    final long fileSizeTemp = fileSizeDownloaded;
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (DownloadManager.progressListener != null) {
                                    DownloadManager.progressListener.upDateProgress(fileSizeTemp, fileSize);
                                }
                            }
                        });
                    }
                }

                outputStream.flush();


                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public interface ProgressListener{
        public void upDateProgress(long fileSizeDownloaded, long fileSize);
    };
}
