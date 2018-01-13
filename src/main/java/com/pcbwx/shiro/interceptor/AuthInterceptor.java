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
public class AuthInterceptor implements HandlerInterceptor {

	private static final Logger logger = LogManager.getLogger(AuthInterceptor.class);

	/**
	 * 在controller后拦截
	 */
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object,
			Exception exception) throws Exception {
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object,
			ModelAndView modelAndView) throws Exception {
	}

	/**
	 * 在controller前拦截
	 */
	@SuppressWarnings("deprecation")
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object)
			throws Exception {
//		String redictUrl = request.getRequestURL().toString() + "?s=1";
//		String s = request.getParameter("s");
//		String search = request.getParameter("search");
//		String openId = request.getParameter("openId");
		
		return true;
	}

}
