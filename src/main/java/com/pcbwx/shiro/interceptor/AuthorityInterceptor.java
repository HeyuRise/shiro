package com.pcbwx.shiro.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.pcbwx.shiro.annotation.RequireAuthority;
import com.pcbwx.shiro.common.InvalidTokenManager;
import com.pcbwx.shiro.enums.AuthorityEnum;
import com.pcbwx.shiro.exception.TimeoutException;
import com.pcbwx.shiro.exception.UnAuthenticatedException;
import com.pcbwx.shiro.utils.SessionManager;

/**
 * 权限拦截器，用于判断用户是否已登录或用户具有操作资源的权限
 * 
 * @author 王海龙
 *
 */
public class AuthorityInterceptor extends HandlerInterceptorAdapter implements
		HandlerInterceptor {

	private static final Logger logger = LogManager.getLogger(AuthorityInterceptor.class);

//	@Autowired
//	private PersonService PersonService;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
	
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		String token = request.getHeader("token");
		logger.info("请求的token是 : " + token);
	
		// 判断用户是否已登录，如果没有登录，抛出UnAuthenticatedException
		if (StringUtils.isBlank(token)) {
			throw new UnAuthenticatedException("Login");
		}
   
		// 客户端的请求，模拟登录
		if (token.startsWith("ipad") || token.startsWith("android")) {
			HttpSession session = request.getSession();
			session.setAttribute("token", token);
//			Person person = new Person();
//			person.setAccount(token);
//			person.setAuthorities(Arrays.asList(
//					AuthorityEnum.ROLE_BOOKING_EDIT.getCode(),
//					AuthorityEnum.ROLE_BOOKING_SUPPORT.getCode(),
//					AuthorityEnum.ROLE_BOOKING_VIEW.getCode()));
//			session.setAttribute("person", person);
//			SessionManager.AddSession(session);
		}

		HttpSession session = SessionManager.getSession(token);

		if (session == null) {
			if(InvalidTokenManager.isTokenExist(token)){
				throw new TimeoutException("ReLogin");
			}
			throw new TimeoutException("outTime");
		}

//		Person person = (Person) session.getAttribute("person");
//		logger.info("用户是= {} ", person.getAccount());

		// 取得方法上的RequireAuthority
		// Annotation的值，断定用户是否已授权，如果没有权限，抛出NoAuthorityException
		Method method = handlerMethod.getMethod();
		if (method.isAnnotationPresent(RequireAuthority.class)) {
			RequireAuthority requireAuthority = method
					.getAnnotation(RequireAuthority.class);
			AuthorityEnum[] values = requireAuthority.value();
			logger.info("方法上拿到权限的个数 " + values.length);
			logger.info("权限 " + values[0].getCode());

//			for (AuthorityEnum authorityEnum : values) {
//				if (person.getAuthorities().contains(authorityEnum.getCode())) {
//					return true;
//				}
//			}
//			throw new NoAuthorityException("No Authority");
		}
		return true;
	}
}
