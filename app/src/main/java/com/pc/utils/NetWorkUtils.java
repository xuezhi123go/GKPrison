package com.pc.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Net work util
 * 
 * @author chenj
 * @date 2013-6-6
 */
public class NetWorkUtils {

	/**
	 * 获取IP地址
	 * 
	 * @param context
	 * @param judeWifi			对Wifi地址进行校验
	 * @return IPV4 Address
	 */
	public static String getIpAddr(Context context, boolean judeWifi) {

		String vpnIp = getVPNIpAddress();
		if (!StringUtils.isNull(vpnIp)) {
			return vpnIp;
		}

		String ipaddr = NetWorkUtils.getLocalIpAddress();
		if (null == ipaddr) ipaddr = "";

		if (!judeWifi) {
			return ipaddr;
		}
		// 小米2S(MI 2S)通过NetWorkUtils.getLocalIpAddress()获取的IP始终为：10.0.2.15
		// 对应2S这种情况,可对WiFi单点进行判断
		if (context != null && (StringUtils.isNull(ipaddr) || "0".equals(ipaddr) || "0.0.0.0".equals(ipaddr) || judeWifi) && isWiFi(context)) {
			String wifiIpaddr = NetWorkUtils.getNormalWiFiIpAddres(context);
			Log.i("ip", "wifi地址  = " + wifiIpaddr);
			if (!StringUtils.isNull(wifiIpaddr) && !(wifiIpaddr.equals("0")) && !(wifiIpaddr.equals("0.0.0.0"))) {
				return wifiIpaddr;
			}
		}
		return ipaddr;
	}

	/**
	 * 标准VPN地址
	 * 
	 * @return IPV4 192.168.1.118
	 */
	public static String getVPNIpAddress() {
		NetworkInterface vpnNet = getVNPNet();
		if (null != vpnNet) {
			for (Enumeration<InetAddress> enumIpAddr = vpnNet.getInetAddresses(); enumIpAddr.hasMoreElements();) {
				InetAddress inetAddress = enumIpAddr.nextElement();
				if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
					Log.e("VPNTEST", "vpnNet 地址  = " + inetAddress.getHostAddress().toString());
					return inetAddress.getHostAddress().toString();
				}
			}
		}
		return null;
	}

	/**
	 * 标准IP地址
	 * 
	 * @return IPV4 192.168.1.118
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
						Log.e("VPNTEST", "Inet4Address地址  = " + inetAddress.getHostAddress().toString());
						return inetAddress.getHostAddress().toString();
					}
				}
			}

		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
			return "";
		}
		return "";
	}

	public static NetworkInterface getVNPNet() {
		try {
			Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
			if (niList != null) {
				for (NetworkInterface intf : Collections.list(niList)) {
					if (!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
						continue;
					}
					Log.d("VPNTEST", "isVpnUsed() NetworkInterface Name: " + intf.getName());
					if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())) {
						return intf; // The VPN is up
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * 物理地址
	 * 
	 * @param context
	 * @return WIFI MAC Address, eg: 88:32:9B:90:BB:32
	 */
	public static String getLocalMacAddres(Context context) {
		if (null == context) {
			return "";
		}

		try {
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			// WifiInfo info = wifi.getConnectionInfo();
			return wifi.getConnectionInfo().getMacAddress();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * WIFI第一个地址
	 * 
	 * <pre>
	 * 第一个IP地址，即本机IP
	 * </pre>
	 * 
	 * @param context
	 * @return WIFI First IP Address eg: 1979820224
	 */
	public static int getFirstWiFiIpAddres(Context context) {
		if (null == context) {
			return 0;
		}

		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();

		return info.getIpAddress();
	}

	/**
	 * Wifi IP地址
	 * 
	 * <pre>
	 * 第一个IP地址，即本机IP
	 * </pre>
	 * @param context
	 * @return WIFI Normal IP Address eg: 192.168.1.118
	 */
	public static String getNormalWiFiIpAddres(Context context) {
		if (null == context) {
			return "";
		}

		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		int inIP = info.getIpAddress();

		return (inIP & 0xFF) + "." + ((inIP >> 8) & 0xFF) + "." + ((inIP >> 16) & 0xFF) + "." + (inIP >> 24 & 0xFF);
	}

	/**
	 * 转换ip4格式地址
	 *
	 * @param intIp 整型格式地址
	 * @return
	 */
	public static String intToIp(int intIp) {
		return (intIp & 0xFF) + "." +

		((intIp >> 8) & 0xFF) + "." +

		((intIp >> 16) & 0xFF) + "." +

		(intIp >> 24 & 0xFF);
	}

	public static long ip2int(String ip) {
		String[] items = ip.split("\\.");
		return Long.valueOf(items[0]) << 24 | Long.valueOf(items[1]) << 16 | Long.valueOf(items[2]) << 8 | Long.valueOf(items[3]);
	}

	/** 
	* 将127.0.0.1形式的IP地址转换成十进制整数  
	* @param strIp 
	* @return 
	*/
	public static long ipToLong(String strIp) {
		long[] ip = new long[4];
		// 先找到IP地址字符串中.的位置
		int position1 = strIp.indexOf(".");
		int position2 = strIp.indexOf(".", position1 + 1);
		int position3 = strIp.indexOf(".", position2 + 1);
		// 将每个.之间的字符串转换成整型
		ip[0] = Long.parseLong(strIp.substring(0, position1));
		ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
		ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
		ip[3] = Long.parseLong(strIp.substring(position3 + 1));
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
	}

	/** 
	 * 将十进制整数  转换成127.0.0.1形式的IP地址
	 * @param strIp 
	 * @return 
	 */
	public static String LongToIp(long longIp) {

		StringBuffer sb = new StringBuffer("");
		// 将高24位置0
		sb.append(String.valueOf((longIp & 0x000000FF)));
		sb.append(".");
		// 将高16位置0，然后右移8位
		sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
		sb.append(".");
		// 直接右移24位
		sb.append(String.valueOf((longIp >>> 24)));

		return sb.toString();
	}

	/** 
	* ipv4地址转有符号byte[5] 
	*/
	private static byte[] ipv4ToBytes(String ipv4) {
		byte[] ret = new byte[5];
		ret[0] = 0;
		// 先找到IP地址字符串中.的位置
		int position1 = ipv4.indexOf(".");
		int position2 = ipv4.indexOf(".", position1 + 1);
		int position3 = ipv4.indexOf(".", position2 + 1);
		// 将每个.之间的字符串转换成整型
		ret[1] = (byte) Integer.parseInt(ipv4.substring(0, position1));
		ret[2] = (byte) Integer.parseInt(ipv4.substring(position1 + 1, position2));
		ret[3] = (byte) Integer.parseInt(ipv4.substring(position2 + 1, position3));
		ret[4] = (byte) Integer.parseInt(ipv4.substring(position3 + 1));
		return ret;
	}

	/** 
	 * ipv6地址转有符号byte[17] 
	 */
	private static byte[] ipv6ToBytes(String ipv6) {
		byte[] ret = new byte[17];
		ret[0] = 0;
		int ib = 16;
		boolean comFlag = false;// ipv4混合模式标记
		if (ipv6.startsWith(":")) // 去掉开头的冒号
			ipv6 = ipv6.substring(1);
		String groups[] = ipv6.split(":");
		for (int ig = groups.length - 1; ig > -1; ig--) {// 反向扫描
			if (groups[ig].contains(".")) {
				// 出现ipv4混合模式
				byte[] temp = ipv4ToBytes(groups[ig]);
				ret[ib--] = temp[4];
				ret[ib--] = temp[3];
				ret[ib--] = temp[2];
				ret[ib--] = temp[1];
				comFlag = true;
			} else if ("".equals(groups[ig])) {
				// 出现零长度压缩,计算缺少的组数
				int zlg = 9 - (groups.length + (comFlag ? 1 : 0));
				while (zlg-- > 0) {// 将这些组置0
					ret[ib--] = 0;
					ret[ib--] = 0;
				}
			} else {
				int temp = Integer.parseInt(groups[ig], 16);
				ret[ib--] = (byte) temp;
				ret[ib--] = (byte) (temp >> 8);
			}
		}
		return ret;
	}

	/** 
	 * 将字符串形式的ip地址转换为BigInteger 
	 *  
	 * @param ipInString  字符串形式的ip地址 
	 * @return 整数形式的ip地址 
	 */
	public static BigInteger stringToBigInt(String ipInString) {
		ipInString = ipInString.replace(" ", "");
		byte[] bytes;
		if (ipInString.contains(":")) {
			bytes = ipv6ToBytes(ipInString);
		} else {
			bytes = ipv4ToBytes(ipInString);
		}

		return new BigInteger(bytes);
	}

	/**
	 * 获取DNS
	 * 
	 * @param context
	 * @return
	 */
	public static String getDns(Context context) {
		if (context == null) {
			return "";
		}

		WifiManager my_wifiManager = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE));
		if (my_wifiManager == null) {
			return null;
		}
		DhcpInfo dhcpInfo = my_wifiManager.getDhcpInfo();

		if (dhcpInfo == null) {
			return null;
		}
		return intToIp(dhcpInfo.dns1);
	}

	/**
	 * 网络是否可用
	 * 
	 * @return
	 */
	public static boolean isAvailable(Context context) {
		if (context == null) {
			return false;
		}
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connMgr == null) {
			return false;
		}
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

		if (null == netInfo) {
			return false;
		}

		if (netInfo.isAvailable() && netInfo.isConnected()) {
			return true;
		}

		return false;
	}

	/**
	 * 网络是否可用
	 * 
	 * @param netInfo
	 * @return
	 */
	public static boolean isAvailable(NetworkInfo netInfo) {
		if (null == netInfo) {
			return false;
		}

		if (netInfo.isAvailable() && netInfo.isConnected()) {
			return true;
		}

		return false;
	}

	/**
	 * 是否连接Wifi
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWiFi(Context context) {
		if (context == null) {
			return false;
		}
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connMgr == null) {
			return false;
		}
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

		if (null == netInfo) {
			return false;
		}

		// 网络可用
		if (isAvailable(netInfo)) {
			int type = netInfo.getType(); // 网络类型
			if (ConnectivityManager.TYPE_WIFI == type) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 是否连接手机网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMobile(Context context) {
		if (context == null) {
			return false;
		}
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connMgr == null) {
			return false;
		}
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

		if (null == netInfo) {
			return false;
		}

		// 网络可用
		if (isAvailable(netInfo)) {
			int type = netInfo.getType(); // 网络类型
			if (ConnectivityManager.TYPE_MOBILE == type) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 网络类型
	 * 
	 * @param context
	 * @return
	 */
	public static int getNetworkType(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		return netInfo.getType();
	}

	/**
	 * 网络子类型
	 * 
	 * @param context
	 * @return
	 */
	public static int getNetworkSubType(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		return netInfo.getSubtype();
	}

	/**
	 * 是否连接3G网络
	 * 
	 * <p>
	 * 注意：NETWORK_TYPE_HSPA 和 NETWORK_TYPE_HSUPA 还没有定位是否为联通3G
	 * </p>
	 * @param context
	 * @return NETWORK_TYPE_HSDPA(联通3G) || NETWORK_TYPE_UMTS(联通3G) || NETWORK_TYPE_EVDO_0(电信3G) || NETWORK_TYPE_EVDO_A(电信3G)
	 */
	public static boolean is3G(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

		if (null == netInfo) {
			return false;
		}

		// 网络不可用
		if (!isAvailable(netInfo)) {
			return false;
		}

		// 非手机网络
		if (ConnectivityManager.TYPE_MOBILE != netInfo.getType()) {
			return false;
		}

		// NetworkInfo mMoble =
		// connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		int subType = netInfo.getSubtype();
		if (subType == TelephonyManager.NETWORK_TYPE_HSDPA // 联通3G
				|| subType == TelephonyManager.NETWORK_TYPE_UMTS // 联通3G
				|| subType == TelephonyManager.NETWORK_TYPE_EVDO_0 // 电信3G
				|| subType == TelephonyManager.NETWORK_TYPE_EVDO_A)// 电信3G
		{
			return true;
		}

		return false;
	}

	/**
	 * 是否为联通3G网络
	 * 
	 * @param context
	 * @return NETWORK_TYPE_HSDPA || NETWORK_TYPE_UMTS
	 */
	public static boolean isUnicom3G(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

		if (null == netInfo) {
			return false;
		}

		// 网络不可用
		if (!isAvailable(netInfo)) {
			return false;
		}

		// 非手机网络
		if (ConnectivityManager.TYPE_MOBILE != netInfo.getType()) {
			return false;
		}

		int subType = netInfo.getSubtype();
		if (subType == TelephonyManager.NETWORK_TYPE_HSDPA || subType == TelephonyManager.NETWORK_TYPE_UMTS) { // 联通3G
			return true;
		}

		return false;
	}

	/**
	 * 是否为电信3G网络
	 * 
	 * @param context
	 * @return NETWORK_TYPE_EVDO_0 || NETWORK_TYPE_EVDO_A
	 */
	public static boolean isTelecom3G(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

		if (null == netInfo) {
			return false;
		}

		// 网络不可用
		if (!isAvailable(netInfo)) {
			return false;
		}

		// 非手机网络
		if (ConnectivityManager.TYPE_MOBILE != netInfo.getType()) {
			return false;
		}

		int subType = netInfo.getSubtype();
		if (subType == TelephonyManager.NETWORK_TYPE_EVDO_0 || subType == TelephonyManager.NETWORK_TYPE_EVDO_A) { // 电信3G
			return true;
		}

		return false;
	}

	/**
	 * 是否连接2G网络
	 * 
	 * @param context
	 * @return TelephonyManager.NETWORK_TYPE_GPRS(移动和联通2G) || TelephonyManager.NETWORK_TYPE_CDMA(电信2G) || TelephonyManager.NETWORK_TYPE_EDGE(移动和联通2G)
	 */
	public static boolean is2G(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

		if (null == netInfo) {
			return false;
		}

		// 网络不可用
		if (!isAvailable(netInfo)) {
			return false;
		}

		// 非手机网络
		if (ConnectivityManager.TYPE_MOBILE != netInfo.getType()) {
			return false;
		}

		int subType = netInfo.getSubtype();
		if (subType == TelephonyManager.NETWORK_TYPE_GPRS // 移动和联通2G
				|| subType == TelephonyManager.NETWORK_TYPE_CDMA // 电信2G
				|| subType == TelephonyManager.NETWORK_TYPE_EDGE) // 移动和联通2G
		{
			return true;
		}

		return false;
	}

}
