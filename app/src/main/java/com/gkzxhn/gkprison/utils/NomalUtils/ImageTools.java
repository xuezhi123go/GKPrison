package com.gkzxhn.gkprison.utils.NomalUtils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.gkzxhn.gkprison.model.net.bean.Uuid_images_attributes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 相片处理工具类(Tools for handler picture)
 *
 * @author Song
 */
public final class ImageTools {
    /**
     * Resize the bitmap
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * Check the SD card
     *
     * @return
     */
    public static boolean checkSDCardAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * Save image to the SD card
     *
     * @param photoBitmap
     * @param photoName
     * @param path
     */
    public static void savePhotoToSDCard(Bitmap photoBitmap, String path,
                                         String photoName) {
        if (checkSDCardAvailable()) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File photoFile = new File(path, photoName + ".png");
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100,
                            fileOutputStream)) {
                        fileOutputStream.flush();
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取压缩图片并转换base64
     * @param newBitmap1
     * @param newBitmap2
     * @param newBitmap3
     * @return
     */
    public static List<Uuid_images_attributes> getZipPicture(Bitmap newBitmap1, Bitmap newBitmap2, Bitmap newBitmap3) {
        List<Uuid_images_attributes> uuid_images = new ArrayList<>();
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        newBitmap1.compress(Bitmap.CompressFormat.JPEG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        String tu1 = Base64.encode(ba1);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        newBitmap2.compress(Bitmap.CompressFormat.JPEG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        String tu2 = Base64.encode(ba2);
        String[] tu = {tu1, tu2};
        uuid_images.clear();
        for (int i = 0; i < tu.length; i++) {
            Uuid_images_attributes uuid_images_attributes = new Uuid_images_attributes();
            uuid_images_attributes.setImage_data(tu[i]);
            uuid_images.add(uuid_images_attributes);
        }
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        newBitmap3.compress(Bitmap.CompressFormat.JPEG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        String tu3 = Base64.encode(ba3);
        Uuid_images_attributes user_icon = new Uuid_images_attributes();
        user_icon.setImage_data(tu3);
        uuid_images.add(user_icon);
        return uuid_images;
    }

    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, String filename, Bitmap mBitmap) {
        String savePath;
        File filePic;
        /*if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = Environment.getExternalStorageDirectory() + "/gkPrison";
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + "/gkPrison";
        }*/
        savePath = context.getCacheDir().getAbsolutePath();
        try {
            filePic = new File(savePath, filename);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }
}
