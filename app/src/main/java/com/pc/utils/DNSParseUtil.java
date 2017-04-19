package com.pc.utils;

import android.util.Log;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.Map;

/**
 * DNS解析
 * 
 * @author chenj
 * @date 2014-7-30
 */
public class DNSParseUtil {

	/**
	 * 域名解析
	 *
	 * @param hostName 主机名
	 * @return
	 */
	public static String dnsParse(String hostName) {
		return dnsParse(true, hostName);
	}

	/**
	 * 域名解析
	 *
	 * @param dnsNeedClear 清理缓存
	 * @param hostName 主机名
	 * @return
	 */
	public static String dnsParse(boolean dnsNeedClear, String hostName) {
		String ip_devdiv = "";
		if (StringUtils.isNull(hostName)) {
			DNSParseUtil.dnsClear();
			return ip_devdiv;
		}

		if (ValidateUtils.isIP(hostName)) {
			ip_devdiv = hostName;

			return ip_devdiv;
		}

		// if (ValidateUtils.isNumber(hostName)) {
		// return ip_devdiv;
		// }

		try {
			if (dnsNeedClear) {
				dnsClear();
			}

			InetAddress[] x = InetAddress.getAllByName(hostName);
			if (x != null && x.length > 0) {
				ip_devdiv = x[0].getHostAddress();
				Log.v("DNS", "hostname's ip is: " + ip_devdiv);
				return ip_devdiv;
				// 下面的判断ip地址是否可达的方法在网络上不可靠 ，暂时去掉做一下测试
				// for (int i = 0; i < x.length; i++) {
				// if (x[i].isReachable(3000)) {//该地址可达
				// Log.v("DNS", "isReachable ip" + i + "is:" +
				// x[i].getHostAddress());
				// ip_devdiv = x[i].getHostAddress();
				// return ip_devdiv;
				// } else {
				// Log.v("DNS", "unReachable ip" + i + "is:" +
				// x[i].getHostAddress());
				// }
				// }
			}
		} catch (Exception e1) {
			Log.e("DNS", "ip_devdiv is :" + ip_devdiv, e1);
		}

		Log.v("DNS", "hostname's ip is: " + ip_devdiv);

		return ip_devdiv;
	}

	/**
	 * 清理手机DNS解析缓存
	 */
	@SuppressWarnings("rawtypes")
	public static void dnsClear() {
		try {
			Class inetAddressClass = InetAddress.class;
			Field cacheField = inetAddressClass.getDeclaredField("addressCache");
			cacheField.setAccessible(true);
			Object obj;
			obj = (Object) cacheField.get(inetAddressClass);
			Class cacheClazz = obj.getClass();
			// String s=android.os.Build.VERSION.SDK;
			int sdkInt = android.os.Build.VERSION.SDK_INT;
			// String ass=android.os.Build.VERSION.RELEASE;
			// 目前测试的三星4.0.3是cache static memeber
			// it's type is libcore.util.BasicLruCache
			// and the sdk_int is 15
			// so till now use sdk_int>15 to judge whether is cache or map
			if (sdkInt >= 15) {
				final Field cache = cacheClazz.getDeclaredField("cache");
				cache.setAccessible(true);
				Object obj2 = (Object) cache.get(obj);
				// final Map cacheMap = (Map)cache.get(obj);
				Class cacheClass = obj2.getClass();
				// java.util.LinkedHashMap
				final Field cacheMapField = cacheClass.getDeclaredField("map");
				cacheMapField.setAccessible(true);
				final Map cacheMap = (Map) cacheMapField.get(obj2);
				cacheMap.clear();
			} else {
				final Field cacheMapField = cacheClazz.getDeclaredField("map");
				cacheMapField.setAccessible(true);
				final Map cacheMap = (Map) cacheMapField.get(obj);
				cacheMap.clear();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
