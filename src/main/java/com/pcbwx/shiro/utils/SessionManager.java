package com.pcbwx.shiro.utils;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

/**
 * Session管理器
 * 
 * @author 王海龙
 *
 */
@SuppressWarnings("all")
public class SessionManager {

	private static HashMap map = new HashMap();

	/**
	 * 添加Session
	 * 
	 * @param session
	 *            待添加的Session
	 */
	public static synchronized void AddSession(HttpSession session) {
		if (session != null) {
			map.put(getToken(session), session);
		}
	}

	/**
	 * 删除Session
	 * 
	 * @param session
	 *            待删除的Session
	 */
	public static synchronized void DelSession(HttpSession session) {
		if (session != null) {
			map.remove(getToken(session));
		}
	}

	/**
	 * 获取Session
	 * 
	 * @param token
	 * @return 对应Token的Session
	 */
	public static synchronized HttpSession getSession(String token) {
		if (token == null) {
			return null;
		}
		return (HttpSession) map.get(token);
	}

	/**
	 * 获取Token值
	 * 
	 * @param session
	 * @return session中的Token属性值
	 */
	public static String getToken(HttpSession session) {
		return (String) session.getAttribute("token");
	}
}
