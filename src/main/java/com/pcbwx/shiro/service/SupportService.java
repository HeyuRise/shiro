package com.pcbwx.shiro.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pcbwx.shiro.enums.ClickEnum;
import com.pcbwx.shiro.model.Address;


/**
 * 工具模块业务接口
 * 
 * @author 王海龙
 *
 */
public interface SupportService {
	/**
	 * 初始化内存数据
	 * @return
	 */
	boolean doReloadCache();
	
	/**
	 * 获取查询路由的xml
	 * @param type
	 * @param mailNo
	 * @return
	 */
	String getQueryRouteXML(Integer type, String mailNo);
	
	/**
	 * 产生订单号
	 * @param now
	 * @return
	 */
	String getOrderId();
	
	/**
	 * 获取成功的xml
	 * @param service
	 * @return
	 */
	String getOkXML(String service);
	
	/**
	 * 获取失败的xml
	 * @param service
	 * @return
	 */
	String getErrorXML(String service);
	
	/**
	 * 更新行政区列表
	 */
	void reloadInternetAddress();
	
	/**
	 * 获取行政区map
	 * @param records
	 * @return
	 */
	Map<String, Map<String, Set<String>>> getAddressMap(List<Address> records);
	
	/**
	 * 获取用户的全部权限id集合
	 * @param account
	 * @return
	 */
	Set<Integer> getUserAuths(String account);
	
	/**
	 * 查看是否可点击
	 * @param account
	 * @param clickEnum
	 * @return
	 */
	Integer ebableClick(String account, ClickEnum clickEnum);
}
