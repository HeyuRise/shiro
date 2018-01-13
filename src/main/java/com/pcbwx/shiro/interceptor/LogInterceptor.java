package com.pcbwx.shiro.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.pcbwx.shiro.component.LogContext;
import com.pcbwx.shiro.model.Log;
import com.pcbwx.shiro.service.LogService;

/**
 * 记录日志的拦截器
 * 
 * @author 王海龙
 *
 */
public class LogInterceptor extends HandlerInterceptorAdapter implements
		HandlerInterceptor {

	private static final Logger logger = LogManager.getLogger(LogInterceptor.class);

	@Autowired
	private LogService logService; // 日志业务接口

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		Log log = LogContext.get();

//		if (log != null) {
//			try {
//				log.setIp(request.getRemoteAddr());
//				log.setTime(new Date());
//				String account = log.getUser();
//				if (StringUtils.isBlank(account)) {
//					HttpSession session = request.getSession(false);
//					if (session != null
//							&& session.getAttribute("token") != null) { // Web登录成功后
////						Person person = (Person) session.getAttribute("person");
////						account = person.getAccount();
////						log.setUser(account);
//					}
//				}
//
//				if (StringUtils.isNotBlank(account)) {// 未登录用户不记日志
//					logger.debug(JsonUtil.obj2json(log));
//					logService.addLog(log);
//				}
//
//			} catch (Exception e) {
//				logger.error("add log exception", e);
//			} finally {
//				LogContext.clear();
//			}
//
//		}
	}

}
