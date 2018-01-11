package com.pcbwx.shiro.interceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.pcbwx.shiro.service.AccountService;

/**
 * 权限拦截器
 * 
 * @author 王海龙
 * 
 */
public class SessionInterceptor implements HandlerInterceptor {

	private static final Logger logger = Logger.getLogger(SessionInterceptor.class);

	@Autowired
	private AccountService accountService;
	
	/**
	 * 在controller后拦截
	 */
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object,
			Exception exception) throws Exception {
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object,
			ModelAndView modelAndView) throws Exception {
	}

	private String filterUrlParams(Map map, String exception) {
		if (map == null || map.isEmpty()) {
			return "";
		}
		String[] exArray = exception.split(";");
		List<String> exList = Arrays.asList(exArray);
		
		StringBuilder sb = new StringBuilder();
		Set<String> keySet = map.keySet();	
		boolean isFirst = true;
		for (String key : keySet) {  
			if (exList.contains(key)) {
				continue;
			}
			String[] values = (String[]) map.get(key);  
	        for (String value : values) {    
		        if (isFirst = true) {
		        	isFirst = false;
		        } else {
		        	sb.append("&");
		        }
	            sb.append(key).append("=").append(value);
	        } 
		}
		return sb.toString();
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object)
			throws Exception {
		String token = request.getParameter("token");
		String contactNo = request.getParameter("contactNo");
//		String s = request.getParameter("s");
		logger.info("token >> " + token);
		String reqUrl = request.getRequestURL().toString();
		String paramStr = request.getQueryString();
		logger.info(request.getMethod() + " >> " + reqUrl + "?" + paramStr);
		
		if (/*StringUtils.isBlank(s) &&*/ !StringUtils.isBlank(token)) {
//			String userNo = accountService.getUserByToken(token);
//			if (!StringUtils.isBlank(userNo)) {		
//				logger.info("获取userNo=" + userNo + ",token >> " + token);
//				request.setAttribute("userNo", userNo);
//			}
		} else if (!StringUtils.isBlank(contactNo)) {
			request.setAttribute("userNo", contactNo);
		}
		
		return true;		
	}

}
