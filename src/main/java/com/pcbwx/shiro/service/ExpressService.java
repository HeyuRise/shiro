package com.pcbwx.shiro.service;

import java.util.Map;

import com.pcbwx.shiro.bean.request.Order;
import com.pcbwx.shiro.model.WxtbUser;

/**
 * 下单模块业务接口
 * 
 * @author 孙贺宇
 *
 */
public interface ExpressService {
	/**
	 * 下单
	 */
	Map<String, Object> expressOrder(WxtbUser wxtbUser, Integer companyId, Order order, String orderId);
	/**
	 * 处理二维码中的信息
	 */
	Map<String, Object> operateQRCode(String text);
	
	/**
	 * 子单号申请
	 */
	Integer goforOrderZD(String mailno, Integer number);
	/**
	 * 处理请求超时订单
	 */
	void operateTimeOutExpress();
	/**
	 * 处理订单筛选推送数据
	 * @param xml
	 * @return
	 */
	Integer operateOrderFilterXml(String xml);
}
