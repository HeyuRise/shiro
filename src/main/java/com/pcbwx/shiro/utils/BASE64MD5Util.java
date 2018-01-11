package com.pcbwx.shiro.utils;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

/**
 * MD5加密工具类
 * 
 * @author 王海龙
 * @version 1.0
 *
 */
public class BASE64MD5Util {

	// private static final Logger logger = Logger.getLogger(BASE64MD5Util.class);

	/**
	 * false 表示：生成32位的Hex版, 这也是encodeHashAsBase64的, Acegi 默认配置; true
	 * 表示：生成24位的Base64版
	 * 
	 * @param password
	 * @return
	 */
	public static String md5(String str) {
		Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		md5.setEncodeHashAsBase64(false);
		String pwd = md5.encodePassword(str, null);
		return pwd;
	}
	
	public static String md5_16(String str){
		return md5(str).substring(8, 24);
	}

	/**
	 * 哈希算法 256
	 * 
	 * @param password
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String sha_256(String password)
			throws NoSuchAlgorithmException {
		ShaPasswordEncoder sha = new ShaPasswordEncoder(256);
		sha.setEncodeHashAsBase64(true);
		String pwd = sha.encodePassword(password, null);
		return pwd;
	}

	/**
	 * 哈希算法 SHA-256
	 * 
	 * @param password
	 * @return
	 */
	public static String sha_SHA_256(String password) {
		ShaPasswordEncoder sha = new ShaPasswordEncoder();
		sha.setEncodeHashAsBase64(false);
		String pwd = sha.encodePassword(password, null);
		return pwd;
	}

	/**
	 * 使用动态加密盐的只需要在注册用户的时候将第二个参数换成用户名即可
	 * @param username
	 * @param password
	 * @return
	 */
	public static String md5_SystemWideSaltSource(String username,
			String password) {
		Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		md5.setEncodeHashAsBase64(false);
		String pwd = md5.encodePassword(password, username);
		return pwd;
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
