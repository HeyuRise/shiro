/**
 * 
 */
package com.pcbwx.shiro.utils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Request工具类
 * 
 * @author 王海龙
 *
 */
public class RequestUtil {

	/**
	 * 获取Request Path中的多个id值
	 * 
	 * @param request
	 *            Http Request对象
	 * @return id 数组
	 */
	static public List<Integer> getPathIds(HttpServletRequest request) {
		String pattern = (String) request
				.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String idString = new AntPathMatcher().extractPathWithinPattern(
				pattern, request.getServletPath());
		String[] idStringArray = StringUtils.split(idString, '/');
//		int[] idArray = new int[idStringArray.length];
		List<Integer> idArray = new ArrayList<Integer>();
		for (int i = 0; i < idStringArray.length; i++) {
//			idArray[i] = Integer.parseInt(idStringArray[i]);
			idArray.add(Integer.parseInt(idStringArray[i]));
		}
		return idArray;
	}

}
