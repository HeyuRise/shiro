/**
 * 
 */
package com.pcbwx.shiro.common;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 非法Token管理类，用于存放被踢走的用户Token
 * 
 * @author 王海龙
 *
 */
public class InvalidTokenManager {
	private static final Logger logger = LogManager.getLogger(InvalidTokenManager.class);

	private static Set<String> tokenSet = new HashSet<String>();

	/**
	 * 添加非法Token
	 * 
	 * @param token
	 */
	public synchronized static void addInvalidToken(String token) {
		logger.info("add Invalid Token:" + token);
		tokenSet.add(token);
	}

	/**
	 * 是否存在非法Token
	 * 
	 * @param token
	 * @return
	 */
	public synchronized static boolean isTokenExist(String token) {
		boolean result = tokenSet.contains(token);

		// 如果存在，移除他
		if (result) {
//			logger.info("Token {} is exist in InvalidTokenSet", token);
			tokenSet.remove(token);
		}
		return result;
	}

}
