package com.pcbwx.shiro.service;

import java.util.List;

import com.pcbwx.shiro.bean.response.ResponseRoute;
/**
 * 路由信息模块接口
 * 
 * @author 孙贺宇
 *
 */
public interface RouteService {
	
	/**
	 * 处理路由推送信息
	 * @param xml
	 * @return
	 */
	Integer operateRoutePush(String xml);
	
	/**
	 * 查询路由信息
	 */
	List<ResponseRoute> goforExpressRoute(Integer type, String mailno);
	/**
	 * 获取推送路由信息
	 * @param mailno
	 * @return
	 */
	List<ResponseRoute> goforRoutePushInfo(String mailno);
}
