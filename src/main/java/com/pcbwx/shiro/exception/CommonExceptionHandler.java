/**
 * 
 */
package com.pcbwx.shiro.exception;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.pcbwx.shiro.component.LogContext;
import com.pcbwx.shiro.enums.MessageEnum;

/**
 * 全局异常处理类
 * 
 * @author 王海龙
 *
 */
//@Component
public class CommonExceptionHandler implements HandlerExceptionResolver {
	private static final Logger logger = Logger
			.getLogger(CommonExceptionHandler.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {

		try {
			response.reset();
			response.setContentType("text/html;charset=UTF-8");
//			PrintWriter writer = response.getWriter();
			if (ex instanceof UnAuthenticatedException
					|| ex instanceof NoAuthorityException
					|| ex instanceof TimeoutException
					|| ex instanceof ServiceException) {
//				logger.warn(message, t);("hander Exception:{}, Message is {}",
//						ex.getClass(), ex.getMessage());
				LogContext.error("系统访问错误", ex.getMessage());

				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//				writer.print(ex.getMessage());

			}else if(ex instanceof SystemShutDownException){
				
//				logger.warn("hander Exception:{}, Message is {}",
//						ex.getClass(), ex.getMessage());
				LogContext.error("系统访问错误", ex.getMessage());
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//				writer.print(ex.getMessage());
			}else {
				// 其他异常，返回500状态码
				logger.error(ex.getMessage(), ex);
				LogContext.error("系统访问错误", ex.getMessage());
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//				writer.print("System error");
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			LogContext.error("系统访问错误", e.getMessage());
//			return null;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		String resultInfo = "您好，系统正在维护中，请稍后再试！";
		map.put("type", MessageEnum.WARNING.getCode());
		map.put("result", resultInfo);
		
//		return new ModelAndView();
		return new ModelAndView("redirect:/404.html", map);
	}

}
