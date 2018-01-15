package com.pcbwx.shiro.utils;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * MD5加密工具类
 * 
 * @author 孙贺宇
 * @version 1.0
 *
 */
public class BASE64MD5Util {

	public static String md5(String str) {
		String md5str = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] input = str.getBytes();
			byte[] buff = md.digest(input);
			md5str = bytesToHex(buff);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return md5str;
	}

	/**
	 * 二进制转十六进制
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(byte[] bytes) {
		StringBuffer md5str = new StringBuffer();
		int digital;
		for (int i = 0; i < bytes.length; i++) {
			digital = bytes[i];
			if (digital < 0) {
				digital += 256;
			}
			if (digital < 16) {
				md5str.append("0");
			}
			md5str.append(Integer.toHexString(digital));
		}
		return md5str.toString();
	}

	public static String md5_16(String str) {
		return md5(str).substring(8, 24);
	}

	public static String base65EnCode(String str) {
		byte[] bytes = str.getBytes();
		String code = Base64.getEncoder().encodeToString(bytes);
		return code;
	}

	public static String base64Decode(String str) {
		byte[] bytes = Base64.getDecoder().decode(str);
		return new String(bytes);
	}

}
