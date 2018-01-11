package com.pcbwx.shiro.utils;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

/**
 * Session监听器
 * 
 * @author 王海龙
 *
 */
public class SessionListener implements HttpSessionListener {

	private static final Logger logger = Logger
			.getLogger(SessionListener.class);

	/**
	 * Notification that a session was created.
	 * 
	 * @param se
	 *            the notification event
	 */
	public void sessionCreated(HttpSessionEvent httpSessionEvent) {

	}

	/**
	 * Notification that a session is about to be invalidated.
	 * 
	 * @param se
	 *            the notification event
	 */
	public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {

//		HttpSession session = httpSessionEvent.getSession();
//		ApplicationContext applicationContext = WebApplicationContextUtils
//				.getRequiredWebApplicationContext(session.getServletContext());
//		AccountSessionService accountSessionService = applicationContext
//				.getBean(AccountSessionService.class);
//
//		String token = SessionManager.getToken(session);
//		logger.info("销毁Session时,token is {} ", token);
//
//		// 删除account_session对应的记录--根据token来删除
//		int sd = accountSessionService.deletebytoken(token);
//		logger.info("删除了当前用户会话表的记录  {} ", sd);
//
//		
//		// 删除session
//		SessionManager.DelSession(session);
	}

}
