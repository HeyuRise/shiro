package com.pcbwx.shiro.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 权限拦截器
 * 
 * @author 王海龙
 * 
 */
public class SessionInterceptor implements HandlerInterceptor {

	private static final Logger logger = LogManager.getLogger(SessionInterceptor.class);
	
	/**
	 * 在controller后拦截
	 */
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object,
			Exception exception) throws Exception {
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object,
			ModelAndView modelAndView) throws Exception {
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object)
			throws Exception {
		String reqUrl = request.getRequestURL().toString();
		String paramStr = request.getQueryString();
		logger.info(request.getMethod() + " >> " + reqUrl + "?" + paramStr);
		return true;		
	}

}
