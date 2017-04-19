package com.gkzxhn.gkprison.utils.NomalUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 加密工具类
 */
public class MD5Utils {
	/**
	 * md5加密方法
	 * @param password  明文
	 * @return	密文
	 */
	public static String ecoder(String password) {
		StringBuffer buffer;
		try {
			// MD5加密
			// 1.信息摘要器
			MessageDigest digest = MessageDigest.getInstance("md5");
			// 2.变成byte数组
			byte[] bytes = digest.digest(password.getBytes());
			buffer = new StringBuffer();
			// 3.每一个byte和8个二进制位做一个与运算
			for (byte b : bytes) {
				int number = b & 0xff;
				// 4.把int类型转换为16进制
				String numberstr = Integer.toHexString(number);
				// 5.不足8位的补全
				if (numberstr.length() == 1) {
					buffer.append("0");
				}
				buffer.append(numberstr);

			}
			// System.out.println(buffer.toString());
			return buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}
}
