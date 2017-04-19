package com.pc.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class ValidateUtils {

	/**
	 * <pre>
	 * [\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?
	 * </pre>
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (StringUtils.isNull(email) || !email.contains("@")) return false;

		if (email.length() <= 22) {
			String checkemail = "^([a-z0-9A-Z]+[_|-|\\.]?){0,}[a-z0-9A-Z]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			return validate(checkemail, email);
		} else {
			String a[] = email.split("@");
			if (a.length == 2) {
				String checkemailleft = "^([a-z0-9A-Z]+[_|-|\\.]?){0,}[a-z0-9A-Z]+$";
				String checkemailright = "^([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
				return validate(checkemailleft, a[0]) && validate(checkemailright, a[1]);
			}
		}
		return false;
	}

	public static boolean isCellphone(String cellphone) {
		String checkcellphone = "^(13[0-9]|15[0-9]|18[0-9])[0-9]{8}$";
		return validate(checkcellphone, cellphone);
	}

	public static boolean isCellphone15(String cellphone) {
		String checkcellphone = "^[+,0-9]{0,4}(13[0-9]|15[0-9]|18[0-9])[0-9]{8}$";
		return validate(checkcellphone, cellphone);
	}

	public static boolean isPhone(String cellphone) {
		String checkcellphone = "^[0-9\\,\\.\\#\\*\\(\\)\\+-\\;\\/\\s]+$";
		return validate(checkcellphone, cellphone);
	}

	public static boolean isE164(String e164) {
		String checkE164 = "^[0-9]{13}$";
		return validate(checkE164, e164);
	}

	public static boolean isNumber(String number) {
		String checkE164 = "^[0-9]*$";
		return validate(checkE164, number);
	}

	public static boolean isHost(String host) {
		String checkHost = "^([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		return validate(checkHost, host);
	}

	public static boolean isIP(String ip) {
		String checkIP = "^((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))$";
		return validate(checkIP, ip);
	}

	public static boolean isUrl(String url) {
		if (null == url || url.length() == 0) return false;

		Pattern patt = Pattern
				.compile(
						"^(http|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$",
						Pattern.CASE_INSENSITIVE);

		Matcher matcher = patt.matcher(url);
		return matcher.matches();
	}

	public static boolean isVersion(String version) {
		String checkVersion = "^([0-9]*\\.)*[0-9]*$";
		return validate(checkVersion, version);
	}

	/***
	 * 判断帐号 $代表结束符
	 * @param account
	 * @return
	 */
	public static boolean isKedacomAccount(String account) {
		if (StringUtils.isNull(account)) {
			return false;
		}
		String check = "^.*@.*\\.kedacom\\.com$";
		String check2 = "^.*@kedacom\\.com$";
		return validate(check, account) || validate(check2, account);
	}

	public static boolean validate(String patternStr, String matcherStr) {
		if (StringUtils.isNull(patternStr) || StringUtils.isNull(matcherStr)) {
			return false;
		}

		try {
			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(matcherStr);
			return matcher.matches();
		} catch (PatternSyntaxException e) {
			return false;
		}
	}

	/**
	 * 判断是否是表情的标志，<\1>或者<\45>,目前数字为0-55
	 * @param num
	 * @return
	 */
	public static boolean isFace(String num) {
		String checkStr = "^\\<\\\\([0-9]|[1-4][0-9]|[5][0-5])\\>$";
		return validate(checkStr, num);
	}

	/**
	 * TL规定，200-500为自定义表情，500+为图片 ;这里统一判断是否是图片的标志，<\200>,<\300>,<\503>目前数字为大于200
	 * @param num
	 * @return
	 */
	public static boolean isPic(String num) {
		String checkStr = "^\\<\\\\([1-9][0-9]{3,}|[2-9][0-9][0-9])\\>$";
		return validate(checkStr, num);
	}

	/**
	 * 自定义字符串是否为vconf p2p record 格式为<\\VConfP2PRecord>......<\\VConfP2PRecord>
	 * 以<\\VConfP2PRecord>开头和结尾
	 * @param num
	 * @return
	 */
	public static boolean isVConfP2PRecord(String str) {
		String checkStr = "^(\\<\\\\VConfP2PRecord\\>).*(<\\\\VConfP2PRecord\\>)$";
		return validate(checkStr, str);
	}

	/**
	 * 电话号码
	 * @param phoneNum
	 * @return
	 */
	public static boolean isPhoneNum(String phoneNum) {
		if (null == phoneNum) return false;

		Pattern patt = Pattern.compile("[0-9]{3,4}//-?[0-9]+");
		Matcher matcher = patt.matcher(phoneNum);
		return matcher.matches();
	}

	/**
	 * 身份证
	 * 
	 * <pre>
	 * 判断一个字符串是不是身份证号码，即是否是15或18位数字。
	 * </pre>
	 * @param identityCard
	 * @return
	 */
	public static boolean isIdentityCard(String identityCard) {
		if (null == identityCard) return false;

		Pattern patt = Pattern.compile("^//d{15}|//d{18}$");
		Matcher matcher = patt.matcher(identityCard);
		return matcher.matches();
	}
}
