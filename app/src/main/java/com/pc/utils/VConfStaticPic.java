/**
 * @(#)VConfStaticPic.java 2014-2-18 Copyright 2014 it.kedacom.com, Inc. All
 *                         rights reserved.
 */

package com.pc.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


/**
 * @author chenjian
 * @date 2014-2-18
 */

public class VConfStaticPic {

	/**
	 * 视频会议静态图片
	 */
	public static void checkStaticPic(Context context, String mediaLibTempDir) {
		if (null == context || StringUtils.isNull(mediaLibTempDir)) return;

		final String staticpicName = "staticpic.bmp";

		File file = new File(mediaLibTempDir, staticpicName);
		if (file.exists() && file.length() > 0) {
			return;
		}

		InputStream inStream = null;
		FileOutputStream foutStream = null;
		try {
			AssetManager am = context.getAssets();
			if (null == am) {
				return;
			}

			inStream = am.open(staticpicName);
			foutStream = new FileOutputStream(file);

			int byteread = 0;
			byte[] buffer = new byte[1444];
			while ((byteread = inStream.read(buffer)) != -1) {
				foutStream.write(buffer, 0, byteread);
				foutStream.flush();
			}
		} catch (Exception e) {
			Log.e("Exception", "FileUtil checkStaticPic 1:", e);
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}

				if (foutStream != null) {
					foutStream.close();
				}
			} catch (Exception e2) {
				Log.e("Exception", "FileUtil checkStaticPic 2:", e2);
			}
		}

	}
}
